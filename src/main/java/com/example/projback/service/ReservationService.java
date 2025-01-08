package com.example.projback.service;

import com.example.projback.config.JwtUtil;
import com.example.projback.dto.MakeReservationDTO;
import com.example.projback.dto.UpdateReservationDTO;
import com.example.projback.dto.UpdateReservationEmployeeDTO;
import com.example.projback.entity.*;
import com.example.projback.repository.ReservationRepository;
import com.example.projback.repository.RoomRepository;
import com.example.projback.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private JwtUtil jwtUtil;

    public void createReservation(MakeReservationDTO reservation, String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (jwtUtil.isTokenExpired(token.substring(7))) {
            throw new RuntimeException("Token is expired");
        }

        Room room = roomService.findRoomById(reservation.getRoom());
        boolean available = roomService.findAvailableRooms(reservation.getStartTime(), reservation.getEndTime()).contains(room);
        if (!available) {
            throw new RuntimeException("The date is taken");
        }

        Reservation reservationEntity = new Reservation();
        reservationEntity.setRoom(room);
        reservationEntity.setStartTime(reservation.getStartTime());
        reservationEntity.setEndTime(reservation.getEndTime());
        reservationEntity.setEstimatedPrice(reservation.getEstimatedPrice());
        reservationEntity.setUser(user);
        reservationEntity.setStatus(ReservationStatus.PENDING);
        reservationEntity.setAdditionalEquipment(reservation.getAdditionalEquipment());

        reservationRepository.save(reservationEntity);
    }

    public Reservation validateReservation(String token, Long reservationId) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (jwtUtil.isTokenExpired(token.substring(7))) {
            throw new RuntimeException("Token is expired");
        }

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (!reservation.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not the owner of this reservation");
        }
        return reservation;
    }

    public void deleteReservation(Long reservationId, String token) {
//        String username = jwtUtil.extractUsername(token.substring(7));
//        User user = userService.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        if (jwtUtil.isTokenExpired(token.substring(7))) {
//            throw new RuntimeException("Token is expired");
//        }
//
//        Reservation reservation = reservationRepository.findById(reservationId)
//                .orElseThrow(() -> new RuntimeException("Reservation not found"));
//
//        if (!reservation.getUser().getId().equals(user.getId())) {
//            throw new RuntimeException("You are not the owner of this reservation");
//        }
        Reservation reservation = validateReservation(token, reservationId);

        reservationRepository.deleteById(reservationId);
    }

    public void updateReservation(Long reservationId, UpdateReservationDTO updateReservation, String token) {
        Reservation reservation = validateReservation(token, reservationId);

        updateReservationFields(reservation, updateReservation);
        reservationRepository.save(reservation);
    }

    public void updateReservationStatusAndPrice(Long reservationId, UpdateReservationEmployeeDTO updateReservation, String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getRole().equals(Role.EMPLOYEE)) {
            throw new RuntimeException("You are not authorized to perform this action");
        }

        if (jwtUtil.isTokenExpired(token.substring(7))) {
            throw new RuntimeException("Token is expired");
        }

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (updateReservation.getReservationStatus() != null) {
            reservation.setStatus(updateReservation.getReservationStatus());
        }

        if (updateReservation.getFinalPrice() != null) {
            if (updateReservation.getFinalPrice() < 0) {
                throw new IllegalArgumentException("Final price cannot be negative");
            }
            reservation.setFinalPrice(updateReservation.getFinalPrice());
        }

        reservationRepository.save(reservation);
    }

    public List<Reservation> getReservationsByStatusOrAll(ReservationStatus status, String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getRole().equals(Role.EMPLOYEE)) {
            throw new RuntimeException("You are not authorized to view reservations");
        }

        if (jwtUtil.isTokenExpired(token.substring(7))) {
            throw new RuntimeException("Token is expired");
        }

        return status != null ? reservationRepository.findByStatus(status) : reservationRepository.findAll();
    }

    public List<Reservation> getReservationsByUser(String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return reservationRepository.findByUserId(user.getId());
    }

    private void updateReservationFields(Reservation reservation, UpdateReservationDTO updateReservation) {
        if (updateReservation.getStartTime() != null) {
            reservation.setStartTime(updateReservation.getStartTime());
        }

        if (updateReservation.getEndTime() != null) {
            reservation.setEndTime(updateReservation.getEndTime());
        }

        if (updateReservation.getEquipment() != null) {
            reservation.setAdditionalEquipment(updateReservation.getEquipment());
        }
    }

    public Reservation getCustomerReservationById(Long reservationId, String token) {
        if (jwtUtil.isTokenExpired(token.substring(7))) {
            throw new RuntimeException("Token is expired");
        }

        String username = jwtUtil.extractUsername(token.substring(7));
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return reservationRepository.findById(reservationId)
                .filter(reservation -> reservation.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Reservation not found or access denied"));
    }

    public Reservation getEmployeeReservationById(Long reservationId, String token) {
        if (jwtUtil.isTokenExpired(token.substring(7))) {
            throw new RuntimeException("Token is expired");
        }

        String username = jwtUtil.extractUsername(token.substring(7));
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getRole().equals(Role.EMPLOYEE)) {
            throw new RuntimeException("Access denied. User is not an employee.");
        }

        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
    }

}
