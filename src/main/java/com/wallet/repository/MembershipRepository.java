package com.wallet.repository;

import com.wallet.entity.Membership;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MembershipRepository extends JpaRepository<Membership, Long> {


    @Query("SELECT m FROM Membership m " +
            "WHERE m.status = ?1 " +
            "AND (:#{#partnerId.size()} = 0 OR m.customer.partner.id IN ?2) " +
            "AND (:#{#programId.size()} = 0 OR m.program.id IN ?3) " +
            "AND (m.program.programName LIKE %?4% " +
            "OR m.customer.fullName LIKE %?4% " +
            "OR m.customer.partner.fullName LIKE %?4% " +
            "OR m.customer.partner.code LIKE %?4%)")
    Page<Membership> getMemberList(boolean status, List<Long> partnerId, List<Long> programId, String search, Pageable pageable);
}
