package com.example.projback.dto;

import com.example.projback.entity.Equipment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MakeReservationDTO {
    private Long room;
    private Date startTime;
    private Date endTime;
    private List<Long> additionalEquipment;
    private Double estimatedPrice;

}
