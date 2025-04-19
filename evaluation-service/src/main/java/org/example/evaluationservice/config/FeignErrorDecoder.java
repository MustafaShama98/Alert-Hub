package org.example.evaluationservice.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class FeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() >= 400 && response.status() <= 499) {
            return new ResponseStatusException(HttpStatus.valueOf(response.status()), 
                "Client error occurred while calling loader service");
        }
        if (response.status() >= 500 && response.status() <= 599) {
            return new ResponseStatusException(HttpStatus.valueOf(response.status()), 
                "Server error occurred while calling loader service");
        }
        return defaultErrorDecoder.decode(methodKey, response);
    }
} 