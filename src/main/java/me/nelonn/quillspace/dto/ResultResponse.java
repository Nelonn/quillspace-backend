package me.nelonn.quillspace.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultResponse<T> extends Response {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    //@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private T result;

    public ResultResponse() {
        super(true);
    }

    public ResultResponse(T result) {
        super(true);
        this.result = result;
    }
}
