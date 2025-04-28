package com.qless.appointment.service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.qless.appointment.domain.Appointment;
import com.qless.appointment.domain.AppointmentFailure;
import com.qless.appointment.domain.AppointmentSuccess;
import com.qless.appointment.dto.AppointmentDTO;
import com.qless.appointment.repository.AppointmentFailureRepository;
import com.qless.appointment.repository.AppointmentSuccessRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AppointmentService {

    private final AppointmentSuccessRepository appointmentSuccessRepository;

    private final AppointmentFailureRepository appointmentFailureRepository;

    public AppointmentService(AppointmentSuccessRepository appointmentSuccessRepository, AppointmentFailureRepository appointmentFailureRepository) {
        this.appointmentSuccessRepository = appointmentSuccessRepository;
        this.appointmentFailureRepository = appointmentFailureRepository;
    }

    @Async
    public void processAppointmentFile(MultipartFile file) throws IOException {
        var data = getDataPoints(file);
        data.forEach(appointmentDTO -> processData(appointmentDTO, file.getOriginalFilename()));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processData(AppointmentDTO appointmentDTO, String fileName) {
        LocalDateTime appointmentTime = appointmentDTO.getAppointmentTime();
        int durationMinutes = 15;
        if (appointmentSuccessRepository.findByDateBetween(appointmentTime.minusMinutes(durationMinutes), appointmentTime).isEmpty()) {
            var foundAppointmentOptional = appointmentSuccessRepository.findByExternalId(appointmentDTO.getExternalId());
            if (foundAppointmentOptional.isPresent()) {
                var appointment = foundAppointmentOptional.get();
                appointment.setDate(appointmentTime);
                appointment.setFilename(fileName);
            } else {
                AppointmentSuccess appointment = new AppointmentSuccess();
                setAppointmentFromDTO(appointment, appointmentDTO, fileName);
                appointmentSuccessRepository.save(appointment);
            }
        } else {
            AppointmentFailure appointment = new AppointmentFailure();
            setAppointmentFromDTO(appointment, appointmentDTO, fileName);
            appointmentFailureRepository.save(appointment);
        }
    }

    public void setAppointmentFromDTO(Appointment appointment, AppointmentDTO appointmentDTO, String fileName) {
        appointment.setExternalId(appointmentDTO.getExternalId());
        appointment.setDate(appointmentDTO.getAppointmentTime());
        appointment.setFilename(fileName);
    }

    public Set<AppointmentDTO> getDataPoints(MultipartFile file) throws IOException {
        try (Reader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(file.getBytes())))) {
            HeaderColumnNameMappingStrategy<AppointmentDTO> strategy =
                    new HeaderColumnNameMappingStrategy<>();
            strategy.setType(AppointmentDTO.class);
            CsvToBean<AppointmentDTO> csvToBean =
                    new CsvToBeanBuilder<AppointmentDTO>(reader)
                            .withMappingStrategy(strategy)
                            .withIgnoreLeadingWhiteSpace(true)
                            .build();
            return csvToBean.parse()
                    .stream()
                    .map(line -> AppointmentDTO.builder()
                            .externalId(line.getExternalId())
                            .appointmentTime(line.getAppointmentTime())
                            .build())
                    .collect(Collectors.toSet());
        }
    }

    public Map<String, List<Long>> getStatuses(String fileName) {
        var failed = appointmentFailureRepository.findByFilename(fileName).stream().map(AppointmentFailure::getExternalId).toList();
        var succeeded = appointmentSuccessRepository.findByFilename(fileName).stream().map(AppointmentSuccess::getExternalId).toList();

        Map<String, List<Long>> statuses = new HashMap<>();
        statuses.put("SUCCESS", succeeded);
        statuses.put("FAILED", failed);
        return statuses;
    }
}
