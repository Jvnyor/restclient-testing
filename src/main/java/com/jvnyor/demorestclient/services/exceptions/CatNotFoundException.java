package com.jvnyor.demorestclient.services.exceptions;

public class CatNotFoundException extends RuntimeException {
    public CatNotFoundException() {
        super("A cat was not found.");
    }
}