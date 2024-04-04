package com.jvnyor.demorestclient.services.exceptions;

public class CatUnknownErrorException extends RuntimeException {
    public CatUnknownErrorException() {
        super("An unknown error occurred while processing the cat request.");
    }
}