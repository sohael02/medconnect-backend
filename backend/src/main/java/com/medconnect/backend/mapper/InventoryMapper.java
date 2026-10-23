package com.medconnect.backend.mapper;

import com.medconnect.backend.dto.InventoryRequestDTO;
import com.medconnect.backend.entity.Inventory;
import com.medconnect.backend.entity.Medicine;
import com.medconnect.backend.entity.Pharmacy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    InventoryMapper INSTANCE = Mappers.getMapper(InventoryMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "pharmacy", ignore = true)
    @Mapping(target = "medicine", ignore = true)
    @Mapping(target = "availabilityStatus", source = "availabilityStatus", qualifiedByName = "stringToAvailabilityStatus")
    @Mapping(target = "lastUpdatedAt", source = "lastUpdatedAt", qualifiedByName = "stringToLocalDateTime")
    Inventory toEntity(InventoryRequestDTO dto);

    @Mapping(target = "pharmacy", ignore = true)
    @Mapping(target = "medicine", ignore = true)
    @Mapping(target = "availabilityStatus", source = "availabilityStatus", qualifiedByName = "stringToAvailabilityStatus")
    @Mapping(target = "lastUpdatedAt", source = "lastUpdatedAt", qualifiedByName = "stringToLocalDateTime")
    void updateEntityFromDto(InventoryRequestDTO dto, @MappingTarget Inventory entity);

    @Named("stringToAvailabilityStatus")
    default Inventory.AvailabilityStatus stringToAvailabilityStatus(String status) {
        if (status == null || status.isBlank()) {
            return Inventory.AvailabilityStatus.UNVERIFIED;
        }
        try {
            return Inventory.AvailabilityStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Inventory.AvailabilityStatus.UNVERIFIED;
        }
    }

    @Named("stringToLocalDateTime")
    default LocalDateTime stringToLocalDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}