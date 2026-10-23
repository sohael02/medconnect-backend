package com.medconnect.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Pharmacy response DTO")
public class PharmacyResponseDTO {

    @Schema(description = "Unique identifier", example = "1")
    private Long id;

    @Schema(description = "Pharmacy name", example = "Apollo Pharmacy Kandukur")
    private String name;

    @Schema(description = "Full address", example = "Gupthas Plaza, No. 20/2/27, Pamuru Rd, Kota Reddy Nagar, Kandukur, Andhra Pradesh 523105")
    private String address;

    @Schema(description = "Latitude coordinate", example = "15.2156868")
    private Double latitude;

    @Schema(description = "Longitude coordinate", example = "79.9034208")
    private Double longitude;

    @Schema(description = "Phone number", example = "7032219399")
    private String phoneNumber;

    @Schema(description = "Email address", example = "greenpharmacy@gmail.com")
    private String email;

    @Schema(description = "Website URL", example = "https://www.apollopharmacy.in/")
    private String website;

    @Schema(description = "Postal code", example = "523105")
    private String pinCode;

    @Schema(description = "Google Plus Code", example = "6W83+29 Kandukur, Andhra Pradesh")
    private String plusCode;

    @Schema(description = "Owner name", example = "John Doe")
    private String ownerName;

    @Schema(description = "Whether the pharmacy is active", example = "true")
    private Boolean isActive;

    @Schema(description = "Creation timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    // Computed fields for search results
    @Schema(description = "Distance from search location in kilometers", example = "2.5")
    private Double distanceKm;

    @Schema(description = "Matching medicines in this pharmacy")
    private List<InventorySummaryDTO> matchingMedicines;
}