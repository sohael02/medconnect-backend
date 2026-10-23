package com.medconnect.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Search request parameters")
public class SearchRequestDTO {

    @NotBlank(message = "Medicine name is required")
    @Schema(description = "Medicine name to search for", example = "Paracetamol", requiredMode = Schema.RequiredMode.REQUIRED)
    private String medicine;

    @Schema(description = "Optional strength filter", example = "500 mg")
    private String strength;

    @Schema(description = "Optional dosage form filter", example = "Tablet")
    private String dosageForm;

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    @Schema(description = "User latitude", example = "15.2156868", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    @Schema(description = "User longitude", example = "79.9034208", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double longitude;

    @Min(value = 1, message = "Radius must be at least 1 km")
    @Max(value = 50, message = "Radius cannot exceed 50 km")
    @Builder.Default
    @Schema(description = "Search radius in kilometers", example = "10", defaultValue = "10")
    private Integer radiusKm = 10;

    @Min(value = 1, message = "Page must be at least 1")
    @Builder.Default
    @Schema(description = "Page number (1-based)", example = "1", defaultValue = "1")
    private Integer page = 1;

    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size cannot exceed 100")
    @Builder.Default
    @Schema(description = "Number of results per page", example = "20", defaultValue = "20")
    private Integer pageSize = 20;

    @Builder.Default
    @Schema(description = "Include only verified availability", example = "false", defaultValue = "false")
    private Boolean verifiedOnly = false;
}