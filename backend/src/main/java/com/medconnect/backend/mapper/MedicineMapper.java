package com.medconnect.backend.mapper;

import com.medconnect.backend.dto.MedicineRequestDTO;
import com.medconnect.backend.dto.MedicineResponseDTO;
import com.medconnect.backend.entity.Medicine;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MedicineMapper {

    MedicineMapper INSTANCE = Mappers.getMapper(MedicineMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "inventories", ignore = true)
    Medicine toEntity(MedicineRequestDTO dto);

    @Mapping(target = "inventories", ignore = true)
    void updateEntityFromDto(MedicineRequestDTO dto, @MappingTarget Medicine entity);

    @Named("toResponseDTO")
    MedicineResponseDTO toResponseDTO(Medicine entity);

    @Named("toResponseDTOBasic")
    @Mapping(target = "pharmacyAvailabilities", ignore = true)
    MedicineResponseDTO toResponseDTOBasic(Medicine entity);

    @IterableMapping(qualifiedByName = "toResponseDTO")
    List<MedicineResponseDTO> toResponseDTOList(List<Medicine> entities);
}