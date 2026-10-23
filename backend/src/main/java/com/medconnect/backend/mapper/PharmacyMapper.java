package com.medconnect.backend.mapper;

import com.medconnect.backend.dto.InventorySummaryDTO;
import com.medconnect.backend.dto.MedicineResponseDTO;
import com.medconnect.backend.dto.PharmacyAvailabilityDTO;
import com.medconnect.backend.dto.PharmacyRequestDTO;
import com.medconnect.backend.dto.PharmacyResponseDTO;
import com.medconnect.backend.entity.Inventory;
import com.medconnect.backend.entity.Medicine;
import com.medconnect.backend.entity.Pharmacy;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring", uses = {MedicineMapper.class})
public interface PharmacyMapper {

    PharmacyMapper INSTANCE = Mappers.getMapper(PharmacyMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "inventories", ignore = true)
    Pharmacy toEntity(PharmacyRequestDTO dto);

    @Mapping(target = "inventories", ignore = true)
    void updateEntityFromDto(PharmacyRequestDTO dto, @MappingTarget Pharmacy entity);

    @Named("toResponseDTO")
    PharmacyResponseDTO toResponseDTO(Pharmacy entity);

    @Named("toResponseDTOBasic")
    @Mapping(target = "distanceKm", ignore = true)
    @Mapping(target = "matchingMedicines", ignore = true)
    PharmacyResponseDTO toResponseDTOBasic(Pharmacy entity);

    @IterableMapping(qualifiedByName = "toResponseDTO")
    List<PharmacyResponseDTO> toResponseDTOList(List<Pharmacy> entities);

    // Custom mappings for computed fields
    @Named("toInventorySummaryDTO")
    default InventorySummaryDTO toInventorySummaryDTO(Inventory inventory) {
        if (inventory == null) return null;
        return InventorySummaryDTO.builder()
                .inventoryId(inventory.getId())
                .medicineName(inventory.getMedicine().getNormalizedName())
                .brandName(inventory.getMedicine().getBrandName())
                .strength(inventory.getMedicine().getStrength())
                .dosageForm(inventory.getMedicine().getDosageForm())
                .availabilityStatus(inventory.getAvailabilityStatus().name())
                .availabilityDisplay(inventory.getAvailabilityDisplay())
                .price(inventory.getPrice())
                .currency(inventory.getCurrency())
                .quantity(inventory.getQuantity())
                .lastUpdatedAt(inventory.getLastUpdatedAt())
                .dataSource(inventory.getDataSource())
                .verified(inventory.isVerified())
                .build();
    }

    @Named("toPharmacyAvailabilityDTO")
    default PharmacyAvailabilityDTO toPharmacyAvailabilityDTO(Inventory inventory) {
        if (inventory == null) return null;
        Pharmacy pharmacy = inventory.getPharmacy();
        return PharmacyAvailabilityDTO.builder()
                .pharmacyId(pharmacy.getId())
                .pharmacyName(pharmacy.getName())
                .address(pharmacy.getAddress())
                .latitude(pharmacy.getLatitude())
                .longitude(pharmacy.getLongitude())
                .phoneNumber(pharmacy.getPhoneNumber())
                .availabilityStatus(inventory.getAvailabilityStatus().name())
                .availabilityDisplay(inventory.getAvailabilityDisplay())
                .price(inventory.getPrice())
                .currency(inventory.getCurrency())
                .quantity(inventory.getQuantity())
                .lastUpdatedAt(inventory.getLastUpdatedAt())
                .dataSource(inventory.getDataSource())
                .verified(inventory.isVerified())
                .build();
    }
}