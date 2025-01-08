package com.example.projback.repository;

import com.example.projback.entity.Reservation;
import com.example.projback.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserId(Long userId);
    @Query("SELECT r FROM Reservation r WHERE r.room.id = :roomId " +
            "AND r.endTime > :startTime AND r.startTime < :endTime")
    List<Reservation> findReservationsForRoom(@Param("roomId") Long roomId,
                                              @Param("startTime") Date startTime,
                                              @Param("endTime") Date endTime);


    List<Reservation> findByStatus(ReservationStatus status);
}
