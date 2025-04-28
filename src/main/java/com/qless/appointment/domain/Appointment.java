package com.qless.appointment.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(nullable = false, unique = true)
    private LocalDateTime date;

    private String filename;

    @Column(nullable = false, unique = true, name = "external_id")
    private Long externalId;
}
