package com.wallet.repository;

import com.wallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findWalletByStatusAndId(boolean status, long id);

    Optional<Wallet> findFirstByStatusAndTypeIdAndMembershipId(boolean status, long id, long membershipId);

    @Query("SELECT w FROM Wallet w " +
            "WHERE w.status = ?1 " +
            "AND w.membership.customer.customerId = ?2 " +
            "AND w.membership.program.token = ?3")
    List<Wallet> findAllByProgramTokenAndCustomerId(boolean status, String customerId, String token);
}
