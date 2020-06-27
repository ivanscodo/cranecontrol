package com.tba.cranecontrol.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.tba.cranecontrol.controller.request.ErrorResponse;
import com.tba.cranecontrol.exception.LaneCreationException;
import com.tba.cranecontrol.exception.MovementNotAllowedException;
import com.tba.cranecontrol.exception.NotFoundException;

@ControllerAdvice
public class ControllerErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleGeneralException(final Exception ex, final WebRequest webRequest) {
        return new ResponseEntity<>(ex.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(LaneCreationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleLaneCreationException(final LaneCreationException ex, final WebRequest webRequest) {
        return new ResponseEntity<>(ex.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MovementNotAllowedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleMovementNotAllowedException(final MovementNotAllowedException ex, final WebRequest webRequest) {
        return new ResponseEntity<>(ex.getMessage(), new HttpHeaders(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleNotFoundException(final NotFoundException ex, final WebRequest webRequest) {
        return new ResponseEntity<>(ex.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            final MethodArgumentNotValidException ex,
            final HttpHeaders headers,
            final HttpStatus status,
            final WebRequest request
    ) {
        return handleBadRequestException(headers, ex.getBindingResult());
    }

    private static ResponseEntity<Object> handleBadRequestException(
            final HttpHeaders headers,
            final BindingResult bindingResult
    ) {
        final List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        final List<ObjectError> globalErrors = bindingResult.getGlobalErrors();
        final List<String> errors = new ArrayList<>(fieldErrors.size() + globalErrors.size());
        errors.addAll(bindingResult
                .getFieldErrors()
                .stream()
                .map(error -> (error.getField() + ": " + error.getDefaultMessage()))
                .collect(Collectors.toList()));
        errors.addAll(bindingResult
                .getGlobalErrors()
                .stream()
                .map(error -> (error.getObjectName() + ": " + error.getDefaultMessage()))
                .collect(Collectors.toList()));
        final ErrorResponse errorResponse = ErrorResponse.with(errors);
        return new ResponseEntity<>(errorResponse, headers, HttpStatus.BAD_REQUEST);
    }
}
