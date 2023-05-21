package com.wallet.mapper;

import com.wallet.dto.ProgramDTO;
import com.wallet.entity.Partner;
import com.wallet.entity.Program;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProgramMapper {

    ProgramMapper INSTANCE = Mappers.getMapper(ProgramMapper.class);

    @Mapping(target = "partnerId", source = "partner.id")
    @Mapping(target = "partnerName", source = "partner.fullName")
    ProgramDTO toDTO(Program entity);

    @Mapping(target = "partner", source = "partnerId", qualifiedByName = "mapPartner")
    Program toEntity(ProgramDTO dto);

    @Named("mapPartner")
    default Partner mapPartner(Long id) {
        Partner partner = new Partner();
        partner.setId(id);
        return partner;
    }

}
