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
@Schema(description = "Pharmacy availability for a specific medicine")
public class PharmacyAvailabilityDTO {

    @Schema(description = "Pharmacy ID", example = "1")
    private Long pharmacyId;

    @Schema(description = "Pharmacy name", example = "Apollo Pharmacy Kandukur")
    private String pharmacyName;

    @Schema(description = "Pharmacy address", example = "Gupthas Plaza, Pamuru Rd, Kandukur")
    private String address;

    @Schema(description = "Latitude", example = "15.2156868")
    private Double latitude;

    @Schema(description = "Longitude", example = "79.9034208")
    private Double longitude;

    @Schema(description = "Phone number", example = "7032219399")
    private String phoneNumber;

    @Schema(description = "Distance from search location in km", example = "2.5")
    private Double distanceKm;

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