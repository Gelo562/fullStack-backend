package com.example.projback.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Double price;

    @Column(length = 500) // Opcjonalnie ustaw limit długości
    private String description;

    @Column(nullable = false)
    private String imageName; // Nazwa pliku obrazka w katalogu
}

