package com.wallet.mapper;

import com.wallet.dto.MembershipDTO;
import com.wallet.entity.Customer;
import com.wallet.entity.Level;
import com.wallet.entity.Membership;
import com.wallet.entity.Program;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MembershipMapper {

    MembershipMapper INSTANCE = Mappers.getMapper(MembershipMapper.class);

    @Mapping(target = "levelId", source = "level.id")
    @Mapping(target = "level", source = "level.level")
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", source = "customer.fullName")
    @Mapping(target = "programId", source = "program.id")
    @Mapping(target = "programName", source = "program.programName")
    MembershipDTO toDTO(Membership entity);

    @Mapping(target = "level", source = "levelId", qualifiedByName = "mapLevel")
    @Mapping(target = "customer", source = "customerId", qualifiedByName = "mapCustomer")
    @Mapping(target = "program", source = "programId", qualifiedByName = "mapProgram")
    Membership toEntity(MembershipDTO dto);

    @Named("mapLevel")
    default Level mapLevel(Long id){
        Level level = new Level();
        level.setId(id);
        return level;
    }

    @Named("mapCustomer")
    default Customer mapCustomer(Long id){
        Customer customer = new Customer();
        customer.setId(id);
        return customer;
    }

    @Named("mapProgram")
    default Program mapProgram(Long id){
        Program program = new Program();
        program.setId(id);
        return program;
    }
}
