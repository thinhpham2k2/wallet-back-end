package com.wallet.repository;

import com.wallet.entity.Request;
import com.wallet.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT DISTINCT t.request FROM Transaction t " +
            "WHERE t.status = ?1 " +
            "AND t.request.status = ?1" +
            "AND t.wallet.id IN ?2 ")
    List<Request> findAllRequestByWalletId(boolean status, List<Long> walletIds);

    @Query("SELECT COUNT(t) FROM Transaction t " +
            "WHERE t.status = ?1 AND t.request.partner.id = ?2")
    Long countAllByStatusAndPartner(boolean status, Long partnerId);

    List<Transaction> getTransactionByDateCreatedBetweenAndStatusAndRequest_PartnerId(LocalDate fromDate, LocalDate toDate, boolean status, long partnerId);
}
