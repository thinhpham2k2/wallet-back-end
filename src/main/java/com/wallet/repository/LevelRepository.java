package com.wallet.repository;

import com.wallet.entity.Level;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LevelRepository extends JpaRepository<Level, Long> {

    Page<Level> findAllByStatus(boolean status, Pageable pageable);
}
