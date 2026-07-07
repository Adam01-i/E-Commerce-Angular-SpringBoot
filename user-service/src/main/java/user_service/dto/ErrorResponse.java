package user_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Structure JSON uniforme retournée pour toute erreur de l'API, qu'elle
 * provienne du GlobalExceptionHandler, du CustomAuthenticationEntryPoint ou
 * du CustomAccessDeniedHandler. Garantit que le frontend Angular peut
 * traiter les erreurs de façon homogène quel que soit le microservice.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> validationErrors;
}
