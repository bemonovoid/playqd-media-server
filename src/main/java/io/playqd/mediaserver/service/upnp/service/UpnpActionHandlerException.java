package io.playqd.mediaserver.service.upnp.service;

import io.playqd.mediaserver.exception.PlayqdException;
import lombok.Getter;
import org.jupnp.model.types.ErrorCode;

@Getter
public class UpnpActionHandlerException extends PlayqdException {

    private final int errorCode;
    private final String errorDescription;

    public UpnpActionHandlerException(ErrorCode errorCode) {
        this(errorCode, (Throwable) null);
    }

    public UpnpActionHandlerException(ErrorCode errorCode, Throwable cause) {
       this(errorCode.getCode(), errorCode.getDescription(), cause);
    }

    public UpnpActionHandlerException(ErrorCode errorCode, String overrideErrorDescription) {
        this(errorCode.getCode(), overrideErrorDescription, null);
    }

    public UpnpActionHandlerException(ErrorCode errorCode, String errorDescription, Throwable cause) {
        this(errorCode.getCode(), errorDescription, cause);
    }

    public UpnpActionHandlerException(int errorCode, String errorDescription, Throwable cause) {
        super(errorDescription, cause);
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
    }

}
