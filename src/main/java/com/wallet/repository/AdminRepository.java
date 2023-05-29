package com.wallet.repository;

import com.wallet.entity.Admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findAdminByUserNameAndStatus(String userName, boolean status);

    Optional<Admin> findAdminByIdAndStatus(Long id, boolean status);

    Optional<Admin> findAdminByEmail(String email);

    Boolean existsAdminByPhoneAndIdNot(String phone, Long id);

    Boolean existsAdminByUserName(String userName);

    Boolean existsAdminByEmail(String email);

    Page<Admin> findAdminsByStatus(boolean status, Pageable pageable);
}