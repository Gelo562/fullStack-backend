package com.example.projback.controller;

import com.example.projback.entity.Equipment;
import com.example.projback.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipment")
@CrossOrigin(origins = "*")
public class EquipmentController {
    @Autowired
    private EquipmentService equipmentService;

    @GetMapping("/getAll")
    public List<Equipment> getAllEquipment() {
        return equipmentService.getAllEquipment();
    }

    @GetMapping("/{id}")
    public Equipment getEquipmentById(@PathVariable Long id) {
        return equipmentService.getEquipmentById(id);
    }

    @PostMapping("/add")
    public Equipment addEquipment(@RequestBody Equipment equipment, @RequestHeader("Authorization") String token) {
        return equipmentService.saveEquipment(equipment, token);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteEquipment(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        return equipmentService.deleteEquipment(id, token);
    }

    @PutMapping("/update/{id}")
    public Equipment updateEquipment(@PathVariable Long id, @RequestBody Equipment equipment, @RequestHeader("Authorization") String token) {
        return equipmentService.updateEquipment(equipment, token, id);
    }
}

