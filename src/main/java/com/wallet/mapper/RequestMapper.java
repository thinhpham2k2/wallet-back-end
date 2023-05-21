package com.wallet.mapper;

import com.wallet.dto.RequestDTO;
import com.wallet.entity.Partner;
import com.wallet.entity.Request;
import com.wallet.entity.RequestType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    RequestMapper INSTANCE = Mappers.getMapper(RequestMapper.class);

    @Mapping(target = "partnerId", source = "partner.id")
    @Mapping(target = "partnerName", source = "partner.fullName")
    @Mapping(target = "typeId", source = "type.id")
    @Mapping(target = "type", source = "type.type")
    RequestDTO toDTO(Request entity);

    @Mapping(target = "partner", source = "partnerId", qualifiedByName = "mapPartner")
    @Mapping(target = "type", source = "typeId", qualifiedByName = "mapType")
    Request toEntity(RequestDTO dto);

    @Named("mapPartner")
    default Partner mapPartner(Long id) {
        Partner partner = new Partner();
        partner.setId(id);
        return partner;
    }

    @Named("mapType")
    default RequestType mapType(Long id) {
        RequestType type = new RequestType();
        type.setId(id);
        return type;
    }

}
