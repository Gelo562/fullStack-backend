package com.example.projback.service;

import com.example.projback.config.JwtUtil;
import com.example.projback.entity.Equipment;
import com.example.projback.entity.Role;
import com.example.projback.entity.User;
import com.example.projback.repository.EquipmentRepository;
import com.example.projback.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipmentService {

    @Autowired
    private EquipmentRepository equipmentRepository;

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public EquipmentService(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    public Equipment getEquipmentById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Nieprawidłowy identyfikator wyposażenia.");
        }

        return equipmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono wyposażenia o podanym ID."));
    }

    public List<Equipment> getAllEquipment() {
        List<Equipment> equipmentList = equipmentRepository.findAll();

        if (equipmentList.isEmpty()) {
            throw new IllegalStateException("Nie znaleziono żadnego wyposażenia.");
        }

        return equipmentList;
    }

    public Equipment saveEquipment(Equipment equipment, String token) {
        String username = jwtUtil.extractUsername(token.substring(7));

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (jwtUtil.isTokenExpired(token.substring(7))) {
            throw new IllegalArgumentException("Token się przedawnil");
        }
        if(user.getRole() != Role.EMPLOYEE){
            throw new IllegalArgumentException("Tylko pracownik moze dodac wyposażenie");
        }
        if (equipment == null) {
            throw new IllegalArgumentException("Wyposażenie nie może być puste.");
        }
        if (equipment.getName() == null || equipment.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nazwa wyposażenia nie może być pusta.");
        }
        if (equipment.getPrice() == null || equipment.getPrice() < 0) {
            throw new IllegalArgumentException("Cena wyposażenia musi być większa lub równa 0.");
        }
        return equipmentRepository.save(equipment);
    }

    public String deleteEquipment(Long id, String token) {
        String username = jwtUtil.extractUsername(token.substring(7));

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (jwtUtil.isTokenExpired(token.substring(7))) {
            throw new IllegalArgumentException("Token się przedawnil");
        }
        if(user.getRole() != Role.EMPLOYEE){
            throw new IllegalArgumentException("Tylko pracownik moze usunac wyposażenie");
        }
        try {
            equipmentRepository.deleteById(id);
            return "Usunięto";
        }catch (Exception e){
            return "Cos poszło nie tak z usuwaniem.";
        }
    }

    public Equipment updateEquipment(Equipment equipment, String token, long id) {
        String username = jwtUtil.extractUsername(token.substring(7));

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (jwtUtil.isTokenExpired(token.substring(7))) {
            throw new IllegalArgumentException("Token się przedawnil");
        }
        if(user.getRole() != Role.EMPLOYEE){
            throw new IllegalArgumentException("Tylko pracownik moze edytowac wyposażenie");
        }
        if (equipment == null) {
            throw new IllegalArgumentException("Wyposażenie nie może być puste.");
        }
        if (equipment.getName() == null || equipment.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nazwa wyposażenia nie może być pusta.");
        }
        if (equipment.getPrice() == null || equipment.getPrice() < 0) {
            throw new IllegalArgumentException("Cena wyposażenia musi być większa lub równa 0.");
        }

        Equipment currentEquipment = getEquipmentById(id);

        currentEquipment.setName(equipment.getName());
        currentEquipment.setPrice(equipment.getPrice());
        currentEquipment.setDescription(equipment.getDescription());
        currentEquipment.setName(equipment.getName());

        return equipmentRepository.save(currentEquipment);
    }
}

