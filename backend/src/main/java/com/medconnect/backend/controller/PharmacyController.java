package com.medconnect.backend.controller;

import com.medconnect.backend.dto.PharmacyRequestDTO;
import com.medconnect.backend.dto.PharmacyResponseDTO;
import com.medconnect.backend.service.PharmacyService;
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
@RequestMapping("/pharmacies")
@RequiredArgsConstructor
@Tag(name = "Pharmacies", description = "Pharmacy management endpoints")
public class PharmacyController {

    private final PharmacyService pharmacyService;

    @GetMapping
    @Operation(summary = "Get all active pharmacies (paginated)")
    public ResponseEntity<Page<PharmacyResponseDTO>> getAllPharmacies(
            @Parameter(description = "Page number (1-based)") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), Math.min(size, 100), sort);
        
        Page<PharmacyResponseDTO> result = pharmacyService.getAllActive(pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/list")
    @Operation(summary = "Get all active pharmacies (list)")
    public ResponseEntity<List<PharmacyResponseDTO>> getAllPharmaciesList() {
        return ResponseEntity.ok(pharmacyService.getAllActiveList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get pharmacy by ID")
    public ResponseEntity<PharmacyResponseDTO> getPharmacyById(
            @Parameter(description = "Pharmacy ID") @PathVariable Long id) {
        return ResponseEntity.ok(pharmacyService.getById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search pharmacies by name")
    public ResponseEntity<List<PharmacyResponseDTO>> searchPharmacies(
            @Parameter(description = "Name query") @RequestParam String name) {
        return ResponseEntity.ok(pharmacyService.searchByName(name));
    }

    @PostMapping
    @Operation(summary = "Create a new pharmacy")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PharmacyResponseDTO> createPharmacy(
            @Valid @RequestBody PharmacyRequestDTO dto) {
        PharmacyResponseDTO created = pharmacyService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a pharmacy")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PharmacyResponseDTO> updatePharmacy(
            @Parameter(description = "Pharmacy ID") @PathVariable Long id,
            @Valid @RequestBody PharmacyRequestDTO dto) {
        return ResponseEntity.ok(pharmacyService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a pharmacy")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePharmacy(
            @Parameter(description = "Pharmacy ID") @PathVariable Long id) {
        pharmacyService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle")
    @Operation(summary = "Toggle pharmacy active status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PharmacyResponseDTO> togglePharmacyActive(
            @Parameter(description = "Pharmacy ID") @PathVariable Long id) {
        return ResponseEntity.ok(pharmacyService.toggleActive(id));
    }
}