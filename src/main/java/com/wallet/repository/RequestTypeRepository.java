package com.wallet.repository;

import com.wallet.entity.RequestType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RequestTypeRepository extends JpaRepository<RequestType, Long> {

    Optional<RequestType> findRequestTypeByStatusAndId(boolean status, long id);
}
