package com.upt.cti.shen.exceptions;

public class CheckEventTimeFormat extends Exception {

    public CheckEventTimeFormat() {
        super("Invalid format,requires HH:MM");
    }

}
