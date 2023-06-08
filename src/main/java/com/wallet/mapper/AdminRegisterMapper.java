package com.wallet.mapper;

import com.wallet.dto.AdminRegisterDTO;
import com.wallet.entity.Admin;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AdminRegisterMapper {

    AdminRegisterMapper INSTANCE = Mappers.getMapper(AdminRegisterMapper.class);

    AdminRegisterDTO toDTO(Admin entity);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "status", ignore = true)
    Admin toEntity(AdminRegisterDTO dto);

}
