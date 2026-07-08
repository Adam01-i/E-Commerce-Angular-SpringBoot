package com.ecommerce.catalog_service.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Format JSON uniforme retourné pour toute erreur de l'API, conforme au
 * format demandé : time, status, message, path (le champ "error" et
 * "validationErrors" sont des compléments utiles conservés en plus).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private LocalDateTime time;
    private int status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> validationErrors;
}
