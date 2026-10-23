package com.medconnect.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Medicine create/update request")
public class MedicineRequestDTO {

    @NotBlank(message = "Medicine name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    @Schema(description = "Normalized medicine name", example = "Paracetamol", requiredMode = Schema.RequiredMode.REQUIRED)
    private String normalizedName;

    @Size(max = 255, message = "Brand name must not exceed 255 characters")
    @Schema(description = "Brand name", example = "Dolo")
    private String brandName;

    @Size(max = 100, message = "Strength must not exceed 100 characters")
    @Schema(description = "Strength", example = "500 mg")
    private String strength;

    @Size(max = 50, message = "Dosage form must not exceed 50 characters")
    @Schema(description = "Dosage form", example = "Tablet")
    private String dosageForm;

    @Size(max = 255, message = "Generic name must not exceed 255 characters")
    @Schema(description = "Generic name", example = "Acetaminophen")
    private String genericName;

    @Size(max = 255, message = "Manufacturer must not exceed 255 characters")
    @Schema(description = "Manufacturer", example = "Micro Labs Ltd")
    private String manufacturer;

    @Size(max = 100, message = "Category must not exceed 100 characters")
    @Schema(description = "Category", example = "Analgesic")
    private String category;

    @Schema(description = "Description")
    private String description;

    @Schema(description = "Whether the medicine is active", example = "true")
    private Boolean isActive;
}