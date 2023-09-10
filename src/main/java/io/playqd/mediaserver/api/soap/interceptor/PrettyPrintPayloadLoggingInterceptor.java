package io.playqd.mediaserver.api.soap.interceptor;

import io.playqd.mediaserver.config.properties.PlayqdLoggingProperties;
import org.springframework.ws.soap.server.endpoint.interceptor.SoapEnvelopeLoggingInterceptor;
import org.springframework.xml.transform.TransformerHelper;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

public class PrettyPrintPayloadLoggingInterceptor extends SoapEnvelopeLoggingInterceptor {

    private final Transformer transformer;

    public PrettyPrintPayloadLoggingInterceptor(PlayqdLoggingProperties logging) {
        this.transformer = createIndentingTransformer();
        setLogResponse(logging.isLogSoapResponse());
    }

    protected void logMessageSource(String logMessage, Source source) throws TransformerException {
        if (source != null) {
            var writer = new StringWriter();
            transformer.transform(source, new StreamResult(writer));
            var message = logMessage + writer.toString();
            logMessage(message);
        }
    }

    private static Transformer createIndentingTransformer() {
        try {
            var transformerFactory = new TransformerHelper().getTransformerFactory();
            transformerFactory.setAttribute("indent-number", 1);
            var transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            return transformer;
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

}
