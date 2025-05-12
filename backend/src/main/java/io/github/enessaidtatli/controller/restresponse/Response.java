package io.github.enessaidtatli.controller.restresponse;

import lombok.Getter;

@Getter
public class Response<T> {

    private T data;
    private ErrorResponse errorResponse;
    private String message = "--";

    public Response(T data, String message) {
        this.data = data;
        this.message = message;
    }

    public Response(ErrorResponse errorResponse) {
        this.errorResponse = errorResponse;
    }
}
