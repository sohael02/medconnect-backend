package com.medconnect.backend.controller;

import com.medconnect.backend.dto.InventoryRequestDTO;
import com.medconnect.backend.dto.InventorySummaryDTO;
import com.medconnect.backend.entity.Inventory;
import com.medconnect.backend.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventories")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Pharmacy inventory management endpoints")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/pharmacy/{pharmacyId}")
    @Operation(summary = "Get all inventory for a pharmacy")
    public ResponseEntity<List<InventorySummaryDTO>> getPharmacyInventory(
            @Parameter(description = "Pharmacy ID") @PathVariable Long pharmacyId) {
        return ResponseEntity.ok(inventoryService.getByPharmacy(pharmacyId));
    }

    @GetMapping("/pharmacy/{pharmacyId}/paged")
    @Operation(summary = "Get inventory for a pharmacy (paginated)")
    public ResponseEntity<Page<InventorySummaryDTO>> getPharmacyInventoryPaged(
            @Parameter(description = "Pharmacy ID") @PathVariable Long pharmacyId,
            @Parameter(description = "Page number (1-based)") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), Math.min(size, 100));
        return ResponseEntity.ok(inventoryService.getByPharmacyPaged(pharmacyId, pageable));
    }

    @GetMapping("/pharmacy/{pharmacyId}/search")
    @Operation(summary = "Search medicines in a pharmacy")
    public ResponseEntity<List<InventorySummaryDTO>> searchPharmacyInventory(
            @Parameter(description = "Pharmacy ID") @PathVariable Long pharmacyId,
            @Parameter(description = "Search query") @RequestParam String q) {
        return ResponseEntity.ok(inventoryService.searchMedicinesInPharmacy(pharmacyId, q));
    }

    @GetMapping("/medicine/{medicineId}")
    @Operation(summary = "Get all pharmacies carrying a medicine")
    public ResponseEntity<List<InventorySummaryDTO>> getMedicineAvailability(
            @Parameter(description = "Medicine ID") @PathVariable Long medicineId) {
        return ResponseEntity.ok(inventoryService.getByMedicine(medicineId));
    }

    @PostMapping
    @Operation(summary = "Create a new inventory entry")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InventorySummaryDTO> createInventory(
            @Valid @RequestBody InventoryRequestDTO dto) {
        InventorySummaryDTO created = inventoryService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an inventory entry")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InventorySummaryDTO> updateInventory(
            @Parameter(description = "Inventory ID") @PathVariable Long id,
            @Valid @RequestBody InventoryRequestDTO dto) {
        return ResponseEntity.ok(inventoryService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an inventory entry")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteInventory(
            @Parameter(description = "Inventory ID") @PathVariable Long id) {
        inventoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/availability")
    @Operation(summary = "Update inventory availability status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InventorySummaryDTO> updateAvailability(
            @Parameter(description = "Inventory ID") @PathVariable Long id,
            @Parameter(description = "New availability status") @RequestParam Inventory.AvailabilityStatus status,
            @Parameter(description = "Verification notes") @RequestParam(required = false) String notes) {
        return ResponseEntity.ok(inventoryService.updateAvailability(id, status, notes));
    }
}