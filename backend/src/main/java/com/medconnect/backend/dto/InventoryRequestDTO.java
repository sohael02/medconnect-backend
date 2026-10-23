package com.medconnect.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Inventory create/update request")
public class InventoryRequestDTO {

    @NotNull(message = "Pharmacy ID is required")
    @Schema(description = "Pharmacy ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long pharmacyId;

    @NotNull(message = "Medicine ID is required")
    @Schema(description = "Medicine ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long medicineId;

    @Min(value = 0, message = "Quantity cannot be negative")
    @Schema(description = "Quantity in stock", example = "100")
    private Integer quantity;

    @Schema(description = "Availability status", example = "IN_STOCK", allowableValues = {"IN_STOCK", "OUT_OF_STOCK", "LIMITED", "UNVERIFIED", "DISCONTINUED"})
    private String availabilityStatus;

    @DecimalMin(value = "0.0", inclusive = true, message = "Price cannot be negative")
    @Schema(description = "Price", example = "15.50")
    private BigDecimal price;

    @Size(max = 3, message = "Currency must not exceed 3 characters")
    @Builder.Default
    @Schema(description = "Currency code", example = "INR", defaultValue = "INR")
    private String currency = "INR";

    @Schema(description = "Last updated timestamp (ISO format)")
    private String lastUpdatedAt;

    @Size(max = 50, message = "Data source must not exceed 50 characters")
    @Builder.Default
    @Schema(description = "Data source", example = "MANUAL", defaultValue = "MANUAL")
    private String dataSource = "MANUAL";

    @Schema(description = "Verification notes")
    private String verificationNotes;
}