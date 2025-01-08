package com.example.projback.service;

import com.example.projback.config.JwtUtil;
import com.example.projback.entity.Reservation;
import com.example.projback.entity.Role;
import com.example.projback.entity.Room;
import com.example.projback.entity.User;
import com.example.projback.repository.ReservationRepository;
import com.example.projback.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;

    private final ReservationRepository reservationRepository;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public RoomService(ReservationRepository reservationRepository, UserService userService, JwtUtil jwtUtil) {
        this.reservationRepository = reservationRepository;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    public Room createRoom(Room room, String token) {
        String username = jwtUtil.extractUsername(token.substring(7));

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (jwtUtil.isTokenExpired(token.substring(7))) {
            throw new RuntimeException("Token się przedawnił");
        }

        if (user.getRole() != Role.EMPLOYEE) {
            throw new RuntimeException("You are not authorized to create rooms");
        }

        // Możesz dodać dodatkowe walidacje dla `room` tutaj:
        // Przykład: sprawdzanie, czy nazwa pokoju nie jest pusta
        if (room.getName() == null || room.getName().isEmpty()) {
            throw new IllegalArgumentException("Room name cannot be empty");
        }

        return roomRepository.save(room);
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public List<Room> findAvailableRooms(Date startTime, Date endTime) {
        if (startTime.after(endTime)) {
            throw new IllegalArgumentException("Start time cannot be after end time");
        }

        return roomRepository.findAvailableRooms(startTime, endTime);
    }

    public Room findRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));
    }

    public boolean isEquipmentAvailable(Long roomId, List<Long> equipment) {
        Room room = findRoomById(roomId);

        // Możesz dodać dodatkowe zabezpieczenia:
        // Przykład: sprawdzanie, czy lista wyposażenia nie jest pusta
        if (equipment == null || equipment.isEmpty()) {
            throw new IllegalArgumentException("Equipment list cannot be empty");
        }

        return room.getOptionalEquipment().containsAll(equipment);
    }

    public boolean isRoomAvailableIgnoringReservation(Long roomId, Date startTime, Date endTime, Long reservationIdToIgnore) {
        if (startTime.after(endTime)) {
            throw new IllegalArgumentException("Start time cannot be after end time");
        }

        List<Reservation> conflictingReservations = reservationRepository.findReservationsForRoom(
                roomId, startTime, endTime);

        // Ignorowanie rezerwacji o podanym ID
        return conflictingReservations.stream()
                .noneMatch(reservation -> !reservation.getId().equals(reservationIdToIgnore));
    }

    public Room getRoomById(Long roomId, String token) {
        if (jwtUtil.isTokenExpired(token.substring(7))) {
            throw new RuntimeException("Token się przedawnił");
        }

        return roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
    }

    public String deleteRoomById(Long roomId, String token) {
        String username = jwtUtil.extractUsername(token.substring(7));

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (jwtUtil.isTokenExpired(token.substring(7))) {
            throw new RuntimeException("Token się przedawnił.");
        }

        if (user.getRole() != Role.EMPLOYEE) {
            throw new RuntimeException("You are not authorized to delete rooms");
        }
        try {
            roomRepository.deleteById(roomId);
            return "Usunięto";
        }catch (Exception e){
            return "Cos poszło nie tak z usuwaniem.";
        }
    }

    public Room updateRoom(Room room, String token, Long roomId) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (jwtUtil.isTokenExpired(token.substring(7))) {
            throw new RuntimeException("Token się przedawnił.");
        }

        if (user.getRole() != Role.EMPLOYEE) {
            throw new RuntimeException("You are not authorized to delete rooms");
        }

        if(room == null) {
            throw new IllegalArgumentException("Room cannot be null");
        }
        if (room.getName() == null || room.getName().isEmpty()) {
            throw new IllegalArgumentException("Room name cannot be empty");
        }
        if(room.getPricePerDay() ==null || room.getPricePerDay() <= 0) {
            throw new IllegalArgumentException("Price per day cannot be empty");
        }
        if(room.getPricePerHour() == null || room.getPricePerHour() <= 0) {
            throw new IllegalArgumentException("Price per hour cannot be empty");
        }
        if(room.getType() == null) {
            throw new IllegalArgumentException("Room type cannot be empty");
        }

        Room currentRoom = findRoomById(roomId);
        currentRoom.setName(room.getName());
        currentRoom.setBaseEquipment(room.getBaseEquipment());
        currentRoom.setPricePerDay(room.getPricePerDay());
        currentRoom.setPricePerHour(room.getPricePerHour());
        currentRoom.setType(room.getType());
        currentRoom.setOptionalEquipment(room.getOptionalEquipment());

        return roomRepository.save(currentRoom);
    }
}

