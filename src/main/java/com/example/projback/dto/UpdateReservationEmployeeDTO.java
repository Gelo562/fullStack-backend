package com.example.projback.dto;

import com.example.projback.entity.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateReservationEmployeeDTO {
    private ReservationStatus reservationStatus;
    private Double finalPrice;
}
