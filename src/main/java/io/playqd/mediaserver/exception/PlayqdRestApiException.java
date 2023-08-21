package io.playqd.mediaserver.exception;

public sealed class PlayqdRestApiException extends PlayqdException {

    private final int errorCode;

    public PlayqdRestApiException(String message) {
        this(500, message);
    }

    public PlayqdRestApiException(int errorCode, String message) {
        this(errorCode, message, null);
    }

    public PlayqdRestApiException(String message, Throwable cause) {
        this(500, message, cause);
    }

    private PlayqdRestApiException(int errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public static final class BadRequest extends PlayqdRestApiException {

        public BadRequest(String message) {
            super(message, null);
        }

        public BadRequest(String message, Throwable cause) {
            super(400, message, cause);
        }
    }

    public static final class NotFound extends PlayqdRestApiException {

        public NotFound(String message) {
            this(message, null);
        }

        public NotFound(String message, Throwable cause) {
            super(400, message, cause);
        }
    }

    public static final class InternalServerError extends PlayqdRestApiException {

        public InternalServerError(String message) {
            this(message, null);
        }

        public InternalServerError(String message, Throwable cause) {
            super(500, message, cause);
        }
    }
}
