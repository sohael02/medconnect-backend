package com.medconnect.backend.service;

import com.medconnect.backend.dto.InventoryRequestDTO;
import com.medconnect.backend.dto.InventorySummaryDTO;
import com.medconnect.backend.entity.Inventory;
import com.medconnect.backend.entity.Medicine;
import com.medconnect.backend.entity.Pharmacy;
import com.medconnect.backend.exception.ResourceNotFoundException;
import com.medconnect.backend.mapper.InventoryMapper;
import com.medconnect.backend.mapper.PharmacyMapper;
import com.medconnect.backend.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final PharmacyService pharmacyService;
    private final MedicineService medicineService;
    private final InventoryMapper inventoryMapper;
    private final PharmacyMapper pharmacyMapper;

    public List<InventorySummaryDTO> getByPharmacy(Long pharmacyId) {
        Pharmacy pharmacy = pharmacyService.findEntityById(pharmacyId);
        return inventoryRepository.findByPharmacyWithMedicine(pharmacy).stream()
                .map(pharmacyMapper::toInventorySummaryDTO)
                .toList();
    }

    public Page<InventorySummaryDTO> getByPharmacyPaged(Long pharmacyId, Pageable pageable) {
        Pharmacy pharmacy = pharmacyService.findEntityById(pharmacyId);
        return inventoryRepository.findByPharmacy(pharmacy, pageable)
                .map(pharmacyMapper::toInventorySummaryDTO);
    }

    public List<InventorySummaryDTO> searchMedicinesInPharmacy(Long pharmacyId, String query) {
        Pharmacy pharmacy = pharmacyService.findEntityById(pharmacyId);
        return inventoryRepository.searchMedicinesInPharmacy(pharmacy, query).stream()
                .map(pharmacyMapper::toInventorySummaryDTO)
                .toList();
    }

    public List<InventorySummaryDTO> getByMedicine(Long medicineId) {
        Medicine medicine = medicineService.findEntityById(medicineId);
        return inventoryRepository.findByMedicineWithPharmacy(medicine, Inventory.AvailabilityStatus.DISCONTINUED).stream()
                .map(pharmacyMapper::toInventorySummaryDTO)
                .toList();
    }

    @Transactional
    public InventorySummaryDTO create(InventoryRequestDTO dto) {
        Pharmacy pharmacy = pharmacyService.findEntityById(dto.getPharmacyId());
        Medicine medicine = medicineService.findEntityById(dto.getMedicineId());

        // Check if inventory already exists
        Optional<Inventory> existing = inventoryRepository.findByPharmacyAndMedicine(pharmacy, medicine);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Inventory entry already exists for this pharmacy and medicine");
        }

        Inventory inventory = inventoryMapper.toEntity(dto);
        inventory.setPharmacy(pharmacy);
        inventory.setMedicine(medicine);
        if (inventory.getLastUpdatedAt() == null) {
            inventory.setLastUpdatedAt(LocalDateTime.now());
        }

        inventory = inventoryRepository.save(inventory);
        log.info("Created inventory: pharmacy={}, medicine={}", pharmacy.getName(), medicine.getDisplayName());
        return pharmacyMapper.toInventorySummaryDTO(inventory);
    }

    @Transactional
    public InventorySummaryDTO update(Long inventoryId, InventoryRequestDTO dto) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "id", inventoryId));

        // If pharmacy or medicine changed, validate
        if (dto.getPharmacyId() != null && !dto.getPharmacyId().equals(inventory.getPharmacy().getId())) {
            Pharmacy pharmacy = pharmacyService.findEntityById(dto.getPharmacyId());
            inventory.setPharmacy(pharmacy);
        }
        if (dto.getMedicineId() != null && !dto.getMedicineId().equals(inventory.getMedicine().getId())) {
            Medicine medicine = medicineService.findEntityById(dto.getMedicineId());
            inventory.setMedicine(medicine);
        }

        inventoryMapper.updateEntityFromDto(dto, inventory);
        if (dto.getLastUpdatedAt() == null) {
            inventory.setLastUpdatedAt(LocalDateTime.now());
        }

        inventory = inventoryRepository.save(inventory);
        log.info("Updated inventory: id={}", inventoryId);
        return pharmacyMapper.toInventorySummaryDTO(inventory);
    }

    @Transactional
    public void delete(Long inventoryId) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "id", inventoryId));
        inventoryRepository.delete(inventory);
        log.info("Deleted inventory: id={}", inventoryId);
    }

    @Transactional
    public InventorySummaryDTO updateAvailability(Long inventoryId, Inventory.AvailabilityStatus status, String notes) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "id", inventoryId));
        inventory.setAvailabilityStatus(status);
        inventory.setLastUpdatedAt(LocalDateTime.now());
        if (notes != null && !notes.isBlank()) {
            inventory.setVerificationNotes(notes);
        }
        inventory = inventoryRepository.save(inventory);
        log.info("Updated inventory availability: id={} -> {}", inventoryId, status);
        return pharmacyMapper.toInventorySummaryDTO(inventory);
    }

    public long countByPharmacy(Long pharmacyId) {
        Pharmacy pharmacy = pharmacyService.findEntityById(pharmacyId);
        return inventoryRepository.countByPharmacy(pharmacy);
    }
}