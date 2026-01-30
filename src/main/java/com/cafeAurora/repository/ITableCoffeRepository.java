package com.cafeAurora.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cafeAurora.model.TableCoffe;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ITableCoffeRepository extends JpaRepository<TableCoffe, Integer>{
    @Query("""
    SELECT t
    FROM TableCoffe t
    WHERE t.isAvailable = true
    AND t.status = 'DISPONIBLE'
    AND t.idTable NOT IN (
        SELECT DISTINCT r.table.idTable
        FROM Reservation r
        WHERE r.table IS NOT NULL
        AND r.reservationDate = :date
        AND r.status = 'CONFIRMADA'
        AND r.reservationTime BETWEEN :startTime AND :endTime
    )
""")
    List<TableCoffe> findAvailableTables(
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );
}
