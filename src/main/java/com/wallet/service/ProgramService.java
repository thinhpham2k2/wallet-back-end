package com.wallet.service;

import com.wallet.dto.*;
import com.wallet.entity.*;
import com.wallet.jwt.JwtTokenProvider;
import com.wallet.mapper.LevelMapper;
import com.wallet.mapper.PartnerMapper;
import com.wallet.mapper.ProgramMapper;
import com.wallet.repository.*;
import com.wallet.service.interfaces.IPagingService;
import com.wallet.service.interfaces.IProgramService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProgramService implements IProgramService {

    private final ProgramRepository programRepository;

    private final ProgramLevelRepository programLevelRepository;

    private final LevelRepository levelRepository;

    private final PartnerRepository partnerRepository;

    private final MembershipRepository membershipRepository;

    private final CustomUserDetailsService customUserDetailsService;

    private final IPagingService pagingService;

    @Override
    public Page<ProgramDTO> getProgramList(boolean status, List<Long> partnerId, String search, String sort, int page, int limit) {
        if (limit < 1) throw new InvalidParameterException("Page size must not be less than one!");
        if (page < 0) throw new InvalidParameterException("Page number must not be less than zero!");
        List<Sort.Order> order = new ArrayList<>();
        Set<String> sourceFieldList = pagingService.getAllFields(Program.class);
        String[] subSort = sort.split(",");
        if (pagingService.checkPropertPresent(sourceFieldList, subSort[0])) {
            order.add(new Sort.Order(pagingService.getSortDirection(subSort[1]), transferProperty(subSort[0])));
        } else {
            throw new InvalidParameterException(subSort[0] + " is not a propertied of Program!");
        }
        Pageable pageable = PageRequest.of(page, limit).withSort(Sort.by(order));
        Page<Program> pageResult = programRepository.getProgramList(true, partnerId, search, pageable);
        return new PageImpl<>(pageResult.getContent().stream().map(ProgramMapper.INSTANCE::toDTO).collect(Collectors.toList()), pageResult.getPageable(), pageResult.getTotalElements());
    }

    private static String transferProperty(String property) {
        if (property.equals("partner")) {
            return "partner.fullName";
        }
        return property;
    }

    @Override
    public ProgramExtraDTO getProgramById(String token, long id, boolean isAdmin) {
        Optional<Program> program;
        if (isAdmin) {
            program = programRepository.getProgramByStatusAndId(true, id);
        } else {
            String userName;
            try {
                JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
                userName = jwtTokenProvider.getUserNameFromJWT(token);
            } catch (ExpiredJwtException e) {
                throw new InvalidParameterException("Invalid JWT token");
            }
            program = programRepository.getProgramByStatusAndId(true, id, userName);
        }
        if (program.isPresent()) {
            ProgramExtraDTO programExtra = new ProgramExtraDTO();
            programExtra.setNumOfMembers(membershipRepository.countAllByStatusAndProgramId(true, id));
            programExtra.setProgram(ProgramMapper.INSTANCE.toDTO(program.get()));
            programExtra.setPartner(PartnerMapper.INSTANCE.toDTO(program.get().getPartner()));
            programExtra.setLevelList(programLevelRepository.getProgramLevelByStatusAndProgramId(true, id).stream()
                    .map(ProgramLevel::getLevel).map(LevelMapper.INSTANCE::toDTO).filter(l -> l.getStatus().equals(true)).toList()
                    .stream().sorted(Comparator.comparing(LevelDTO::getCondition)).toList());
            return programExtra;
        }
        return null;
    }

    @Override
    public Page<ProgramDTO> getProgramListForPartner(boolean status, String token, String search, String sort, int page, int limit) {
        if (limit < 1) throw new InvalidParameterException("Page size must not be less than one!");
        if (page < 0) throw new InvalidParameterException("Page number must not be less than zero!");
        String userName;
        List<Sort.Order> order = new ArrayList<>();
        try {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
            userName = jwtTokenProvider.getUserNameFromJWT(token);
        } catch (ExpiredJwtException e) {
            throw new InvalidParameterException("Expired JWT token");
        }
        Set<String> sourceFieldList = pagingService.getAllFields(Program.class);
        String[] subSort = sort.split(",");
        if (pagingService.checkPropertPresent(sourceFieldList, subSort[0])) {
            order.add(new Sort.Order(pagingService.getSortDirection(subSort[1]), transferProperty(subSort[0])));
        } else {
            throw new InvalidParameterException(subSort[0] + " is not a propertied of Program!");
        }
        Pageable pageable = PageRequest.of(page, limit).withSort(Sort.by(order));
        Page<Program> pageResult = programRepository.getProgramListForPartner(true, userName, search, pageable);
        return new PageImpl<>(pageResult.getContent().stream().map(ProgramMapper.INSTANCE::toDTO).collect(Collectors.toList()), pageResult.getPageable(), pageResult.getTotalElements());
    }

    @Override
    public String getProgramTokenActiveByPartnerCode(String code) {
        Optional<Program> program = programRepository.getProgramToken(true, code, LocalDate.now()).stream().findFirst();
        return program.map(Program::getToken).orElse(null);
    }

    @Override
    public ProgramExtraDTO updateProgram(ProgramUpdateDTO update, String token) {
        String userName;
        try {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
            userName = jwtTokenProvider.getUserNameFromJWT(token);
        } catch (ExpiredJwtException e) {
            throw new InvalidParameterException("Expired JWT token");
        }
        Optional<Program> program = programRepository.getProgramByStatusAndId(true, update.getId(), userName);
        if (program.isPresent()) {
            if (!update.getProgramName().isBlank()) {
                if (!update.getLevelList().isEmpty()) {
                    if (checkLevel(update.getLevelList().stream().map(LevelUpdateDTO::getLevel).toList())) {
                        List<Long> idList = update.getLevelList().stream().map(LevelUpdateDTO::getId).filter(Objects::nonNull).toList();
                        Set<Long> ids = program.get().getProgramLevelList().stream().filter(p -> p.getStatus().equals(true)).map(ProgramLevel::getLevel).filter(l -> l.getStatus().equals(true)).map(Level::getId).collect(Collectors.toSet());
                        if (ids.containsAll(idList)) {
                            Set<Long> idSet = new HashSet<>(idList);
                            if (idList.size() == idSet.size()) {
                                List<BigDecimal> conditionList = update.getLevelList().stream().map(LevelUpdateDTO::getCondition).toList();
                                if (conditionList.stream().filter(c -> c.compareTo(BigDecimal.ZERO) < 0).toList().size() == 0) {
                                    Set<BigDecimal> conditionSet = new HashSet<>(conditionList);
                                    if (conditionList.size() == conditionSet.size()) {
                                        if (conditionList.stream().filter(c -> c.compareTo(BigDecimal.ZERO) == 0).toList().size() == 1) {
                                            //Create ProgramExtraDTO
                                            ProgramExtraDTO programExtra = new ProgramExtraDTO();
                                            programExtra.setNumOfMembers(membershipRepository.countAllByStatusAndProgramId(true, program.get().getId()));
                                            programExtra.setPartner(PartnerMapper.INSTANCE.toDTO(program.get().getPartner()));

                                            List<Program> programList = programRepository.findAllByStatusAndStateAndDateUpdatedBeforeAndPartnerId(true, true, LocalDate.now(), program.get().getPartner().getId());
                                            if (!programList.isEmpty()) {
                                                for (Program programPast : programList) {
                                                    programPast.setState(false);
                                                    programRepository.save(programPast);
                                                }
                                            }

                                            //Update program
                                            program.get().setProgramName(update.getProgramName());
                                            program.get().setDescription(update.getDescription());
                                            Program newProgram = programRepository.save(program.get());
                                            programExtra.setProgram(ProgramMapper.INSTANCE.toDTO(newProgram));

                                            List<LevelDTO> levelDTOS = new ArrayList<>();
                                            for (LevelUpdateDTO levelDTO : update.getLevelList()) {
                                                if (levelDTO.getId() != null) {
                                                    Optional<Level> level = levelRepository.findLevelByStatusAndId(true, levelDTO.getId());
                                                    if (level.isPresent()) {
                                                        //Update Level
                                                        level.get().setLevel(levelDTO.getLevel());
                                                        level.get().setCondition(levelDTO.getCondition());
                                                        level.get().setDescription(levelDTO.getDescription());
                                                        level.get().setStatus(levelDTO.getStatus());
                                                        Level newLevel = levelRepository.save(level.get());
                                                        //Update program level
                                                        Optional<ProgramLevel> programLevel = programLevelRepository.findFirstByLevelId(newLevel.getId());
                                                        if (programLevel.isPresent()) {
                                                            programLevel.get().setStatus(levelDTO.getStatus());
                                                            programLevel.get().setDescription(levelDTO.getDescription());
                                                            programLevelRepository.save(programLevel.get());
                                                            if (newLevel.getStatus().equals(true)) {
                                                                levelDTOS.add(LevelMapper.INSTANCE.toDTO(newLevel));
                                                            }
                                                        } else {
                                                            throw new InvalidParameterException("Not found level");
                                                        }
                                                    } else {
                                                        throw new InvalidParameterException("Not found level");
                                                    }
                                                } else {
                                                    //Create Level
                                                    Level level = levelRepository.save(new Level(null, levelDTO.getLevel(), levelDTO.getCondition(), levelDTO.getDescription(), true, null, null));
                                                    //Create program level
                                                    programLevelRepository.save(new ProgramLevel(null, levelDTO.getDescription(), true, true, level, program.get()));
                                                    levelDTOS.add(LevelMapper.INSTANCE.toDTO(level));
                                                }
                                            }
                                            ids.removeAll(idSet);
                                            for (Long id : ids) {
                                                Optional<Level> level = levelRepository.findLevelByStatusAndId(true, id);
                                                if (level.isPresent()) {
                                                    level.get().setStatus(false);
                                                    Level newLevel = levelRepository.save(level.get());
                                                    Optional<ProgramLevel> programLevel = programLevelRepository.findFirstByLevelId(id);
                                                    if (programLevel.isPresent()) {
                                                        programLevel.get().setStatus(false);
                                                        programLevelRepository.save(programLevel.get());
                                                    }
                                                }
                                            }
                                            programExtra.setLevelList(levelDTOS.stream().sorted(Comparator.comparing(LevelDTO::getCondition)).toList());

                                            try {
                                                for (Membership membership : membershipRepository.findAllByProgramIdAndStatus(programExtra.getProgram().getId(), true)) {
                                                    Optional<LevelDTO> level = programExtra.getLevelList().stream().filter(l -> l.getCondition().compareTo(membership.getTotalExpenditure()) <= 0).max(Comparator.comparing(LevelDTO::getCondition));
                                                    if (level.isPresent()) {
                                                        membership.setLevel(LevelMapper.INSTANCE.toEntity(level.get()));
                                                        membershipRepository.save(membership);
                                                    }
                                                }
                                            } catch (Exception e) {
                                                System.out.println(e.getMessage());
                                            }

                                            return programExtra;
                                        } else {
                                            throw new InvalidParameterException("Each program must have only one level condition equal to 0");
                                        }
                                    } else {
                                        throw new InvalidParameterException("The level list contains levels that duplicate the conditions");
                                    }
                                } else {
                                    throw new InvalidParameterException("The level condition cannot be negative");
                                }
                            } else {
                                throw new InvalidParameterException("Duplicate level id");
                            }
                        } else {
                            throw new InvalidParameterException("Invalid level");
                        }
                    } else {
                        throw new InvalidParameterException("The level cannot be empty");
                    }
                } else {
                    throw new InvalidParameterException("The program must have at least one level");
                }
            } else {
                throw new InvalidParameterException("The program name cannot be empty");
            }
        } else {
            throw new InvalidParameterException("Not found program");
        }
    }

    @Override
    public ProgramExtraDTO createProgram(ProgramCreationDTO creation, String token) {
        String userName;
        try {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
            userName = jwtTokenProvider.getUserNameFromJWT(token);
        } catch (ExpiredJwtException e) {
            throw new InvalidParameterException("Expired JWT token");
        }
        Optional<Partner> partner = partnerRepository.findPartnerByUserNameAndStatus(userName, true);
        if (partner.isPresent()) {
            if (!creation.getProgramName().isBlank()) {
                if (creation.getNumberOfWeek() > 0) {
                    if (!creation.getLevelList().isEmpty()) {
                        if (checkLevel(creation.getLevelList().stream().map(LevelCreationDTO::getLevel).toList())) {
                            List<BigDecimal> conditionList = creation.getLevelList().stream().map(LevelCreationDTO::getCondition).toList();
                            if (conditionList.stream().filter(c -> c.compareTo(BigDecimal.ZERO) < 0).toList().size() == 0) {
                                Set<BigDecimal> conditionSet = new HashSet<>(conditionList);
                                if (conditionList.size() == conditionSet.size()) {
                                    if (conditionList.stream().filter(c -> c.compareTo(BigDecimal.ZERO) == 0).toList().size() == 1) {
                                        //Create ProgramExtraDTO
                                        ProgramExtraDTO programExtra = new ProgramExtraDTO();
                                        programExtra.setNumOfMembers(0);
                                        programExtra.setPartner(PartnerMapper.INSTANCE.toDTO(partner.get()));

                                        //Create token
                                        String jwt;
                                        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
                                        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(partner.get().getUserName());
                                        jwt = jwtTokenProvider.generateToken(userDetails, 604800000L * creation.getNumberOfWeek());

                                        List<Program> programList = programRepository.findAllByStatusAndStateAndDateUpdatedBeforeAndPartnerId(true, true, LocalDate.now(), partner.get().getId());
                                        if (!programList.isEmpty()) {
                                            for (Program program : programList) {
                                                program.setState(false);
                                                programRepository.save(program);
                                            }
                                        }

                                        //Create program
                                        Program program = programRepository.save(new Program(null, creation.getProgramName(), creation.getDescription(), jwt, LocalDate.now(), LocalDate.now().plusWeeks(creation.getNumberOfWeek()), !programRepository.existsProgramByStatusAndStateAndPartnerId(true, true, partner.get().getId()), true, partner.get(), null, null));
                                        programExtra.setProgram(ProgramMapper.INSTANCE.toDTO(program));

                                        List<LevelDTO> levelDTOS = new ArrayList<>();
                                        for (LevelCreationDTO levelDTO : creation.getLevelList()) {
                                            //Create Level
                                            Level level = levelRepository.save(new Level(null, levelDTO.getLevel(), levelDTO.getCondition(), levelDTO.getDescription(), true, null, null));
                                            //Create program level
                                            programLevelRepository.save(new ProgramLevel(null, levelDTO.getDescription(), true, true, level, program));
                                            levelDTOS.add(LevelMapper.INSTANCE.toDTO(level));
                                        }
                                        programExtra.setLevelList(levelDTOS);

                                        return programExtra;
                                    } else {
                                        throw new InvalidParameterException("Each program must have only one level condition equal to 0");
                                    }
                                } else {
                                    throw new InvalidParameterException("The level list contains levels that duplicate the conditions");
                                }
                            } else {
                                throw new InvalidParameterException("The level condition cannot be negative");
                            }
                        } else {
                            throw new InvalidParameterException("The level cannot be empty");
                        }
                    } else {
                        throw new InvalidParameterException("The program must have at least one level");
                    }
                } else {
                    throw new InvalidParameterException("The duration of the program must last at least one week");
                }
            } else {
                throw new InvalidParameterException("The program name cannot be empty");
            }
        } else {
            throw new InvalidParameterException("Invalid partner");
        }
    }

    private boolean checkLevel(List<String> levels) {
        for (String level : levels) {
            if (level.isBlank()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ProgramDTO deleteProgram(Long programId, String token) {
        String userName;
        try {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
            userName = jwtTokenProvider.getUserNameFromJWT(token);
        } catch (ExpiredJwtException e) {
            throw new InvalidParameterException("Invalid JWT token");
        }
        Optional<Program> program = programRepository.getProgramByStatusAndId(true, programId, userName);
        if (program.isPresent()) {
            program.get().setState(false);
            program.get().setStatus(false);
            return ProgramMapper.INSTANCE.toDTO(programRepository.save(program.get()));
        } else {
            throw new InvalidParameterException("Not found program !");
        }
    }

    @Override
    public ProgramExtraDTO updateProgramState(boolean state, long programId, String token) {
        String userName;
        try {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
            userName = jwtTokenProvider.getUserNameFromJWT(token);
        } catch (ExpiredJwtException e) {
            throw new InvalidParameterException("Invalid JWT token");
        }
        Optional<Partner> partner = partnerRepository.findPartnerByUserNameAndStatus(userName, true);
        if (partner.isPresent()) {
            Optional<Program> program = programRepository.getProgramByStatusAndId(true, programId, userName);
            if (program.isPresent()) {
                if (program.get().getDateUpdated().isAfter(LocalDate.now())) {
                    if (state) {
                        List<Program> programsActive = programRepository.getAllByStateAndStatusAndPartnerId(true, true, partner.get().getId());
                        if (!programsActive.isEmpty()) {
                            for (Program p : programsActive) {
                                p.setState(false);
                                programRepository.save(p);
                            }
                        }
                    }

                    program.get().setState(state);
                    Program program1 = programRepository.save(program.get());
                    ProgramExtraDTO programExtraDTO = new ProgramExtraDTO();
                    programExtraDTO.setNumOfMembers(membershipRepository.countAllByStatusAndProgramId(true, program.get().getId()));
                    programExtraDTO.setProgram(ProgramMapper.INSTANCE.toDTO(program1));
                    programExtraDTO.setPartner(PartnerMapper.INSTANCE.toDTO(program1.getPartner()));
                    programExtraDTO.setLevelList(program1.getProgramLevelList().stream().map(ProgramLevel::getLevel).filter(l -> l.getStatus().equals(true)).map(LevelMapper.INSTANCE::toDTO).collect(Collectors.toList()));
                    return programExtraDTO;
                } else {
                    throw new InvalidParameterException("The program has expired !");
                }
            } else {
                throw new InvalidParameterException("Not found program !");
            }
        } else {
            throw new InvalidParameterException("Invalid partner !");
        }
    }
}
