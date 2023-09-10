package io.playqd.mediaserver.service.upnp.service.contentdirectory;

import io.playqd.mediaserver.exception.PlayqdException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.xml.transform.TransformerHelper;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

@Slf4j
class InvalidXmlCharacterValidation implements BrowsableObjectValidation {

    private final Transformer transformer = createTransformer();

    @Override
    public boolean isValid(Source source) {
        log.info("Validating source ...");
        try {
            var writer = new StringWriter();
            transformer.transform(source, new StreamResult(writer));
            log.info("Source is valid.");
            return true;
        } catch (TransformerException e) {
            if (e.getMessage().contains("An invalid XML character")) {
                log.warn("Detected invalid XML character(s). {}", e.getMessage());
                return false;
            }
            throw new PlayqdException("Unexpected source validation exception.", e);
        }
    }

    private static Transformer createTransformer() {
        try {
            var transformerFactory = new TransformerHelper().getTransformerFactory();
            return transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new IllegalStateException("Cannot create validation transformer.", e);
        }
    }

}
