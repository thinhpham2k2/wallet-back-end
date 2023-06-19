package com.wallet.mapper;

import com.wallet.dto.PartnerUpdateDTO;
import com.wallet.entity.Partner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PartnerUpdateMapper {


    PartnerUpdateMapper INSTANCE = Mappers.getMapper(PartnerUpdateMapper.class);

    PartnerUpdateDTO toDTO(Partner entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userName", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "customerList", ignore = true)
    @Mapping(target = "programList", ignore = true)
    @Mapping(target = "requestList", ignore = true)
    Partner toEntity(PartnerUpdateDTO dto);
}
