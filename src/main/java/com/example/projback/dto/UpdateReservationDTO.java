package com.example.projback.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateReservationDTO {
    private Date startTime;
    private Date endTime;
    private List<Long> equipment;
}
