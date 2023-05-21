package com.wallet.mapper;

import com.wallet.dto.AdminDTO;
import com.wallet.entity.Admin;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AdminMapper {
    AdminMapper INSTANCE = Mappers.getMapper(AdminMapper.class);

    AdminDTO toDTO(Admin entity);

    @Mapping(target = "password", ignore = true)
    Admin toEntity(AdminDTO dto);

}
