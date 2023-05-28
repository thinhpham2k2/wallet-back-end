package com.wallet.service;

import com.wallet.repository.LevelRepository;
import com.wallet.service.interfaces.ILevelService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class LevelService implements ILevelService {

    private final LevelRepository levelRepository;

}
