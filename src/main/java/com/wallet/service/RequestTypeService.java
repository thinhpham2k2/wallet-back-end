package com.wallet.service;

import com.wallet.repository.RequestTypeRepository;
import com.wallet.service.interfaces.IRequestTypeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class RequestTypeService implements IRequestTypeService {

    private final RequestTypeRepository requestTypeRepository;

}
