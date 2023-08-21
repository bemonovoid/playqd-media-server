package io.playqd.mediaserver.api.soap.data;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(namespace = "urn:schemas-upnp-org:control-1-0", name = UpnpError.ELEMENT_NAME)
@XmlType(name = UpnpError.ELEMENT_NAME, namespace = "")
public class UpnpError {

    public static final String ELEMENT_NAME = "UPnPError";

    @XmlElement(name = "errorCode", required = true)
    private int errorCode;

    @XmlElement(name = "errorDescription", required = true)
    private String errorDescription;

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }
}
