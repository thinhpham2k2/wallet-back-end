package com.wallet.service.interfaces;

import com.wallet.dto.CustomerMembershipDTO;
import com.wallet.dto.MembershipDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IMembershipService {

    Page<MembershipDTO> getMemberList(boolean status, List<Long> partnerId, List<Long> programId, String search, String sort, int page, int limit);

    CustomerMembershipDTO getCustomerMembershipInform(String token, String customerId);
}
