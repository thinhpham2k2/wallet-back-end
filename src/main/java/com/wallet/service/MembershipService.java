package com.wallet.service;

import com.wallet.repository.MembershipRepository;
import com.wallet.service.interfaces.IMembershipService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class MembershipService implements IMembershipService {

    private final MembershipRepository membershipRepository;

}
