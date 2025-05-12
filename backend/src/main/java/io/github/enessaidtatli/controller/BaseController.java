package io.github.enessaidtatli.controller;

import io.github.enessaidtatli.config.usecase.BeanAwareHandlerPublisher;
import io.github.enessaidtatli.controller.restresponse.DataResponse;
import io.github.enessaidtatli.controller.restresponse.ErrorResponse;
import io.github.enessaidtatli.controller.restresponse.Response;
import io.github.enessaidtatli.controller.restresponse.ResponseBuilder;

import java.util.List;


public abstract class BaseController extends BeanAwareHandlerPublisher {

    public <T> Response<DataResponse<T>> respond(List<T> items, int page, int size, int totalSize, String sortDirection, String message){
        return ResponseBuilder.build(items, page, size, totalSize, sortDirection, message);
    }

    public <T> Response<DataResponse<T>> respond(List<T> items, String message){
        return ResponseBuilder.build(items, message);
    }

    public <T> Response<T> respond(T item, String message){
        return ResponseBuilder.build(item, message);
    }

    public Response respond(ErrorResponse errorResponse){
        return ResponseBuilder.build(errorResponse);
    }
}
