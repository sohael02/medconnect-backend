package com.medconnect.backend.service;

import com.medconnect.backend.dto.PharmacyResponseDTO;
import com.medconnect.backend.dto.SearchRequestDTO;
import com.medconnect.backend.dto.SearchResponseDTO;
import com.medconnect.backend.entity.Inventory;
import com.medconnect.backend.entity.Medicine;
import com.medconnect.backend.entity.Pharmacy;
import com.medconnect.backend.mapper.PharmacyMapper;
import com.medconnect.backend.repository.InventoryRepository;
import com.medconnect.backend.repository.MedicineRepository;
import com.medconnect.backend.repository.PharmacyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SearchService {

    private final MedicineRepository medicineRepository;
    private final PharmacyRepository pharmacyRepository;
    private final InventoryRepository inventoryRepository;
    private final PharmacyMapper pharmacyMapper;

    @Value("${app.search.default-radius-km:10}")
    private int defaultRadiusKm;

    @Value("${app.search.max-radius-km:50}")
    private int maxRadiusKm;

    @Value("${app.search.max-results:50}")
    private int maxResults;

    public SearchResponseDTO search(SearchRequestDTO request) {
        log.info("Search request: medicine={}, lat={}, lng={}, radius={}km", 
                request.getMedicine(), request.getLatitude(), request.getLongitude(), request.getRadiusKm());

        // Validate and clamp radius
        int radiusKm = Math.min(request.getRadiusKm(), maxRadiusKm);
        
        // Find matching medicines
        List<Medicine> matchingMedicines = findMatchingMedicines(request);
        if (matchingMedicines.isEmpty()) {
            return buildEmptyResponse(request, radiusKm);
        }

        // Get pharmacies within bounding box (rough filter)
        double[] bounds = calculateBoundingBox(request.getLatitude(), request.getLongitude(), radiusKm);
        List<Pharmacy> nearbyPharmacies = pharmacyRepository.findByBoundingBox(
                bounds[0], bounds[1], bounds[2], bounds[3]);

        // Filter by exact distance and get matching inventories
        List<PharmacyResponseDTO> results = new ArrayList<>();
        
        for (Pharmacy pharmacy : nearbyPharmacies) {
            double distance = calculateDistance(
                    request.getLatitude(), request.getLongitude(),
                    pharmacy.getLatitude(), pharmacy.getLongitude());
            
            if (distance <= radiusKm) {
                // Find matching inventories for this pharmacy
                List<Inventory> matchingInventories = inventoryRepository
                        .findByPharmacyIdsAndMedicineQuery(
                                List.of(pharmacy.getId()),
                                request.getMedicine(),
                                request.getVerifiedOnly() ? Inventory.AvailabilityStatus.DISCONTINUED : Inventory.AvailabilityStatus.OUT_OF_STOCK
                        );

                if (!matchingInventories.isEmpty()) {
                    // Filter by strength and dosage form if specified
                    List<Inventory> filteredInventories = matchingInventories.stream()
                            .filter(inv -> matchesStrengthAndForm(inv.getMedicine(), request.getStrength(), request.getDosageForm()))
                            .toList();

                    if (!filteredInventories.isEmpty()) {
                        PharmacyResponseDTO dto = pharmacyMapper.toResponseDTOBasic(pharmacy);
                        dto.setDistanceKm(Math.round(distance * 100.0) / 100.0);
                        dto.setMatchingMedicines(filteredInventories.stream()
                                .map(pharmacyMapper::toInventorySummaryDTO)
                                .toList());
                        results.add(dto);
                    }
                }
            }
        }

        // Sort by distance
        results.sort(Comparator.comparingDouble(PharmacyResponseDTO::getDistanceKm));

        // Apply pagination
        int page = Math.max(1, request.getPage()) - 1; // Convert to 0-based
        int pageSize = Math.min(request.getPageSize(), maxResults);
        int start = page * pageSize;
        int end = Math.min(start + pageSize, results.size());
        
        List<PharmacyResponseDTO> pagedResults = start < results.size() ? results.subList(start, end) : List.of();

        return SearchResponseDTO.builder()
                .pharmacies(pagedResults)
                .totalResults((long) results.size())
                .currentPage(request.getPage())
                .pageSize(request.getPageSize())
                .totalPages((int) Math.ceil((double) results.size() / pageSize))
                .hasNext(end < results.size())
                .hasPrevious(page > 0)
                .query(request.getMedicine())
                .searchLatitude(request.getLatitude())
                .searchLongitude(request.getLongitude())
                .radiusKm(radiusKm)
                .build();
    }

    private List<Medicine> findMatchingMedicines(SearchRequestDTO request) {
        if (request.getStrength() != null && !request.getStrength().isBlank() 
                && request.getDosageForm() != null && !request.getDosageForm().isBlank()) {
            return medicineRepository.searchWithFilters(request.getMedicine(), request.getStrength(), request.getDosageForm());
        } else if (request.getStrength() != null && !request.getStrength().isBlank()) {
            return medicineRepository.searchWithFilters(request.getMedicine(), request.getStrength(), null);
        } else if (request.getDosageForm() != null && !request.getDosageForm().isBlank()) {
            return medicineRepository.searchWithFilters(request.getMedicine(), null, request.getDosageForm());
        } else {
            return medicineRepository.searchByQuery(request.getMedicine());
        }
    }

    private boolean matchesStrengthAndForm(Medicine medicine, String strength, String dosageForm) {
        if (strength != null && !strength.isBlank()) {
            if (medicine.getStrength() == null || !medicine.getStrength().equalsIgnoreCase(strength)) {
                return false;
            }
        }
        if (dosageForm != null && !dosageForm.isBlank()) {
            if (medicine.getDosageForm() == null || !medicine.getDosageForm().equalsIgnoreCase(dosageForm)) {
                return false;
            }
        }
        return true;
    }

    private double[] calculateBoundingBox(double lat, double lng, double radiusKm) {
        // Rough approximation: 1 degree latitude ≈ 111 km
        // 1 degree longitude ≈ 111 km * cos(latitude)
        double latDelta = radiusKm / 111.0;
        double lngDelta = radiusKm / (111.0 * Math.cos(Math.toRadians(lat)));
        
        return new double[]{
                lat - latDelta,  // minLat
                lat + latDelta,  // maxLat
                lng - lngDelta,  // minLng
                lng + lngDelta   // maxLng
        };
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Haversine formula
        final int R = 6371; // Earth radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private SearchResponseDTO buildEmptyResponse(SearchRequestDTO request, int radiusKm) {
        return SearchResponseDTO.builder()
                .pharmacies(List.of())
                .totalResults(0L)
                .currentPage(request.getPage())
                .pageSize(request.getPageSize())
                .totalPages(0)
                .hasNext(false)
                .hasPrevious(false)
                .query(request.getMedicine())
                .searchLatitude(request.getLatitude())
                .searchLongitude(request.getLongitude())
                .radiusKm(radiusKm)
                .build();
    }
}