package com.doktorthe2nd.nyax.net.exceptions;

public class SessionExpiredException extends PacketException {
    public SessionExpiredException(String message) {
        super(message);
    }
}
