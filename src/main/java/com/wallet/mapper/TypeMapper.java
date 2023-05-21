package com.wallet.mapper;

import com.wallet.dto.TypeDTO;
import com.wallet.entity.Type;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TypeMapper {

    TypeMapper INSTANCE = Mappers.getMapper(TypeMapper.class);

    TypeDTO toDTO(Type entity);

    Type toEntity(TypeDTO dto);

}
