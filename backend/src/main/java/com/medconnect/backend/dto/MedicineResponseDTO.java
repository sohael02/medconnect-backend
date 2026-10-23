package com.medconnect.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Medicine response DTO")
public class MedicineResponseDTO {

    @Schema(description = "Unique identifier", example = "1")
    private Long id;

    @Schema(description = "Normalized medicine name", example = "Paracetamol")
    private String normalizedName;

    @Schema(description = "Brand name", example = "Dolo")
    private String brandName;

    @Schema(description = "Strength", example = "500 mg")
    private String strength;

    @Schema(description = "Dosage form", example = "Tablet")
    private String dosageForm;

    @Schema(description = "Generic name", example = "Acetaminophen")
    private String genericName;

    @Schema(description = "Manufacturer", example = "Micro Labs Ltd")
    private String manufacturer;

    @Schema(description = "Category", example = "Analgesic")
    private String category;

    @Schema(description = "Description")
    private String description;

    @Schema(description = "Whether the medicine is active", example = "true")
    private Boolean isActive;

    @Schema(description = "Creation timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    @Schema(description = "Display name combining all fields", example = "Paracetamol (Dolo) 500 mg Tablet")
    private String displayName;

    @Schema(description = "Pharmacies carrying this medicine")
    private List<PharmacyAvailabilityDTO> pharmacyAvailabilities;
}