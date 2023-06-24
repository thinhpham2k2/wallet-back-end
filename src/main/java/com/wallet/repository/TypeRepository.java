package com.wallet.repository;

import com.wallet.entity.Type;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TypeRepository extends JpaRepository<Type, Long> {

    Optional<Type> findTypeByStatusAndId(boolean status, long id);
}
