package com.wallet.mapper;

import com.wallet.dto.LevelDTO;
import com.wallet.entity.Level;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface LevelMapper {

    LevelMapper INSTANCE = Mappers.getMapper(LevelMapper.class);

    LevelDTO toDTO(Level entity);

    Level toEntity(LevelDTO dto);

}
