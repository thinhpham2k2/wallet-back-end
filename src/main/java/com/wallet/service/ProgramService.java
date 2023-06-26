package com.wallet.service;

import com.wallet.dto.ProgramDTO;
import com.wallet.dto.ProgramExtraDTO;
import com.wallet.entity.Program;
import com.wallet.entity.ProgramLevel;
import com.wallet.jwt.JwtTokenProvider;
import com.wallet.mapper.LevelMapper;
import com.wallet.mapper.PartnerMapper;
import com.wallet.mapper.ProgramMapper;
import com.wallet.repository.MembershipRepository;
import com.wallet.repository.ProgramLevelRepository;
import com.wallet.repository.ProgramRepository;
import com.wallet.service.interfaces.IPagingService;
import com.wallet.service.interfaces.IProgramService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProgramService implements IProgramService {

    private final ProgramRepository programRepository;

    private final ProgramLevelRepository programLevelRepository;

    private final MembershipRepository membershipRepository;

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
                    .map(ProgramLevel::getLevel).map(LevelMapper.INSTANCE::toDTO).collect(Collectors.toList()));
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
    public String getProgramTokenByPartnerCode(String code) {
        Optional<Program> program = programRepository.getProgramToken(true, code).stream().findFirst();
        return program.map(Program::getToken).orElse(null);
    }
}
