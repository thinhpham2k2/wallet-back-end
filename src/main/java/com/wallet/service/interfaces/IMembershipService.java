package com.wallet.service.interfaces;

import com.wallet.dto.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IMembershipService {

    Page<MembershipDTO> getMemberList(boolean status, List<Long> partnerId, List<Long> programId, String search, String sort, int page, int limit);

    Page<MembershipDTO> getMemberListForPartner(boolean status, String token, List<Long> programId, String search, String sort, int page, int limit);

    CustomerMembershipDTO getCustomerMembershipInform(String token, String customerId);

    CustomerMembershipDTO createCustomerMembership(String token, CustomerProgramDTO customer);

    CustomerMembershipDTO createCustomerMembershipWeb(String token, CustomerProgramWebDTO customer);

    CustomerMembershipDTO createCustomer(String token, CustomerProgramDTO customer);

    CustomerMembershipDTO createCustomerWeb(String token, CustomerProgramWebDTO customer);

    CustomerMembershipDTO createMembership(String token, String customerId);

    MembershipExtraDTO getMemberById(String token, long memberId, boolean isAdmin);

    MembershipExtraDTO deleteMember(String token, long memberId);
}
