package com.wallet.repository;

import com.wallet.entity.ProgramLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface ProgramLevelRepository extends JpaRepository<ProgramLevel, Long> {

    @Query("SELECT p FROM ProgramLevel p " +
            "WHERE p.status = ?1 " +
            "AND p.program.status = ?1 " +
            "AND p.program.token = ?2 " +
            "AND p.level.status = ?1 " +
            "AND p.level.condition > ?3 " +
            "ORDER BY p.level.condition ASC " +
            "FETCH FIRST 1 ROW ONLY")
    ProgramLevel getNextLevel(boolean status, String token, BigDecimal condition);

    @Query("SELECT p FROM ProgramLevel p " +
            "WHERE p.status = ?1 " +
            "AND p.program.status = ?1 " +
            "AND p.program.token = ?2 " +
            "AND p.level.status = ?1 " +
            "ORDER BY p.level.condition ASC")
    List<ProgramLevel> getLeveListByProgramToken(boolean status, String token);
}
