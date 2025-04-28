package com.qless.appointment.repository;

import com.qless.appointment.domain.AppointmentSuccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentSuccessRepository extends JpaRepository<AppointmentSuccess, Long> {

    Optional<AppointmentSuccess> findByExternalId(Long externalId);

    Optional<AppointmentSuccess> findByDateBetween(LocalDateTime dateAfter, LocalDateTime dateBefore);

    List<AppointmentSuccess> findByFilename(String filename);
}
