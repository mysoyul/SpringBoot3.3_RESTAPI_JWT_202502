package com.boot3.myrestapi.common.exception.advice;

import lombok.Data;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Data
public class ErrorObject {
    private Integer statusCode;
    private String message;
    private String timestamp;

    public String getTimestamp() {
        LocalDateTime ldt = LocalDateTime.now();
        return DateTimeFormatter.ofPattern(
                "yyyy-MM-dd HH:mm:ss E a", 
                Locale.KOREA).format(ldt);
    }
}