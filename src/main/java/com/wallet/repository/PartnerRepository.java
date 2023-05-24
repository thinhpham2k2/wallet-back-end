package com.wallet.repository;

import com.wallet.entity.Partner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PartnerRepository extends JpaRepository<Partner, Long> {

    Optional<Partner> findPartnerByUserNameAndStatus(String userName, boolean status);

    List<Partner> findPartnersByStatus(boolean status);
}
