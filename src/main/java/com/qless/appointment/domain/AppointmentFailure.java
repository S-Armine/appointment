package com.qless.appointment.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "appointment_failed")
public class AppointmentFailure extends Appointment {
}
