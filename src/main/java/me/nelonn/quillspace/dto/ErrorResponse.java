package me.nelonn.quillspace.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse extends Response {
    private String error;

    public ErrorResponse() {
        super(false);
    }

    public ErrorResponse(String error) {
        this();
        this.error = error;
    }
}
