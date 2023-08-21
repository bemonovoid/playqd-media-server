package io.playqd.mediaserver.exception;

public class PlayqdException extends RuntimeException {

    public PlayqdException(String message) {
        super(message);
    }

    public PlayqdException(Throwable cause) {
        super(cause);
    }

    public PlayqdException(String message, Throwable cause) {
        super(message, cause);
    }
}
