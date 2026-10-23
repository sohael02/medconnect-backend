package com.medconnect.backend.controller;

import com.medconnect.backend.dto.MedicineRequestDTO;
import com.medconnect.backend.dto.MedicineResponseDTO;
import com.medconnect.backend.service.MedicineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicines")
@RequiredArgsConstructor
@Tag(name = "Medicines", description = "Medicine catalog management endpoints")
public class MedicineController {

    private final MedicineService medicineService;

    @GetMapping
    @Operation(summary = "Get all active medicines (paginated)")
    public ResponseEntity<Page<MedicineResponseDTO>> getAllMedicines(
            @Parameter(description = "Page number (1-based)") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "normalizedName") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), Math.min(size, 100), sort);
        
        Page<MedicineResponseDTO> result = medicineService.getAllActive(pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/list")
    @Operation(summary = "Get all active medicines (list)")
    public ResponseEntity<List<MedicineResponseDTO>> getAllMedicinesList() {
        return ResponseEntity.ok(medicineService.getAllActiveList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get medicine by ID")
    public ResponseEntity<MedicineResponseDTO> getMedicineById(
            @Parameter(description = "Medicine ID") @PathVariable Long id) {
        return ResponseEntity.ok(medicineService.getById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search medicines by name")
    public ResponseEntity<List<MedicineResponseDTO>> searchMedicines(
            @Parameter(description = "Search query") @RequestParam String q) {
        return ResponseEntity.ok(medicineService.searchByName(q));
    }

    @GetMapping("/search/filter")
    @Operation(summary = "Search medicines with filters")
    public ResponseEntity<List<MedicineResponseDTO>> searchMedicinesWithFilters(
            @Parameter(description = "Search query") @RequestParam String q,
            @Parameter(description = "Strength filter") @RequestParam(required = false) String strength,
            @Parameter(description = "Dosage form filter") @RequestParam(required = false) String dosageForm) {
        return ResponseEntity.ok(medicineService.searchWithFilters(q, strength, dosageForm));
    }

    @PostMapping
    @Operation(summary = "Create a new medicine")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MedicineResponseDTO> createMedicine(
            @Valid @RequestBody MedicineRequestDTO dto) {
        MedicineResponseDTO created = medicineService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a medicine")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MedicineResponseDTO> updateMedicine(
            @Parameter(description = "Medicine ID") @PathVariable Long id,
            @Valid @RequestBody MedicineRequestDTO dto) {
        return ResponseEntity.ok(medicineService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a medicine")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMedicine(
            @Parameter(description = "Medicine ID") @PathVariable Long id) {
        medicineService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle")
    @Operation(summary = "Toggle medicine active status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MedicineResponseDTO> toggleMedicineActive(
            @Parameter(description = "Medicine ID") @PathVariable Long id) {
        return ResponseEntity.ok(medicineService.toggleActive(id));
    }
}