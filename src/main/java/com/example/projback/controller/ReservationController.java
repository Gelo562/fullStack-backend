package com.example.projback.controller;

import com.example.projback.config.JwtUtil;
import com.example.projback.dto.MakeReservationDTO;
import com.example.projback.dto.UpdateReservationDTO;
import com.example.projback.dto.UpdateReservationEmployeeDTO;
import com.example.projback.entity.*;
import com.example.projback.service.ReservationService;
import com.example.projback.service.RoomService;
import com.example.projback.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;

    @PostMapping("/customer/add")
    public ResponseEntity<String> createReservation(@RequestBody MakeReservationDTO reservation, @RequestHeader("Authorization") String token) {
        try {
            reservationService.createReservation(reservation, token);
            return ResponseEntity.ok("Reservation applied");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @DeleteMapping("/customer/delete/{reservationId}")
    public ResponseEntity<String> deleteReservation(@PathVariable Long reservationId, @RequestHeader("Authorization") String token) {
        try {
            reservationService.deleteReservation(reservationId, token);
            return ResponseEntity.ok("Reservation deleted");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PutMapping("/customer/update/{reservationId}")
    public ResponseEntity<String> updateReservation(
            @PathVariable Long reservationId,
            @RequestBody UpdateReservationDTO updateReservation,
            @RequestHeader("Authorization") String token) {
        try {
            reservationService.updateReservation(reservationId, updateReservation, token);
            return ResponseEntity.ok("Reservation updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }


    @GetMapping("/customer/get/{id}")
    public ResponseEntity<?> getCustomerReservationById(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        try {
            Reservation reservation = reservationService.getCustomerReservationById(id, token);
            return ResponseEntity.ok(reservation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("/customer/getAll")
    public List<Reservation> getReservationsByUser(@RequestHeader("Authorization") String token) {
        return reservationService.getReservationsByUser(token);
    }

    @PutMapping("/employee/update/{reservationId}")
    public ResponseEntity<String> updateReservationStatusAndPrice(
            @PathVariable Long reservationId,
            @RequestBody UpdateReservationEmployeeDTO updateReservation,
            @RequestHeader("Authorization") String token) {
        try {
            reservationService.updateReservationStatusAndPrice(reservationId, updateReservation, token);
            return ResponseEntity.ok("Reservation updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("/employee/reservations")
    public ResponseEntity<List<Reservation>> getReservations(
            @RequestParam(required = false) ReservationStatus status,
            @RequestHeader("Authorization") String token) {
        try {
            List<Reservation> reservations = reservationService.getReservationsByStatusOrAll(status, token);
            return ResponseEntity.ok(reservations);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @GetMapping("/employee/get/{id}")
    public ResponseEntity<?> getEmployeeReservationById(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        try {
            Reservation reservation = reservationService.getEmployeeReservationById(id, token);
            return ResponseEntity.ok(reservation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }


}
