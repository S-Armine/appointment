package com.qless.appointment.controller;

import com.qless.appointment.service.AppointmentService;
import com.qless.appointment.util.FileUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/import")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<Void> importAppointments(
            @RequestPart("file") MultipartFile file) throws IOException {
        if (!(file != null
                && Objects.requireNonNull(file.getContentType()).equalsIgnoreCase("text/csv")
                && FileUtil.checkHeader(file))) {
            return ResponseEntity.badRequest().build();
        }
        FileUtil.saveFile(file);
        appointmentService.processAppointmentFile(file);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Void> handleIOException(IOException e) {
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/status/{filename}")
    public ResponseEntity<Map<String, List<Long>>> getFileStatus(@PathVariable String filename) {
        var statuses = appointmentService.getStatuses(filename);
        return ResponseEntity.ok().body(statuses);
    }
}
