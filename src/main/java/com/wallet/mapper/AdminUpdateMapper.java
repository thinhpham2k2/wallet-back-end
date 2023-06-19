package com.wallet.mapper;

import com.wallet.dto.AdminUpdateDTO;
import com.wallet.entity.Admin;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AdminUpdateMapper {

    AdminUpdateMapper INSTANCE = Mappers.getMapper(AdminUpdateMapper.class);

    AdminUpdateDTO toDTO(Admin entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userName", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "status", ignore = true)
    Admin toEntity(AdminUpdateDTO dto);

}
