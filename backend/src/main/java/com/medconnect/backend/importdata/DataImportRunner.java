package com.medconnect.backend.importdata;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medconnect.backend.entity.Inventory;
import com.medconnect.backend.entity.Medicine;
import com.medconnect.backend.entity.Pharmacy;
import com.medconnect.backend.repository.InventoryRepository;
import com.medconnect.backend.repository.MedicineRepository;
import com.medconnect.backend.repository.PharmacyRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataImportRunner implements CommandLineRunner {

    private final PharmacyRepository pharmacyRepository;
    private final MedicineRepository medicineRepository;
    private final InventoryRepository inventoryRepository;
    private final ObjectMapper objectMapper;

    @Value("${app.import.enabled:false}")
    private boolean importEnabled;

    @Value("${app.import.source-file:classpath:med_data.json}")
    private Resource sourceFile;

    @Override
    @Transactional
    public void run(String... args) {
        if (!importEnabled) {
            log.info("Data import is disabled. Set app.import.enabled=true to enable.");
            return;
        }

        if (pharmacyRepository.countActivePharmacies() > 0) {
            log.info("Database already contains pharmacies. Skipping import.");
            return;
        }

        log.info("Starting data import from {}", sourceFile.getFilename());
        importData();
        log.info("Data import completed successfully.");
    }

    private void importData() {
        try {
            JsonNode rootNode = objectMapper.readTree(sourceFile.getInputStream());
            
            Map<String, Medicine> medicineCache = new HashMap<>();
            int pharmacyCount = 0;
            int medicineCount = 0;
            int inventoryCount = 0;
            int failedCount = 0;

            for (JsonNode pharmacyNode : rootNode) {
                try {
                    Pharmacy pharmacy = parsePharmacy(pharmacyNode);
                    pharmacy = pharmacyRepository.save(pharmacy);
                    pharmacyCount++;

                    String medicinesStr = pharmacyNode.get("medicines").asText();
                    String[] medicineNames = medicinesStr.split(",");

                    for (String medName : medicineNames) {
                        medName = medName.trim();
                        if (medName.isEmpty()) continue;

                        try {
                            ParsedMedicine parsed = parseMedicineName(medName);
                            String medicineKey = buildMedicineKey(parsed);

                            Medicine medicine = medicineCache.get(medicineKey);
                            if (medicine == null) {
                                medicine = findOrCreateMedicine(parsed);
                                medicineCache.put(medicineKey, medicine);
                                medicineCount++;
                            }

                            // Create inventory entry
                            Inventory inventory = Inventory.builder()
                                    .pharmacy(pharmacy)
                                    .medicine(medicine)
                                    .availabilityStatus(Inventory.AvailabilityStatus.UNVERIFIED)
                                    .dataSource("IMPORT")
                                    .lastUpdatedAt(java.time.LocalDateTime.now())
                                    .build();

                            inventoryRepository.save(inventory);
                            inventoryCount++;

                        } catch (Exception e) {
                            log.warn("Failed to process medicine '{}' for pharmacy '{}': {}", 
                                    medName, pharmacy.getName(), e.getMessage());
                            failedCount++;
                        }
                    }

                } catch (Exception e) {
                    log.error("Failed to process pharmacy: {}", e.getMessage(), e);
                    failedCount++;
                }
            }

            log.info("Import summary: {} pharmacies, {} medicines, {} inventory entries, {} failures", 
                    pharmacyCount, medicineCount, inventoryCount, failedCount);

        } catch (IOException e) {
            log.error("Failed to read source file: {}", e.getMessage(), e);
            throw new RuntimeException("Data import failed", e);
        }
    }

    private Pharmacy parsePharmacy(JsonNode node) {
        // Handle latitude that might be a string with trailing comma
        double latitude = parseCoordinate(node.get("latitude"));
        double longitude = parseCoordinate(node.get("longitude"));

        return Pharmacy.builder()
                .name(node.get("store_name").asText())
                .address(node.get("address").asText())
                .latitude(latitude)
                .longitude(longitude)
                .phoneNumber(getTextOrNull(node, "contact_number"))
                .email(getTextOrNull(node, "email"))
                .website(getTextOrNull(node, "website"))
                .pinCode(getTextOrNull(node, "pin_code"))
                .plusCode(getTextOrNull(node, "plus_code"))
                .ownerName(getTextOrNull(node, "owner_name"))
                .isActive(true)
                .sourceData(node.toString())
                .build();
    }

    private double parseCoordinate(JsonNode node) {
        if (node == null || node.isNull()) {
            throw new IllegalArgumentException("Coordinate is null");
        }
        if (node.isNumber()) {
            return node.asDouble();
        }
        if (node.isTextual()) {
            String text = node.asText().trim().replaceAll("[,\\s]+$", "");
            return Double.parseDouble(text);
        }
        throw new IllegalArgumentException("Invalid coordinate format: " + node);
    }

    private String getTextOrNull(JsonNode node, String fieldName) {
        JsonNode field = node.get(fieldName);
        return (field != null && !field.isNull() && !field.asText().isBlank()) ? field.asText() : null;
    }

    private ParsedMedicine parseMedicineName(String name) {
        String normalizedName = name;
        String brandName = null;
        String strength = null;
        String dosageForm = null;
        String genericName = null;

        // Extract strength
        Pattern strengthPattern = Pattern.compile("(\\d+(?:\\.\\d+)?\\s*(?:mg|ml|g|mcg|IU)(?:/\\d+(?:\\.\\d+)?\\s*(?:mg|ml|g|mcg|IU))?)", Pattern.CASE_INSENSITIVE);
        Matcher strengthMatcher = strengthPattern.matcher(name);
        if (strengthMatcher.find()) {
            strength = strengthMatcher.group(1).trim();
            normalizedName = name.substring(0, strengthMatcher.start()).trim();
        }

        // Extract dosage form
        String[] dosageForms = {"Tablet", "Tablets", "Tab", "Tabs", "Capsule", "Capsules", "Cap", "Caps",
            "Syrup", "Suspension", "Susp", "Injection", "Inj", "IV", "IM", "SC",
            "Cream", "Ointment", "Gel", "Lotion", "Drops", "Drop", "Eye Drops", "Ear Drops",
            "Inhaler", "Rotacap", "Respule", "Nebulizer", "Patch", "Spray", "Powder",
            "Granules", "Sachet", "Lozenge", "Suppository", "Pessary", "Film"};

        for (String form : dosageForms) {
            Pattern formPattern = Pattern.compile("\\b" + Pattern.quote(form) + "\\b", Pattern.CASE_INSENSITIVE);
            Matcher formMatcher = formPattern.matcher(normalizedName);
            if (formMatcher.find()) {
                dosageForm = form;
                normalizedName = normalizedName.substring(0, formMatcher.start()).trim();
                break;
            }
        }

        normalizedName = normalizedName.replaceAll("\\s+", " ").trim();

        // Brand to generic mapping
        Map<String, String> brandToGeneric = Map.ofEntries(
            Map.entry("Dolo", "Paracetamol"), Map.entry("Calpol", "Paracetamol"), Map.entry("Crocin", "Paracetamol"),
            Map.entry("Combiflam", "Ibuprofen + Paracetamol"), Map.entry("Meftal", "Mefenamic Acid"),
            Map.entry("Allegra", "Fexofenadine"), Map.entry("Alerid", "Cetirizine"), Map.entry("Cetrine", "Cetirizine"),
            Map.entry("Omez", "Omeprazole"), Map.entry("Pan 40", "Pantoprazole"), Map.entry("Nexpro", "Esomeprazole"),
            Map.entry("Rantac", "Ranitidine"), Map.entry("Zantac", "Ranitidine"), Map.entry("Azithral", "Azithromycin"),
            Map.entry("Azax", "Azithromycin"), Map.entry("Taxim-O", "Cefixime"), Map.entry("Zifi", "Cefixime"),
            Map.entry("Moxikind-CV", "Amoxicillin + Clavulanic Acid"), Map.entry("Augmentin", "Amoxicillin + Clavulanic Acid"),
            Map.entry("Clavam", "Amoxicillin + Clavulanic Acid"), Map.entry("Ciplox", "Ciprofloxacin"),
            Map.entry("Levoflox", "Levofloxacin"), Map.entry("Norflox", "Norfloxacin"), Map.entry("Ofloxacin", "Ofloxacin"),
            Map.entry("Metrogyl", "Metronidazole"), Map.entry("Flagyl", "Metronidazole"), Map.entry("Tinidazole", "Tinidazole"),
            Map.entry("Glycomet", "Metformin"), Map.entry("Gluconorm", "Metformin"), Map.entry("Glucophage", "Metformin"),
            Map.entry("Janumet", "Sitagliptin + Metformin"), Map.entry("Jalra", "Vildagliptin"),
            Map.entry("Diamicron", "Gliclazide"), Map.entry("Amaryl", "Glimepiride"), Map.entry("Zoryl", "Glimepiride"),
            Map.entry("Teneligliptin", "Teneligliptin"), Map.entry("Sitagliptin", "Sitagliptin"), Map.entry("Voglibose", "Voglibose"),
            Map.entry("Pioglitazone", "Pioglitazone"), Map.entry("Ecosprin", "Aspirin"), Map.entry("Clopidogrel", "Clopidogrel"),
            Map.entry("Shelcal", "Calcium + Vitamin D3"), Map.entry("Zincovit", "Multivitamin + Zinc"),
            Map.entry("Dexorange", "Iron + Folic Acid + Vitamin B12"), Map.entry("Becosules", "Vitamin B Complex"),
            Map.entry("Neurobion Forte", "Vitamin B1 + B6 + B12"), Map.entry("Revital", "Multivitamin + Minerals + Ginseng"),
            Map.entry("Liv52", "Herbal Liver Tonic"), Map.entry("Himalaya Cystone", "Herbal Urinary Tonic"),
            Map.entry("Limcee", "Vitamin C"), Map.entry("Sinerga", "Multivitamin"), Map.entry("Ferritas", "Iron"),
            Map.entry("Astymin", "Amino Acids"), Map.entry("A to Z Gold", "Multivitamin + Minerals"),
            Map.entry("Supradyn", "Multivitamin + Minerals"), Map.entry("Folvite", "Folic Acid"),
            Map.entry("Cobadex CZS", "Vitamin B Complex + Zinc"), Map.entry("Optineuron", "Mecobalamin + Vitamins"),
            Map.entry("Maxirich", "Multivitamin + Minerals"), Map.entry("Becosule Z", "Vitamin B Complex + Zinc"),
            Map.entry("Bplex", "Vitamin B Complex"), Map.entry("Volini", "Diclofenac"), Map.entry("Moov", "Diclofenac"),
            Map.entry("Ibugesic", "Ibuprofen"), Map.entry("Spasmonil", "Dicyclomine + Paracetamol"),
            Map.entry("Cyclopam", "Dicyclomine + Paracetamol"), Map.entry("Normaxin", "Dicyclomine + Chlordiazepoxide"),
            Map.entry("Digene", "Antacid"), Map.entry("Gelusil", "Antacid"), Map.entry("Mucaine", "Antacid + Oxetacaine"),
            Map.entry("Ulgel", "Antacid"), Map.entry("Lomotil", "Diphenoxylate + Atropine"), Map.entry("Otrivin", "Xylometazoline"),
            Map.entry("Sinarest", "Paracetamol + Chlorpheniramine + Phenylephrine"), Map.entry("Benadryl", "Diphenhydramine"),
            Map.entry("Cetirizine", "Cetirizine"), Map.entry("Amoxicillin", "Amoxicillin"),
            Map.entry("Azithromycin", "Azithromycin"), Map.entry("Doxycycline", "Doxycycline"),
            Map.entry("Erythromycin", "Erythromycin"), Map.entry("Cefixime", "Cefixime"),
            Map.entry("Amikacin", "Amikacin"), Map.entry("Linezolid", "Linezolid"), Map.entry("Targocid", "Teicoplanin"),
            Map.entry("Rabeprazole", "Rabeprazole"), Map.entry("Lanzol", "Lansoprazole"), Map.entry("Esomac", "Esomeprazole"),
            Map.entry("Aciloc", "Ranitidine"), Map.entry("Itopride", "Itopride"), Map.entry("Domperidone", "Domperidone"),
            Map.entry("Ondansetron", "Ondansetron"), Map.entry("Tazloc", "Telmisartan"), Map.entry("Aztreonam", "Aztreonam"),
            Map.entry("Losartan", "Losartan"), Map.entry("Amlodipine", "Amlodipine"), Map.entry("Telmisartan", "Telmisartan"),
            Map.entry("Rosuvastatin", "Rosuvastatin"), Map.entry("Atorvastatin", "Atorvastatin"),
            Map.entry("Metformin", "Metformin"), Map.entry("Human Mixtard", "Human Insulin Mix"),
            Map.entry("Insuman", "Human Insulin"), Map.entry("Ciprofloxacin", "Ciprofloxacin")
        );

        genericName = brandToGeneric.getOrDefault(normalizedName, normalizedName);

        return new ParsedMedicine(normalizedName, brandName, strength, dosageForm, genericName);
    }

    private String buildMedicineKey(ParsedMedicine parsed) {
        return parsed.normalizedName() + "|" + 
                (parsed.strength() != null ? parsed.strength() : "") + "|" + 
                (parsed.dosageForm() != null ? parsed.dosageForm() : "");
    }

    private Medicine findOrCreateMedicine(ParsedMedicine parsed) {
        return medicineRepository
                .findByNormalizedNameIgnoreCaseAndStrengthIgnoreCaseAndDosageFormIgnoreCase(
                        parsed.normalizedName(), parsed.strength(), parsed.dosageForm())
                .orElseGet(() -> {
                    Medicine medicine = Medicine.builder()
                            .normalizedName(parsed.normalizedName())
                            .brandName(parsed.brandName())
                            .strength(parsed.strength())
                            .dosageForm(parsed.dosageForm())
                            .genericName(parsed.genericName())
                            .isActive(true)
                            .build();
                    return medicineRepository.save(medicine);
                });
    }

    private record ParsedMedicine(
        String normalizedName, String brandName, String strength, String dosageForm, String genericName
    ) {}
}