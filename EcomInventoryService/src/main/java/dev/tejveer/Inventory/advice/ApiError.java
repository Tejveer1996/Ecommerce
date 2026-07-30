package dev.tejveer.Inventory.advice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
