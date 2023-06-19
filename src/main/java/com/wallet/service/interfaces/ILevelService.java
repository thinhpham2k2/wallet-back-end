package com.wallet.service.interfaces;

import com.wallet.dto.LevelDTO;
import org.springframework.data.domain.Page;

public interface ILevelService {

    Page<LevelDTO> getLevelList(String sort, int page, int limit);

}
