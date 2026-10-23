package com.medconnect.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Pharmacy create/update request")
public class PharmacyRequestDTO {

    @NotBlank(message = "Pharmacy name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    @Schema(description = "Pharmacy name", example = "Apollo Pharmacy Kandukur", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank(message = "Address is required")
    @Schema(description = "Full address", example = "Gupthas Plaza, Pamuru Rd, Kandukur", requiredMode = Schema.RequiredMode.REQUIRED)
    private String address;

    @NotNull(message = "Latitude is required")
    @Schema(description = "Latitude coordinate", example = "15.2156868", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double latitude;

    @NotNull(message = "Longitude is required")
    @Schema(description = "Longitude coordinate", example = "79.9034208", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double longitude;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    @Schema(description = "Phone number", example = "7032219399")
    private String phoneNumber;

    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Schema(description = "Email address", example = "pharmacy@example.com")
    private String email;

    @Size(max = 500, message = "Website must not exceed 500 characters")
    @Schema(description = "Website URL", example = "https://www.apollopharmacy.in/")
    private String website;

    @Size(max = 10, message = "Pin code must not exceed 10 characters")
    @Schema(description = "Postal code", example = "523105")
    private String pinCode;

    @Size(max = 20, message = "Plus code must not exceed 20 characters")
    @Schema(description = "Google Plus Code", example = "6W83+29 Kandukur")
    private String plusCode;

    @Size(max = 100, message = "Owner name must not exceed 100 characters")
    @Schema(description = "Owner name", example = "John Doe")
    private String ownerName;

    @Schema(description = "Whether the pharmacy is active", example = "true")
    private Boolean isActive;
}