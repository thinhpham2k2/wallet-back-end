package com.wallet.service.interfaces;

import com.wallet.dto.CustomerDTO;
import com.wallet.dto.CustomerExtraDTO;
import com.wallet.dto.CustomerUpdateDTO;
import com.wallet.dto.TitleDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ICustomerService {

    Page<CustomerDTO> getCustomerList(boolean status, List<Long> partnerId, String search, String sort, int page, int limit);

    Page<CustomerDTO> getCustomerListForPartner(boolean status, String token, String search, String sort, int page, int limit);

    CustomerExtraDTO getCustomerById(String token, long id, boolean isAdmin);

    CustomerExtraDTO updateCustomer(CustomerUpdateDTO customerUpdate, long customerId, String token);

    CustomerExtraDTO deleteCustomer(long customerId, String token);

    TitleDTO getTitle(String token);
}
