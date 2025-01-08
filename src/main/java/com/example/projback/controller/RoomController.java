package com.example.projback.controller;

import com.example.projback.entity.Room;
import com.example.projback.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "*")
public class RoomController {
    @Autowired
    private RoomService roomService;

    @PostMapping("/employee/add")
    public ResponseEntity<Room> createRoom(@RequestBody Room room, @RequestHeader("Authorization") String token) {
        try {
            Room createdRoom = roomService.createRoom(room, token);
            return ResponseEntity.ok(createdRoom);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @GetMapping("/all")
    public List<Room> getAllRooms() {
        return roomService.getAllRooms();
    }

    @GetMapping("/customer/available")
    public ResponseEntity<List<Room>> getAvailableRooms(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date endTime) {
        try {
            List<Room> availableRooms = roomService.findAvailableRooms(startTime, endTime);
            return ResponseEntity.ok(availableRooms);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRoomById(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        try {
            Room room = roomService.getRoomById(id, token);
            return ResponseEntity.ok(room);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @DeleteMapping("/employee/delete/{id}")
    public ResponseEntity<?> deleteRoom(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        try {
            String deleted = roomService.deleteRoomById(id, token);
            return ResponseEntity.ok(deleted);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @PutMapping("/employee/update/{id}")
    public ResponseEntity<?> updateRoom(@PathVariable Long id, @RequestBody Room room, @RequestHeader("Authorization") String token) {
        try {
            Room updated = roomService.updateRoom(room, token, id);
            return ResponseEntity.ok(updated);
        }catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }
}
