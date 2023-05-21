package com.wallet.mapper;

import com.wallet.dto.RequestTypeDTO;
import com.wallet.entity.RequestType;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RequestTypeMapper {

    RequestTypeMapper INSTANCE = Mappers.getMapper(RequestTypeMapper.class);

    RequestTypeDTO toDTO(RequestType entity);

    RequestType toEntity(RequestTypeDTO dto);

}
