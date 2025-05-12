package io.github.enessaidtatli.controller.restresponse;

import org.springframework.stereotype.Repository;

import java.util.List;

public final class ResponseBuilder {

    private ResponseBuilder(){
        throw new UnsupportedOperationException("");
    }

    public static <T> Response<DataResponse<T>> build(List<T> items, String message){
        return new Response<>(new DataResponse<>(items), message);
    }

    public static <T> Response<DataResponse<T>> build(List<T> items, int page, int size, long totalSize, String sortDirection, String message){
        return new Response<>(new DataResponse<>(items, page, size, totalSize, sortDirection), message);
    }

    public static <T> Response<T> build(T item, String message){
        return new Response<>(item, message);
    }

    public static Response build(ErrorResponse errorResponse){
        return new Response(new ErrorResponse(errorResponse.getCode(), errorResponse.getMessage()));
    }

}
