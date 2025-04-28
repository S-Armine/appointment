package com.qless.appointment.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentDTO {

    @CsvBindByName(column = "external_id")
    private long externalId;

    @CsvBindByName(column = "appointment_time")
    @CsvDate(value = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime appointmentTime;

}
