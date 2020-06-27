package com.tba.cranecontrol.exception;

public class MovementNotAllowedException extends RuntimeException{

    public MovementNotAllowedException(final String message) {
        super(message);
    }

}
