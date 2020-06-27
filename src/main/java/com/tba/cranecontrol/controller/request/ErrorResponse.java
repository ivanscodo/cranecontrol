package com.tba.cranecontrol.controller.request;

import java.util.Arrays;
import java.util.List;

public final class ErrorResponse {

    private final List<String> errors;

    private ErrorResponse(final List<String> errors) {
        this.errors = errors;
    }

    public static ErrorResponse with(final List<String> errors) {
        return new ErrorResponse(errors);
    }

    public static ErrorResponse with(final String ... errors) {
        return new ErrorResponse(Arrays.asList(errors));
    }

    public List<String> getErrors() {
        return errors;
    }
}