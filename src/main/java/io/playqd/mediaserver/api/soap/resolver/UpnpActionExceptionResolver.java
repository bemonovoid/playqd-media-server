package io.playqd.mediaserver.api.soap.resolver;

import io.playqd.mediaserver.api.soap.data.UpnpError;
import io.playqd.mediaserver.exception.PlayqdException;
import io.playqd.mediaserver.service.upnp.service.UpnpActionHandlerException;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import lombok.extern.slf4j.Slf4j;
import org.jupnp.model.types.ErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.server.endpoint.SoapFaultDefinition;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;

import java.util.Properties;

@Slf4j
@Component
class UpnpActionExceptionResolver extends SoapFaultMappingExceptionResolver {

    private final Marshaller upnpErrorMarshaller;

    UpnpActionExceptionResolver() throws jakarta.xml.bind.JAXBException {
        setDefaultFault();
        setExceptionMappings(buildProperties());
        setOrder(20);
        this.upnpErrorMarshaller = JAXBContext.newInstance(UpnpError.class).createMarshaller();
    }

    private void setDefaultFault() {
        var faultDefinition = new SoapFaultDefinition();
        faultDefinition.setFaultCode(SoapFaultDefinition.CLIENT);
        faultDefinition.setFaultStringOrReason(UpnpError.ELEMENT_NAME);
        setDefaultFault(faultDefinition);
    }

    private Properties buildProperties() {
        var properties = new Properties();
        properties.put(Exception.class.getName(), SoapFaultDefinition.CLIENT.toString());
        properties.put(PlayqdException.class.getName(), SoapFaultDefinition.CLIENT.toString());
        properties.put(UpnpActionHandlerException.class.getName(), SoapFaultDefinition.CLIENT.toString());
        return properties;
    }

    @Override
    protected void customizeFault(Object endpoint, Exception ex, SoapFault fault) {
        log.warn("SOAP Exception processed ", ex);

        var upnpError = new UpnpError();
        upnpError.setErrorCode(ErrorCode.ACTION_FAILED.getCode());
        upnpError.setErrorDescription(ErrorCode.ACTION_FAILED.getDescription());

        if (ex instanceof UpnpActionHandlerException upnpActionException) {
            upnpError.setErrorCode(upnpActionException.getErrorCode());
            upnpError.setErrorDescription(upnpActionException.getErrorDescription());
        }

        var faultDetail = fault.addFaultDetail();

        try {
            upnpErrorMarshaller.marshal(upnpError, faultDetail.getResult());
        } catch (JAXBException e) {
            log.error("failed to to marshal upnpError into result", e);
        }
    }
}
