package io.playqd.mediaserver.api.soap;

import io.playqd.mediaserver.config.upnp.ConditionalOnUpnpEnabled;
import io.playqd.mediaserver.service.upnp.server.PlayqdUpnpService;
import io.playqd.mediaserver.service.upnp.server.UpnpServiceContextHolder;
import io.playqd.mediaserver.templates.TemplateService;
import io.playqd.mediaserver.templates.TemplateNames;
import org.jupnp.model.types.ServiceId;
import org.jupnp.support.connectionmanager.ConnectionManagerService;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.server.endpoint.annotation.SoapAction;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.util.Map;

@Endpoint
@ConditionalOnUpnpEnabled
class ConnectionManagerServiceEndpoint {

    private final PlayqdUpnpService upnpService;
    private final TemplateService templateService;

    ConnectionManagerServiceEndpoint(TemplateService templateService,
                                     UpnpServiceContextHolder upnpServiceContextHolder) {
        this.templateService = templateService;
        this.upnpService = upnpServiceContextHolder.getServiceInstance();
    }

    @SoapAction("urn:schemas-upnp-org:service:ConnectionManager:1#PrepareForConnection")
    @ResponsePayload
    Source prepareForConnection(MessageContext request) {
        var id = "<u:GetSystemUpdateIDResponse xmlns:u=\"urn:schemas-upnp-org:service:ContentDirectory:1\"><Id>1</Id></u:GetSystemUpdateIDResponse>";
        return new StreamSource(new StringReader(id));
    }

    @SoapAction("urn:schemas-upnp-org:service:ConnectionManager:1#GetCurrentConnectionIDs")
    @ResponsePayload
    Source getCurrentConnectionIDs(MessageContext request) {
        var id = "<u:GetSystemUpdateIDResponse xmlns:u=\"urn:schemas-upnp-org:service:ContentDirectory:1\"><Id>1</Id></u:GetSystemUpdateIDResponse>";
        return new StreamSource(new StringReader(id));
    }

    @SoapAction("urn:schemas-upnp-org:service:ConnectionManager:1#GetCurrentConnectionInfo")
    @ResponsePayload
    Source getCurrentConnectionInfo(MessageContext request) {
        var id = "<u:GetSystemUpdateIDResponse xmlns:u=\"urn:schemas-upnp-org:service:ContentDirectory:1\"><Id>1</Id></u:GetSystemUpdateIDResponse>";
        return new StreamSource(new StringReader(id));
    }

    @SoapAction("urn:schemas-upnp-org:service:ConnectionManager:1#GetProtocolInfo")
    @ResponsePayload
    Source getProtocolInfo(MessageContext request) {
        var localDevice = upnpService.getThisLocalDevice();
        var service = localDevice.findService(ServiceId.valueOf("urn:upnp-org:serviceId:ConnectionManager"));
        var connectionManagerService = (ConnectionManagerService) service.getManager().getImplementation();
        var protocolInfos = connectionManagerService.getSourceProtocolInfo().toString();

        var data = Map.<String, Object>of("protocolInfos", Map.of("sources", protocolInfos));

        return createResponse(data);
    }

    private Source createResponse(Map<String, Object> data) {
        var parsedTemplate =
                templateService.processToString(TemplateNames.GET_PROTOCOL_INFO_RESPONSE, data);
        return new StreamSource(new StringReader(parsedTemplate));
    }

}
