package com.wallet.service;

import com.wallet.dto.ChartDTO;
import com.wallet.entity.Partner;
import com.wallet.entity.Transaction;
import com.wallet.jwt.JwtTokenProvider;
import com.wallet.repository.PartnerRepository;
import com.wallet.repository.TransactionRepository;
import com.wallet.service.interfaces.ITransactionService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class TransactionService implements ITransactionService {

    private final TransactionRepository transactionRepository;

    private final PartnerRepository partnerRepository;

    @Override
    public List<ChartDTO> getTransactionByPartner(LocalDate fromDate, LocalDate toDate, String sort, String token) {
        String userName;
        try {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
            userName = jwtTokenProvider.getUserNameFromJWT(token);
        } catch (ExpiredJwtException e) {
            throw new InvalidParameterException("Invalid JWT token");
        }
        Optional<Partner> partner = partnerRepository.findPartnerByUserNameAndStatus(userName, true);
        if (partner.isPresent()) {
            List<ChartDTO> charts = new ArrayList<>();
            List<Transaction> transactions = transactionRepository.getTransactionByDateCreatedBetweenAndStatusAndRequest_PartnerId(fromDate, toDate, true, partner.get().getId());
            LocalDate currentDate = fromDate;
            while (!currentDate.isAfter(toDate)) {
                // Xử lý logic của bạn với currentDate
                LocalDate finalCurrentDate = currentDate;
                charts.add(new ChartDTO(finalCurrentDate,
                        transactions.stream().filter(t -> t.getDateCreated().isEqual(finalCurrentDate) && t.getType().getType())
                                .map(Transaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add),
                        transactions.stream().filter(t -> t.getDateCreated().isEqual(finalCurrentDate) && !t.getType().getType())
                                .map(Transaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add)));
                // Tăng ngày hiện tại lên 1
                currentDate = currentDate.plusDays(1);
            }
            if (sort.equals("asc")) {
                charts.sort(Comparator.comparing(chart -> chart.getTotalReceipt().add(chart.getTotalExpenditure())));
            } else if (sort.equals("desc")) {
                charts.sort(Comparator.comparing((ChartDTO chart) -> chart.getTotalReceipt().add(chart.getTotalExpenditure())).reversed());
            }
            return charts;
        } else {
            throw new InvalidParameterException("Not found partner");
        }
    }
}
