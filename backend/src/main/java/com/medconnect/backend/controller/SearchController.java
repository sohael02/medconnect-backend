package com.medconnect.backend.controller;

import com.medconnect.backend.dto.SearchRequestDTO;
import com.medconnect.backend.dto.SearchResponseDTO;
import com.medconnect.backend.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@Tag(name = "Search", description = "Medicine search with location-based pharmacy discovery")
public class SearchController {

    private final SearchService searchService;

    @PostMapping
    @Operation(summary = "Search for pharmacies with a specific medicine near a location")
    public ResponseEntity<SearchResponseDTO> search(@Valid @RequestBody SearchRequestDTO request) {
        SearchResponseDTO response = searchService.search(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Search for pharmacies with a specific medicine near a location (GET)")
    public ResponseEntity<SearchResponseDTO> searchGet(
            @RequestParam String medicine,
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(required = false) String strength,
            @RequestParam(required = false) String dosageForm,
            @RequestParam(defaultValue = "10") Integer radiusKm,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "false") Boolean verifiedOnly) {
        
        SearchRequestDTO request = SearchRequestDTO.builder()
                .medicine(medicine)
                .strength(strength)
                .dosageForm(dosageForm)
                .latitude(latitude)
                .longitude(longitude)
                .radiusKm(radiusKm)
                .page(page)
                .pageSize(pageSize)
                .verifiedOnly(verifiedOnly)
                .build();

        SearchResponseDTO response = searchService.search(request);
        return ResponseEntity.ok(response);
    }
}