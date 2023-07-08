package com.wallet.repository;

import com.wallet.entity.Program;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProgramRepository extends JpaRepository<Program, Long> {

    @Query("SELECT p FROM Program p " +
            "WHERE p.status = ?1 " +
            "AND (:#{#partnerId.size()} = 0 OR p.partner.id IN ?2) " +
            "AND (p.partner.fullName LIKE %?3% " +
            "OR p.programName LIKE %?3% " +
            "OR p.description LIKE %?3%)" +
            "ORDER BY p.state DESC ")
    Page<Program> getProgramList(boolean status, List<Long> partnerId, String search, Pageable pageable);

    @Query("SELECT p FROM Program p " +
            "WHERE p.status = ?1 " +
            "AND p.partner.userName = ?2 " +
            "AND (p.programName LIKE %?3% " +
            "OR p.description LIKE %?3%) " +
            "ORDER BY p.state DESC ")
    Page<Program> getProgramListForPartner(boolean status, String userName, String search, Pageable pageable);

    List<Program> findAllByStatusAndStateAndDateUpdatedBeforeAndPartnerId(boolean status, boolean state, LocalDate date, long partnerId);

    @Query("SELECT p FROM Program p " +
            "WHERE p.status = ?1 " +
            "AND p.state = ?1" +
            "AND p.partner.code = ?2 " +
            "AND p.dateUpdated >= ?3 " +
            "ORDER BY p.id DESC " +
            "FETCH FIRST 1 ROW ONLY")
    Optional<Program> getProgramToken(boolean status, String code, LocalDate now);

    Boolean existsProgramByStatusAndStateAndToken(boolean status, boolean state, String token);

    Boolean existsProgramByStatusAndState(boolean status, boolean state);

    Optional<Program> getProgramByStatusAndId(boolean status, long programId);

    @Query("SELECT p FROM Program p " +
            "WHERE p.status = ?1 " +
            "AND p.id = ?2 " +
            "AND p.partner.userName = ?3 ")
    Optional<Program> getProgramByStatusAndId(boolean status, long programId, String userName);

    Program getProgramByStatusAndToken(boolean status, String token);
}
