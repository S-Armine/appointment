package com.qless.appointment.dto;

import lombok.Data;

import java.util.List;

@Data
public class StatusDTO {

    private List<Long> success;

    private List<Long> failed;

}
