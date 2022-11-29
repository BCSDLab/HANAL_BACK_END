package com.bcsdlab.biseo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class ErrorResponse {

    private String className;
    private String errorMessage;
    private Map<String, String> errorMessages;
    private HttpStatus httpStatus;
}
