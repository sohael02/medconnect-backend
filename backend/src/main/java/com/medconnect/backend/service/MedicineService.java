package com.medconnect.backend.service;

import com.medconnect.backend.dto.MedicineRequestDTO;
import com.medconnect.backend.dto.MedicineResponseDTO;
import com.medconnect.backend.entity.Medicine;
import com.medconnect.backend.exception.DuplicateResourceException;
import com.medconnect.backend.exception.ResourceNotFoundException;
import com.medconnect.backend.mapper.MedicineMapper;
import com.medconnect.backend.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MedicineService {

    private final MedicineRepository medicineRepository;
    private final MedicineMapper medicineMapper;

    public MedicineResponseDTO getById(Long id) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine", "id", id));
        return medicineMapper.toResponseDTO(medicine);
    }

    public MedicineResponseDTO getByIdBasic(Long id) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine", "id", id));
        return medicineMapper.toResponseDTOBasic(medicine);
    }

    public Page<MedicineResponseDTO> getAllActive(Pageable pageable) {
        return medicineRepository.findByIsActiveTrue(pageable)
                .map(medicineMapper::toResponseDTOBasic);
    }

    public List<MedicineResponseDTO> getAllActiveList() {
        return medicineRepository.findByIsActiveTrue().stream()
                .map(medicineMapper::toResponseDTOBasic)
                .toList();
    }

    public List<MedicineResponseDTO> searchByName(String query) {
        return medicineRepository.searchByQuery(query).stream()
                .map(medicineMapper::toResponseDTOBasic)
                .toList();
    }

    public List<MedicineResponseDTO> searchWithFilters(String query, String strength, String dosageForm) {
        return medicineRepository.searchWithFilters(query, strength, dosageForm).stream()
                .map(medicineMapper::toResponseDTOBasic)
                .toList();
    }

    public Optional<Medicine> findByNormalizedNameAndStrengthAndForm(String normalizedName, String strength, String dosageForm) {
        return medicineRepository.findByNormalizedNameIgnoreCaseAndStrengthIgnoreCaseAndDosageFormIgnoreCase(
                normalizedName, strength, dosageForm);
    }

    @Transactional
    public MedicineResponseDTO create(MedicineRequestDTO dto) {
        Optional<Medicine> existing = medicineRepository
                .findByNormalizedNameIgnoreCaseAndStrengthIgnoreCaseAndDosageFormIgnoreCase(
                        dto.getNormalizedName(), dto.getStrength(), dto.getDosageForm());
        if (existing.isPresent()) {
            throw new DuplicateResourceException("Medicine", "name/strength/form", 
                    dto.getNormalizedName() + " " + dto.getStrength() + " " + dto.getDosageForm());
        }
        Medicine medicine = medicineMapper.toEntity(dto);
        medicine = medicineRepository.save(medicine);
        log.info("Created medicine: {} (id={})", medicine.getDisplayName(), medicine.getId());
        return medicineMapper.toResponseDTO(medicine);
    }

    @Transactional
    public MedicineResponseDTO update(Long id, MedicineRequestDTO dto) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine", "id", id));
        
        // Check for duplicate (excluding current medicine)
        Optional<Medicine> existing = medicineRepository
                .findByNormalizedNameIgnoreCaseAndStrengthIgnoreCaseAndDosageFormIgnoreCase(
                        dto.getNormalizedName(), dto.getStrength(), dto.getDosageForm());
        if (existing.isPresent() && !existing.get().getId().equals(id)) {
            throw new DuplicateResourceException("Medicine", "name/strength/form", 
                    dto.getNormalizedName() + " " + dto.getStrength() + " " + dto.getDosageForm());
        }

        medicineMapper.updateEntityFromDto(dto, medicine);
        medicine = medicineRepository.save(medicine);
        log.info("Updated medicine: {} (id={})", medicine.getDisplayName(), medicine.getId());
        return medicineMapper.toResponseDTO(medicine);
    }

    @Transactional
    public void delete(Long id) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine", "id", id));
        medicineRepository.delete(medicine);
        log.info("Deleted medicine: {} (id={})", medicine.getDisplayName(), medicine.getId());
    }

    @Transactional
    public MedicineResponseDTO toggleActive(Long id) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine", "id", id));
        medicine.setIsActive(!medicine.getIsActive());
        medicine = medicineRepository.save(medicine);
        log.info("Toggled medicine active status: {} (id={}) -> {}", medicine.getDisplayName(), medicine.getId(), medicine.getIsActive());
        return medicineMapper.toResponseDTO(medicine);
    }

    public long countActive() {
        return medicineRepository.countActiveMedicines();
    }

    public Medicine findEntityById(Long id) {
        return medicineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine", "id", id));
    }

    public boolean existsById(Long id) {
        return medicineRepository.existsById(id);
    }

    public Medicine findOrCreate(String normalizedName, String brandName, String strength, String dosageForm) {
        return findByNormalizedNameAndStrengthAndForm(normalizedName, strength, dosageForm)
                .orElseGet(() -> {
                    Medicine medicine = Medicine.builder()
                            .normalizedName(normalizedName)
                            .brandName(brandName)
                            .strength(strength)
                            .dosageForm(dosageForm)
                            .isActive(true)
                            .build();
                    return medicineRepository.save(medicine);
                });
    }
}