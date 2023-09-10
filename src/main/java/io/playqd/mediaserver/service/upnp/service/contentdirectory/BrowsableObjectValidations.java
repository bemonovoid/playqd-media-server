package io.playqd.mediaserver.service.upnp.service.contentdirectory;

import lombok.extern.slf4j.Slf4j;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.function.Supplier;

@Slf4j
public class BrowsableObjectValidations {

    private static final String XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><test>%s</test>";

    private static final BrowsableObjectValidation INVALID_XML_CHAR_VALIDATION = new InvalidXmlCharacterValidation();

    private static final Map<String, Boolean> SUCCESSFULLY_VALIDATED_OBJECT_IDS = new HashMap<>();

    public static boolean wasAlreadyValidated(String objectId) {
        return SUCCESSFULLY_VALIDATED_OBJECT_IDS.getOrDefault(objectId, false);
    }

    public static void markValidated(String objectId) {
        SUCCESSFULLY_VALIDATED_OBJECT_IDS.put(objectId, true);
    }

    public static boolean isXmlSourceValid(Source source) {
        return INVALID_XML_CHAR_VALIDATION.isValid(source);
    }

    public static String getFirstValidValue(Queue<Supplier<String>> values) {
        var supplier = values.poll();
        if (supplier == null) {
            return "Unknown";
        }
        var value = supplier.get();
        if (value == null) {
            return "Unknown"; //TODO log?
        }
        if (isXmlValueValid(value)) {
            return value;
        }
        return getFirstValidValue(values);
    }

    private static boolean isXmlValueValid(String value) {
        var reader = new StringReader(String.format(XML, value));
        return isXmlSourceValid(new StreamSource(reader));
    }

}
