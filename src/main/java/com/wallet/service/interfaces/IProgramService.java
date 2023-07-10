package com.wallet.service.interfaces;

import com.wallet.dto.ProgramCreationDTO;
import com.wallet.dto.ProgramDTO;
import com.wallet.dto.ProgramExtraDTO;
import com.wallet.dto.ProgramUpdateDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IProgramService {

    Page<ProgramDTO> getProgramList(boolean status, List<Long> partnerId, String search, String sort, int page, int limit);

    Page<ProgramDTO> getProgramListForPartner(boolean status, String token, String search, String sort, int page, int limit);

    ProgramExtraDTO getProgramById(String token, long id, boolean isAdmin);

    String getProgramTokenActiveByPartnerCode(String code);

    ProgramExtraDTO createProgram(ProgramCreationDTO creation, String token);

    ProgramExtraDTO updateProgram(ProgramUpdateDTO update, String token);

    ProgramExtraDTO updateProgramState(boolean state, long programId, String token);

    ProgramDTO deleteProgram(Long programId, String token);
}
