package com.wallet.service;

import com.wallet.repository.TypeRepository;
import com.wallet.service.interfaces.ITypeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class TypeService implements ITypeService {

    private final TypeRepository typeRepository;

}
