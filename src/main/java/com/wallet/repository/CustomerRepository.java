package com.wallet.repository;

import com.wallet.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("SELECT c FROM Customer c " +
            "WHERE c.status = ?1 " +
            "AND (:#{#partnerId.size()} = 0 OR c.partner.id IN ?2) " +
            "AND (c.partner.fullName LIKE %?3% " +
            "OR c.fullName LIKE %?3% " +
            "OR c.email LIKE %?3% " +
            "OR c.phone LIKE %?3%)")
    Page<Customer> getCustomerList(boolean status, List<Long> partnerId, String search, Pageable pageable);

    @Query("SELECT c FROM Customer c " +
            "WHERE c.status = ?1 " +
            "AND c.partner.userName = ?2 " +
            "AND (c.partner.fullName LIKE %?3% " +
            "OR c.fullName LIKE %?3% " +
            "OR c.email LIKE %?3% " +
            "OR c.phone LIKE %?3%)")
    Page<Customer> getCustomerListForPartner(boolean status, String userName, String search, Pageable pageable);

    Integer countAllByStatusAndPartnerId(boolean status, long partnerId);

    @Query("SELECT c FROM Customer c " +
            "WHERE c.status = ?1 " +
            "AND c.id = ?2 ")
    Optional<Customer> findByStatusAndId(boolean status, long id);

    @Query("SELECT c FROM Customer c " +
            "WHERE c.status = ?1 " +
            "AND c.id = ?2 " +
            "AND c.partner.userName = ?3")
    Optional<Customer> findByStatusAndId(boolean status, long id, String userName);
}
