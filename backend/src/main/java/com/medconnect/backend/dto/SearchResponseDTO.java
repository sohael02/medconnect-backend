package com.medconnect.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Search response with paginated results")
public class SearchResponseDTO {

    @Schema(description = "List of matching pharmacies")
    private List<PharmacyResponseDTO> pharmacies;

    @Schema(description = "Total number of matching pharmacies", example = "42")
    private Long totalResults;

    @Schema(description = "Current page number (1-based)", example = "1")
    private Integer currentPage;

    @Schema(description = "Number of results per page", example = "20")
    private Integer pageSize;

    @Schema(description = "Total number of pages", example = "3")
    private Integer totalPages;

    @Schema(description = "Whether there is a next page", example = "true")
    private Boolean hasNext;

    @Schema(description = "Whether there is a previous page", example = "false")
    private Boolean hasPrevious;

    @Schema(description = "Search query that was used", example = "Paracetamol")
    private String query;

    @Schema(description = "Search location latitude", example = "15.2156868")
    private Double searchLatitude;

    @Schema(description = "Search location longitude", example = "79.9034208")
    private Double searchLongitude;

    @Schema(description = "Search radius in kilometers", example = "10")
    private Integer radiusKm;
}