package br.com.devduo.viverbemapi.exceptions;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ExceptionResponse {
    private Date timestamp;
    private String message;
    private String details;
}
