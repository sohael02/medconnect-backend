package com.medconnect.backend.service;

import com.medconnect.backend.dto.PharmacyRequestDTO;
import com.medconnect.backend.dto.PharmacyResponseDTO;
import com.medconnect.backend.entity.Pharmacy;
import com.medconnect.backend.exception.DuplicateResourceException;
import com.medconnect.backend.exception.ResourceNotFoundException;
import com.medconnect.backend.mapper.PharmacyMapper;
import com.medconnect.backend.repository.PharmacyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PharmacyService {

    private final PharmacyRepository pharmacyRepository;
    private final PharmacyMapper pharmacyMapper;

    public PharmacyResponseDTO getById(Long id) {
        Pharmacy pharmacy = pharmacyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pharmacy", "id", id));
        return pharmacyMapper.toResponseDTO(pharmacy);
    }

    public PharmacyResponseDTO getByIdBasic(Long id) {
        Pharmacy pharmacy = pharmacyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pharmacy", "id", id));
        return pharmacyMapper.toResponseDTOBasic(pharmacy);
    }

    public Page<PharmacyResponseDTO> getAllActive(Pageable pageable) {
        return pharmacyRepository.findByIsActiveTrue(pageable)
                .map(pharmacyMapper::toResponseDTOBasic);
    }

    public List<PharmacyResponseDTO> getAllActiveList() {
        return pharmacyRepository.findByIsActiveTrue().stream()
                .map(pharmacyMapper::toResponseDTOBasic)
                .toList();
    }

    public List<PharmacyResponseDTO> searchByName(String name) {
        return pharmacyRepository.findByNameContainingIgnoreCase(name).stream()
                .map(pharmacyMapper::toResponseDTOBasic)
                .toList();
    }

    public List<PharmacyResponseDTO> findByBoundingBox(Double minLat, Double maxLat, Double minLng, Double maxLng) {
        return pharmacyRepository.findByBoundingBox(minLat, maxLat, minLng, maxLng).stream()
                .map(pharmacyMapper::toResponseDTOBasic)
                .toList();
    }

    @Transactional
    public PharmacyResponseDTO create(PharmacyRequestDTO dto) {
        if (pharmacyRepository.findByNameIgnoreCase(dto.getName()).isPresent()) {
            throw new DuplicateResourceException("Pharmacy", "name", dto.getName());
        }
        Pharmacy pharmacy = pharmacyMapper.toEntity(dto);
        pharmacy = pharmacyRepository.save(pharmacy);
        log.info("Created pharmacy: {} (id={})", pharmacy.getName(), pharmacy.getId());
        return pharmacyMapper.toResponseDTO(pharmacy);
    }

    @Transactional
    public PharmacyResponseDTO update(Long id, PharmacyRequestDTO dto) {
        Pharmacy pharmacy = pharmacyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pharmacy", "id", id));
        
        // Check for duplicate name (excluding current pharmacy)
        pharmacyRepository.findByNameIgnoreCase(dto.getName())
                .filter(p -> !p.getId().equals(id))
                .ifPresent(p -> {
                    throw new DuplicateResourceException("Pharmacy", "name", dto.getName());
                });

        pharmacyMapper.updateEntityFromDto(dto, pharmacy);
        pharmacy = pharmacyRepository.save(pharmacy);
        log.info("Updated pharmacy: {} (id={})", pharmacy.getName(), pharmacy.getId());
        return pharmacyMapper.toResponseDTO(pharmacy);
    }

    @Transactional
    public void delete(Long id) {
        Pharmacy pharmacy = pharmacyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pharmacy", "id", id));
        pharmacyRepository.delete(pharmacy);
        log.info("Deleted pharmacy: {} (id={})", pharmacy.getName(), pharmacy.getId());
    }

    @Transactional
    public PharmacyResponseDTO toggleActive(Long id) {
        Pharmacy pharmacy = pharmacyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pharmacy", "id", id));
        pharmacy.setIsActive(!pharmacy.getIsActive());
        pharmacy = pharmacyRepository.save(pharmacy);
        log.info("Toggled pharmacy active status: {} (id={}) -> {}", pharmacy.getName(), pharmacy.getId(), pharmacy.getIsActive());
        return pharmacyMapper.toResponseDTO(pharmacy);
    }

    public long countActive() {
        return pharmacyRepository.countActivePharmacies();
    }

    public Pharmacy findEntityById(Long id) {
        return pharmacyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pharmacy", "id", id));
    }

    public boolean existsById(Long id) {
        return pharmacyRepository.existsById(id);
    }
}