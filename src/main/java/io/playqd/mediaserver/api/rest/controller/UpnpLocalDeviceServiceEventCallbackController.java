package io.playqd.mediaserver.api.rest.controller;

import io.playqd.mediaserver.config.upnp.ConditionalOnUpnpEnabled;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.xml.transform.TransformerHelper;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/dev")
@ConditionalOnUpnpEnabled
class UpnpLocalDeviceServiceEventCallbackController {

    private final Transformer transformer;

    UpnpLocalDeviceServiceEventCallbackController() {
        var transformerFactory = new TransformerHelper().getTransformerFactory();
        transformerFactory.setAttribute("indent-number", 1);
        this.transformer = createIndentingTransformer(transformerFactory);
    }

    @RequestMapping(path = "/{uuid}/svc/upnp-org/{serviceName}/event")
    @ResponseStatus(HttpStatus.OK)
    void handleSubscribeToSelf(@PathVariable String uuid,
                                   @PathVariable String serviceName,
                                   @RequestHeader HttpHeaders httpHeaders,
                                   @RequestBody Map<String, Object> requestBody,
                                   HttpServletRequest httpRequest) {
        log.info(">>>>> " + requestBody.toString());
    }

    @RequestMapping(path = "/{uuid}/svc/upnp-org/{serviceName}/event/cb")
    @ResponseStatus(HttpStatus.OK)
    void handleUpnpServiceCallback(@PathVariable String uuid,
                                   @PathVariable String serviceName,
                                   @RequestHeader HttpHeaders httpHeaders,
                                   @RequestBody Map<String, Object> requestBody,
                                   HttpServletRequest httpRequest) {
        log.info(">>>>> " + requestBody.toString());
//        var propertyValue = (Map<String, String>) requestBody.get("property");
//        if (propertyValue.containsKey("LastChange")) {
//            var source = new StreamSource(new StringReader(propertyValue.get("LastChange")));
//            try {
//                logMessageSource(source);
//            } catch (TransformerException e) {
//                log.error("Transform failed", e);
//            }
//        }
    }

    @RequestMapping(path = "/{uuid}/svc/tencent-com/{serviceName}/event/cb")
    @ResponseStatus(HttpStatus.OK)
    void handleTencentServiceCallback(@PathVariable String uuid,
                                      @PathVariable String serviceName,
                                      @RequestHeader HttpHeaders httpHeaders,
                                      @RequestBody Map<String, Object> requestBody,
                                      HttpServletRequest httpRequest) {
        log.info(">>>>> " + requestBody.toString());
//        var propertyValue = (Map<String, String>) requestBody.get("property");
//        if (propertyValue.containsKey("LastChange")) {
//            var source = new StreamSource(new StringReader(propertyValue.get("LastChange")));
//            try {
//                logMessageSource(source);
//            } catch (TransformerException e) {
//                log.error("Transform failed", e);
//            }
//        }
    }

    @RequestMapping(path = "/{uuid}/svc/wiimu-com/{serviceName}/event/cb")
    @ResponseStatus(HttpStatus.OK)
    void handleWiimuServiceCallback(@PathVariable String uuid,
                                      @PathVariable String serviceName,
                                      @RequestHeader HttpHeaders httpHeaders,
                                      @RequestBody Map<String, Object> requestBody,
                                      HttpServletRequest httpRequest) {
        log.info(">>>>> " + requestBody.toString());
//        var propertyValue = (Map<String, String>) requestBody.get("property");
//        if (propertyValue.containsKey("LastChange")) {
//            var source = new StreamSource(new StringReader(propertyValue.get("LastChange")));
//            try {
//                logMessageSource(source);
//            } catch (TransformerException e) {
//                log.error("Transform failed", e);
//            }
//        }
    }

    private void logMessageSource(Source source) throws TransformerException {
        if (source != null) {
            var writer = new StringWriter();
            transformer.transform(source, new StreamResult(writer));
            log.info(writer.toString());
        }
    }

    private Transformer createIndentingTransformer(TransformerFactory transformerFactory) {
        try {
            var transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            return transformer;
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}

