package com.wallet.repository;

import com.wallet.entity.Partner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PartnerRepository extends JpaRepository<Partner, Long> {

    Optional<Partner> findPartnerByUserNameAndStatus(String userName, boolean status);

    Optional<Partner> findPartnerByEmailAndStatus(String email, boolean status);

    Boolean existsPartnerByEmail(String email);

    Page<Partner> findPartnersByStatus(boolean status, Pageable pageable);
}
