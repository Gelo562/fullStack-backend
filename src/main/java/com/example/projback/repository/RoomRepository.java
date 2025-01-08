package com.example.projback.repository;

import com.example.projback.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("SELECT r FROM Room r WHERE NOT EXISTS (" +
            "SELECT res FROM Reservation res WHERE res.room = r AND " +
            "(res.startTime <= :endTime AND res.endTime >= :startTime))")
    List<Room> findAvailableRooms(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    Room findRoomById(Long id);
}
