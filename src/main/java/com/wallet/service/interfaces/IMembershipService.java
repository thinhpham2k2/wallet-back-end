package com.wallet.service.interfaces;

import com.wallet.dto.CustomerMembershipDTO;
import com.wallet.dto.CustomerProgramDTO;
import com.wallet.dto.MembershipDTO;
import com.wallet.dto.MembershipExtraDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IMembershipService {

    Page<MembershipDTO> getMemberList(boolean status, List<Long> partnerId, List<Long> programId, String search, String sort, int page, int limit);

    Page<MembershipDTO> getMemberListForPartner(boolean status, String token, List<Long> programId, String search, String sort, int page, int limit);

    CustomerMembershipDTO getCustomerMembershipInform(String token, String customerId);

    CustomerMembershipDTO createCustomerMembership(String token, CustomerProgramDTO customer);

    CustomerMembershipDTO createCustomer(String token, CustomerProgramDTO customer);

    CustomerMembershipDTO createMembership(String token, String customerId);

    MembershipExtraDTO getMemberById(String token, long memberId, boolean isAdmin);
}
