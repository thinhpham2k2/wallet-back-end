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
            programExtra.setLevelList(programLevelRepository.getProgramLevelByStatusAndProgramId(true, id).stream().map(ProgramLevel::getLevel).map(LevelMapper.INSTANCE::toDTO).collect(Collectors.toList()));
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
                                        if(!programList.isEmpty()) {
                                            for (Program program:programList) {
                                                program.setState(false);
                                                programRepository.save(program);
                                            }
                                        }

                                        //Create program
                                        Program program = programRepository.save(new Program(null, creation.getProgramName(), creation.getDescription(), jwt, LocalDate.now(), LocalDate.now().plusWeeks(creation.getNumberOfWeek()), !programRepository.existsProgramByStatusAndState(true, true), true, partner.get(), null, null));
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
}
