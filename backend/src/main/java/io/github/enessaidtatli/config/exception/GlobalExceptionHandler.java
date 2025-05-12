package io.github.enessaidtatli.config.exception;

import io.github.enessaidtatli.controller.BaseController;
import io.github.enessaidtatli.controller.restresponse.ErrorResponse;
import io.github.enessaidtatli.controller.restresponse.Response;
import io.github.enessaidtatli.exception.EmailDuplicationException;
import io.github.enessaidtatli.exception.SourceNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalExceptionHandler extends BaseController {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Response handle(Exception exception){
        return respond(new ErrorResponse(INTERNAL_SERVER_ERROR.getReasonPhrase(), exception.getLocalizedMessage()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public Response handle(ConstraintViolationException constraintViolationException){
        return respond(new ErrorResponse(BAD_REQUEST.getReasonPhrase(), extractViolations(constraintViolationException)));
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public Response handle(ValidationException validationException){
        return respond(new ErrorResponse(BAD_REQUEST.getReasonPhrase(), validationException.getLocalizedMessage()));
    }

    @ExceptionHandler(SourceNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    @ResponseBody
    public Response handle(SourceNotFoundException sourceNotFoundException){
        return respond(new ErrorResponse(NOT_FOUND.getReasonPhrase(), sourceNotFoundException.getLocalizedMessage()));
    }

    @ExceptionHandler(EmailDuplicationException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public Response handle(EmailDuplicationException emailDuplicationException){
        return respond(new ErrorResponse(BAD_REQUEST.getReasonPhrase(), emailDuplicationException.getLocalizedMessage()));
    }

    private String extractViolations(ConstraintViolationException constraintViolationException){
        return constraintViolationException.getConstraintViolations()
                .stream().map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(" ~~~ "));
    }
}
