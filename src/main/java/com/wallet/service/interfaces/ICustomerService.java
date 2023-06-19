package com.wallet.service.interfaces;

import com.wallet.dto.CustomerDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ICustomerService {

    Page<CustomerDTO> getCustomerList(boolean status, List<Long> partnerId, String search, String sort, int page, int limit);

    Page<CustomerDTO> getCustomerListForPartner(boolean status, String token, String search, String sort, int page, int limit);

}
