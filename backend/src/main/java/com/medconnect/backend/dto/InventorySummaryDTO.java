package com.medconnect.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Inventory summary for search results")
public class InventorySummaryDTO {

    @Schema(description = "Inventory ID", example = "1")
    private Long inventoryId;

    @Schema(description = "Medicine name", example = "Paracetamol")
    private String medicineName;

    @Schema(description = "Brand name", example = "Dolo")
    private String brandName;

    @Schema(description = "Strength", example = "500 mg")
    private String strength;

    @Schema(description = "Dosage form", example = "Tablet")
    private String dosageForm;

    @Schema(description = "Availability status", example = "UNVERIFIED", allowableValues = {"IN_STOCK", "OUT_OF_STOCK", "LIMITED", "UNVERIFIED", "DISCONTINUED"})
    private String availabilityStatus;

    @Schema(description = "Availability display text", example = "Availability Unverified")
    private String availabilityDisplay;

    @Schema(description = "Price", example = "15.50")
    private BigDecimal price;

    @Schema(description = "Currency", example = "INR")
    private String currency;

    @Schema(description = "Quantity in stock", example = "100")
    private Integer quantity;

    @Schema(description = "Last inventory update timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastUpdatedAt;

    @Schema(description = "Data source", example = "IMPORT")
    private String dataSource;

    @Schema(description = "Whether availability is verified", example = "false")
    private Boolean verified;
}