package io.playqd.mediaserver.api.rest.controller;

import io.playqd.mediaserver.service.upnp.server.PlayqdDeviceNamespace;
import io.playqd.mediaserver.service.upnp.server.UpnpServiceContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jupnp.UpnpService;
import org.jupnp.model.Namespace;
import org.jupnp.model.message.StreamRequestMessage;
import org.jupnp.model.message.UpnpRequest;
import org.jupnp.model.meta.LocalDevice;
import org.jupnp.model.profile.RemoteClientInfo;
import org.jupnp.model.types.UDN;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/dev")
class UpnpLocalDeviceDescriptorController {

    private static final Namespace ROOT_DEVICE_NAMESPACE = new PlayqdDeviceNamespace();

    private final UpnpService upnpService;

    UpnpLocalDeviceDescriptorController(UpnpServiceContextHolder upnpServiceContextHolder) {
        this.upnpService = upnpServiceContextHolder.getServiceInstance();
    }

    @GetMapping(path = "/local/desc", produces = MediaType.APPLICATION_XML_VALUE)
    List<String> describeLocalDevices(HttpServletRequest request) {

        return upnpService.getRegistry().getLocalDevices().stream()
                .map(localDevice -> describeLocalDevice(localDevice, request.getRequestURI()))
                .toList();
    }

    @GetMapping(path = "/{uuid}/desc", produces = MediaType.APPLICATION_XML_VALUE)
    String describe(@PathVariable String uuid, HttpServletRequest request) {

        log.debug("Received description request for device: '{}' from: {}:{}",
                uuid, request.getRemoteAddr(), request.getRemotePort());

        var localDevice = upnpService.getRegistry().getLocalDevice(UDN.valueOf(uuid), true);
        return describeLocalDevice(localDevice, request.getRequestURI());
    }

    private String describeLocalDevice(LocalDevice localDevice, String requestUri) {
        try {
            var streamRequestMessage = new StreamRequestMessage(UpnpRequest.Method.GET, URI.create(requestUri));

            var remoteClientInfo = new RemoteClientInfo(streamRequestMessage);

            var descriptorBinder = upnpService.getConfiguration().getDeviceDescriptorBinderUDA10();

            return descriptorBinder.generate(localDevice, remoteClientInfo, ROOT_DEVICE_NAMESPACE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
