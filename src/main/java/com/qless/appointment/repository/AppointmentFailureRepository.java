package com.qless.appointment.repository;

import com.qless.appointment.domain.AppointmentFailure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentFailureRepository extends JpaRepository<AppointmentFailure, Long> {

    List<AppointmentFailure> findByFilename(String filename);
}
