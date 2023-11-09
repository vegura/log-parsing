package com.ursolutions.dataflow.beam.pipeline.step.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data @ToString
public class LogGrabbedData implements Serializable {
    private String clientIp;
    private ZonedDateTime dateTime;
    private String method;
    private Integer responseStatus;
    private String request;
}
