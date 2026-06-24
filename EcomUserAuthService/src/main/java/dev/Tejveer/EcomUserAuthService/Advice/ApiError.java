package dev.Tejveer.EcomUserAuthService.Advice;

import lombok.Data;
import org.springframework.http.HttpStatusCode;

import java.time.LocalDateTime;

@Data
public class ApiError {
    private LocalDateTime localDateTime;
    private String errorMessage;
    private HttpStatusCode statusCode;

    public ApiError(){
        this.localDateTime = LocalDateTime.now();
    }

    public ApiError(String errorMessage, HttpStatusCode statusCode){
        this();
        this.errorMessage = errorMessage;
        this.statusCode = statusCode;
    }

}
