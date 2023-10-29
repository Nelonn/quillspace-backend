package me.nelonn.quillspace.util;

import me.nelonn.quillspace.dto.Response;
import me.nelonn.quillspace.dto.ResultResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResultEntity<T> extends ResponseEntity<Response> {
    public ResultEntity(T result) {
        super(new ResultResponse<>(result), HttpStatus.OK);
    }
}
