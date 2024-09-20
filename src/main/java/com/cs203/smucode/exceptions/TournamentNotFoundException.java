package com.cs203.smucode.exceptions;

public class TournamentNotFoundException extends RuntimeException{
    public TournamentNotFoundException(String message) {
        super(message);
    }
}
