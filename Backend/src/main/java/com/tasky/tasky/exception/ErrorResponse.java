package com.tasky.tasky.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ErrorResponse {

    private Date timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
