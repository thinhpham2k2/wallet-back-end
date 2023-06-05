package com.wallet.mapper;

import com.wallet.dto.PartnerRegisterDTO;
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
    @Mapping(target = "password", ignore = true)
    Partner toEntity(PartnerUpdateDTO dto);
}
