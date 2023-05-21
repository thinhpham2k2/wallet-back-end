package com.wallet.mapper;

import com.wallet.dto.WalletTypeDTO;
import com.wallet.entity.WalletType;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface WalletTypeMapper {

    WalletTypeMapper INSTANCE = Mappers.getMapper(WalletTypeMapper.class);

    WalletTypeDTO toDTO(WalletType entity);

    WalletType toEntity(WalletTypeDTO dto);

}
