package com.wallet.service.interfaces;

import com.wallet.dto.ProgramDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IProgramService {

    Page<ProgramDTO> getProgramList(boolean status, List<Long> partnerId, String search, String sort, int page, int limit);

    Page<ProgramDTO> getProgramListForPartner(boolean status, String token, String search, String sort, int page, int limit);
}
