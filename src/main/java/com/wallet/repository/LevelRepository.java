package com.wallet.repository;

import com.wallet.entity.Level;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LevelRepository extends JpaRepository<Level, Long> {

    Page<Level> findAllByStatus(boolean status, Pageable pageable);

    Optional<Level> findLevelByStatusAndId(boolean status, long id);
}
