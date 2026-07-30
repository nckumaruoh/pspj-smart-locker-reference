package edu.klh.pspj.smartlocker.exception;

public class InvalidPickupCodeException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidPickupCodeException(String message) {
        super(message);
    }
}
