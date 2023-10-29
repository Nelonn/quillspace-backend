package me.nelonn.quillspace.util;

import me.nelonn.quillspace.dto.ErrorResponse;
import me.nelonn.quillspace.dto.Response;
import me.nelonn.quillspace.dto.RestError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ErrorEntity extends ResponseEntity<Response> {
    public ErrorEntity(RestError err) {
        super(new ErrorResponse(err.name()), HttpStatus.BAD_REQUEST);
    }
}
