package com.wallet.mapper;

import com.wallet.dto.PartnerDTO;
import com.wallet.entity.Partner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PartnerMapper {

    PartnerMapper INSTANCE = Mappers.getMapper(PartnerMapper.class);

    PartnerDTO toDTO(Partner entity);

    @Mapping(target = "password", ignore = true)
    Partner toEntity(PartnerDTO dto);

}
