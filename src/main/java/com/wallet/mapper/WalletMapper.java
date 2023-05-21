package com.wallet.mapper;

import com.wallet.dto.WalletDTO;
import com.wallet.entity.Membership;
import com.wallet.entity.Wallet;
import com.wallet.entity.WalletType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface WalletMapper {

    WalletMapper INSTANCE = Mappers.getMapper(WalletMapper.class);

    @Mapping(target = "membershipId", source = "membership.id")
    @Mapping(target = "typeId", source = "type.id")
    @Mapping(target = "type", source = "type.type")
    WalletDTO toDTO(Wallet entity);

    @Mapping(target = "membership", source = "membershipId", qualifiedByName = "mapMem")
    @Mapping(target = "type", source = "typeId", qualifiedByName = "mapType")
    Wallet toEntity(WalletDTO dto);

    @Named("mapMem")
    default Membership mapMem(Long id) {
        Membership membership = new Membership();
        membership.setId(id);
        return membership;
    }

    @Named("mapType")
    default WalletType mapType(Long id) {
        WalletType type = new WalletType();
        type.setId(id);
        return type;
    }

}
