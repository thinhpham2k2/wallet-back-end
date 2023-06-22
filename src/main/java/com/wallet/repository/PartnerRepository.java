package com.wallet.repository;

import com.wallet.entity.Partner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PartnerRepository extends JpaRepository<Partner, Long> {

    Optional<Partner> findPartnerByUserNameAndStatus(String userName, boolean status);

    Optional<Partner> findPartnerByEmail(String email);

    Optional<Partner> findPartnerByIdAndStatus(Long id, boolean status);

    Optional<Partner> findPartnerById(Long id);

    Boolean existsPartnerByEmail(String email);

    Boolean existsPartnerByUserName(String username);

    Boolean existsPartnerByCode(String code);

    @Query("SELECT p FROM Partner p " +
            "WHERE p.status = ?1 " +
            "AND (p.fullName LIKE %?2% " +
            "OR p.code LIKE %?2% " +
            "OR p.email LIKE %?2% " +
            "OR p.address LIKE %?2%)")
    Page<Partner> getPartnerList(boolean status, String search, Pageable pageable);

    Optional<Partner> getPartnerByUserNameAndStatus(String userName, boolean status);
}
