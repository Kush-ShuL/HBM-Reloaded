package com.hbm.api.computer;

/** An intentional machine API error reported to a calling Lua program. */
public final class ComputerException extends RuntimeException {
    public ComputerException(String message) {
        super(message);
    }
}
