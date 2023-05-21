package com.wallet.mapper;

import com.wallet.dto.ProgramLevelDTO;
import com.wallet.entity.Level;
import com.wallet.entity.Program;
import com.wallet.entity.ProgramLevel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProgramLevelMapper {

    ProgramMapper INSTANCE = Mappers.getMapper(ProgramMapper.class);

    @Mapping(target = "levelId", source = "level.id")
    @Mapping(target = "level", source = "level.level")
    @Mapping(target = "programId", source = "program.id")
    @Mapping(target = "programName", source = "program.programName")
    ProgramLevelDTO toDTO(ProgramLevel entity);

    @Mapping(target = "level", source = "levelId", qualifiedByName = "mapLevel")
    @Mapping(target = "program", source = "programId", qualifiedByName = "mapProgram")
    ProgramLevel toEntity(ProgramLevelDTO dto);

    @Named("mapLevel")
    default Level mapLevel(Long id) {
        Level level = new Level();
        level.setId(id);
        return level;
    }

    @Named("mapProgram")
    default Program mapProgram(Long id) {
        Program program = new Program();
        program.setId(id);
        return program;
    }

}
