package edu.klh.pspj.smartlocker.exception;

public class LockerUnavailableException extends Exception {
    private static final long serialVersionUID = 1L;

    public LockerUnavailableException(String message) {
        super(message);
    }
}
