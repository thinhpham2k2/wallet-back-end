package com.wallet.mapper;

import com.wallet.dto.CustomerDTO;
import com.wallet.entity.Customer;
import com.wallet.entity.Partner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    @Mapping(target = "partnerId", source = "partner.id")
    @Mapping(target = "partnerName", source = "partner.fullName")
    CustomerDTO toDTO(Customer entity);

    @Mapping(target = "partner", source = "partnerId", qualifiedByName = "mapPartner")
    @Mapping(target = "membershipList", ignore = true)
    Customer toEntity(CustomerDTO dto);

    @Named("mapPartner")
    default Partner mapPartner(Long id){
        Partner partner = new Partner();
        partner.setId(id);
        return partner;
    }
}
