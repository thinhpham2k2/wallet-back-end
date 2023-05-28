package com.wallet.service;

import com.wallet.repository.RequestRepository;
import com.wallet.service.interfaces.IRequestService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class RequestService implements IRequestService {

    private final RequestRepository requestRepository;

}
