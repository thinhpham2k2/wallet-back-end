package com.wallet.service;

import com.wallet.repository.ProgramRepository;
import com.wallet.service.interfaces.IProgramService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ProgramService implements IProgramService {

    private final ProgramRepository programRepository;

}
