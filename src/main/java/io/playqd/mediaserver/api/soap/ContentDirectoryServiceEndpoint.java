package io.playqd.mediaserver.api.soap;

import io.playqd.mediaserver.api.soap.data.Browse;
import io.playqd.mediaserver.service.upnp.server.service.StateVariableName;
import io.playqd.mediaserver.persistence.jpa.dao.BrowseResult;
import io.playqd.mediaserver.service.upnp.server.service.UpnpActionHandler;
import io.playqd.mediaserver.service.upnp.server.service.UpnpActionHandlerException;
import io.playqd.mediaserver.service.upnp.server.service.contentdirectory.BrowsableObjectValidations;
import io.playqd.mediaserver.service.upnp.server.service.contentdirectory.BrowseContext;
import io.playqd.mediaserver.service.upnp.server.service.StateVariableContextHolder;
import io.playqd.mediaserver.templates.TemplateNames;
import io.playqd.mediaserver.templates.TemplateService;
import org.jupnp.model.types.ErrorCode;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.server.endpoint.annotation.SoapAction;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.util.Map;

@Endpoint
class ContentDirectoryServiceEndpoint {

    private final TemplateService templateService;
    private final StateVariableContextHolder stateVariableContextHolder;
    private final UpnpActionHandler<BrowseContext, BrowseResult> browseActionHandler;

    ContentDirectoryServiceEndpoint(TemplateService templateService,
                                    StateVariableContextHolder stateVariableContextHolder,
                                    UpnpActionHandler<BrowseContext, BrowseResult> browseActionHandler) {
        this.templateService = templateService;
        this.browseActionHandler = browseActionHandler;
        this.stateVariableContextHolder = stateVariableContextHolder;
    }

    @SoapAction("urn:schemas-upnp-org:service:ContentDirectory:1#GetSystemUpdateID")
    @ResponsePayload
    Source getSystemUpdateId(MessageContext request) {
        var responseXml = templateService.processToString(
                TemplateNames.SYSTEM_UPDATE_ID_RESPONSE,
                stateVariableContextHolder.getOrThrow(StateVariableName.SYSTEM_UPDATE_ID));
        return new StreamSource(new StringReader(responseXml));
    }

    @SoapAction("urn:schemas-upnp-org:service:ContentDirectory:1#GetSortCapabilities")
    @ResponsePayload
    Source getSortCapabilities() {
        var sortCaps = "<u:GetSortCapabilitiesResponse xmlns:u=\"urn:schemas-upnp-org:service:ContentDirectory:1\"><SortCaps>" +
                "upnp:class,dc:title,upnp:artist" +
                "</SortCaps></u:GetSortCapabilitiesResponse>";
        return new StreamSource(new StringReader(sortCaps));
    }

    @SoapAction("urn:schemas-upnp-org:service:ContentDirectory:1#GetSearchCapabilities")
    @ResponsePayload
    Source getSearchCapabilities() {
        var searchCaps = "<u:GetSearchCapabilitiesResponse xmlns:u=\"urn:schemas-upnp-org:service:ContentDirectory:1\">" +
                "<SearchCaps>" +
                "upnp:class,dc:title,dc:creator,upnp:artist,upnp:album,upnp:genre" +
                "</SearchCaps></u:GetSearchCapabilitiesResponse>";
        return new StreamSource(new StringReader(searchCaps));
    }

    @SoapAction("urn:schemas-upnp-org:service:ContentDirectory:1#Browse")
    @ResponsePayload
    Source browse(@RequestPayload Browse requestPayload) {
        return browse(new BrowseContext(requestPayload));
    }

    @SoapAction("urn:schemas-upnp-org:service:ContentDirectory:1#Search")
    @ResponsePayload
    Source search(@RequestPayload Browse requestPayload) {
        throw new UpnpActionHandlerException(ErrorCode.ACTION_FAILED, "ContentDirectory:#Search is not implemented yet");
    }

    private Source browse(BrowseContext context) {
        return createResponse(context, browseActionHandler.handle(context));
    }

    private Source createResponse(BrowseContext context, BrowseResult response) {
        try {
            var parsedTemplate = templateService.processToString(
                    TemplateNames.BROWSE_RESPONSE, buildTemplateDataModel(response));

            var source = new StreamSource(new StringReader(parsedTemplate));

            if (BrowsableObjectValidations.wasAlreadyValidated(context.getObjectId())) {
                return source;
            }

            if (BrowsableObjectValidations.isXmlSourceValid(source)) {
                BrowsableObjectValidations.markValidated(context.getObjectId());
                return new StreamSource(new StringReader(parsedTemplate));
            } else {
                context.addHeader(BrowseContext.HEADER_INVALID_XML_CHAR_VALIDATION_ENABLED, true);
                return browse(context);
            }
        } catch (Exception e) {
            throw new UpnpActionHandlerException(ErrorCode.ACTION_FAILED, e);
        }
    }

    private Map<String, Object> buildTemplateDataModel(BrowseResult response) {
        return Map.of("browseResponse", Map.of(
                "objects", response.objects(),
                "numberReturned", response.numberReturned(),
                "totalMatches", response.totalMatches(),
                "updateId", stateVariableContextHolder.getOrThrow(StateVariableName.SYSTEM_UPDATE_ID)));
    }
}
