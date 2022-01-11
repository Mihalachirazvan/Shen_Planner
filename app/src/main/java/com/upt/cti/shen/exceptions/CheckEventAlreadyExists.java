package com.upt.cti.shen.exceptions;

public class CheckEventAlreadyExists extends Exception{
    public CheckEventAlreadyExists() {
        super("An event already exits at this hour");
    }
}