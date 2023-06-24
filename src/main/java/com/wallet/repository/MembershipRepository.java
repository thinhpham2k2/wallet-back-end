package com.wallet.repository;

import com.wallet.entity.Membership;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

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

    @Query("SELECT m FROM Membership m " +
            "WHERE m.status = ?1 " +
            "AND m.program.partner.userName = ?2 " +
            "AND (:#{#programId.size()} = 0 OR m.program.id IN ?3) " +
            "AND (m.program.programName LIKE %?4% " +
            "OR m.customer.fullName LIKE %?4% )")
    Page<Membership> getMemberListForPartner(boolean status, String userName, List<Long> programId, String search, Pageable pageable);

    @Query("SELECT m FROM Membership m " +
            "WHERE m.status = ?1 " +
            "AND m.program.status = ?1 " +
            "AND m.program.token = ?2 " +
            "AND m.customer.status = ?1 " +
            "AND m.customer.customerId = ?3")
    Membership getCustomerMembershipInform(boolean status, String token, String customerId);

    Integer countAllByStatusAndProgramId(boolean status, long programId);

    @Query("SELECT m FROM Membership m " +
            "WHERE m.status = ?1 " +
            "AND m.id = ?2 ")
    Optional<Membership> findByStatusAndId(boolean status, long memberId);

    @Query("SELECT m FROM Membership m " +
            "WHERE m.status = ?1 " +
            "AND m.id = ?2 " +
            "AND m.customer.partner.userName = ?3")
    Optional<Membership> findByStatusAndId(boolean status, long memberId, String userName);

    @Query("SELECT m FROM Membership m " +
            "WHERE m.status = ?1 " +
            "AND m.customer.customerId = ?2 " +
            "AND m.program.token = ?3")
    Optional<Membership> findByCustomerIdAndStatus(boolean status, String customerId, String token);
}
