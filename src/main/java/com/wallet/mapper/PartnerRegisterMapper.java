package com.wallet.mapper;

import com.wallet.dto.PartnerDTO;
import com.wallet.dto.PartnerRegisterDTO;
import com.wallet.entity.Partner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PartnerRegisterMapper {

    PartnerRegisterMapper INSTANCE = Mappers.getMapper(PartnerRegisterMapper.class);

    PartnerRegisterDTO toDTO(Partner entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "status", ignore = true)
    Partner toEntity(PartnerRegisterDTO dto);

}
