package com.example.projback.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType type;

    @ElementCollection
    @CollectionTable(name = "room_base_equipment_ids", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "equipment_id")
    private List<Long> baseEquipment;

    @ElementCollection
    @CollectionTable(name = "room_optional_equipment_ids", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "equipment_id")
    private List<Long> optionalEquipment;

    @Column
    private Double pricePerHour;

    @Column
    private Double pricePerDay;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Reservation> reservations;

}

