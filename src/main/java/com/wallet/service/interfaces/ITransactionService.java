package com.wallet.service.interfaces;

import com.wallet.dto.ChartDTO;

import java.time.LocalDate;
import java.util.List;

public interface ITransactionService {

    List<ChartDTO> getTransactionByPartner(LocalDate fromDate, LocalDate toDate, String sort, String token);
}
