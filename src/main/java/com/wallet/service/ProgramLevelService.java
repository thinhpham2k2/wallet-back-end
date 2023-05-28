package com.wallet.service;

import com.wallet.repository.ProgramLevelRepository;
import com.wallet.service.interfaces.IProgramLevelService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ProgramLevelService implements IProgramLevelService {

    private final ProgramLevelRepository pLevelRepository;

}
