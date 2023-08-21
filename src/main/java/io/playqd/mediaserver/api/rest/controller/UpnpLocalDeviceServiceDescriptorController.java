package io.playqd.mediaserver.api.rest.controller;

import io.playqd.mediaserver.service.upnp.server.UpnpServiceContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jupnp.UpnpService;
import org.jupnp.binding.xml.DescriptorBindingException;
import org.jupnp.model.types.ServiceId;
import org.jupnp.model.types.UDN;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/dev")
class UpnpLocalDeviceServiceDescriptorController {

    private final UpnpService upnpService;

    UpnpLocalDeviceServiceDescriptorController(UpnpServiceContextHolder upnpServiceContextHolder) {
        this.upnpService = upnpServiceContextHolder.getServiceInstance();
    }

    @GetMapping(path = "/{uuid}/svc/{serviceNamespace}/{serviceName}/desc", produces = MediaType.APPLICATION_XML_VALUE)
    String describe(@PathVariable String uuid,
                    @PathVariable String serviceNamespace,
                    @PathVariable String serviceName,
                    HttpServletRequest request) {

        log.info("Received description request for device: '{}' and service: '{}' from: {}:{}",
                uuid, serviceName, request.getRemoteAddr(), request.getRemotePort());

        var localDevice = upnpService.getRegistry().getLocalDevice(UDN.valueOf(uuid), true);
        var serviceLookupId = String.format("urn:%s:serviceId:%s", serviceNamespace, serviceName);
        var service = localDevice.findService(ServiceId.valueOf(serviceLookupId));

        try {
            return upnpService.getConfiguration().getServiceDescriptorBinderUDA10().generate(service);
        } catch (DescriptorBindingException e) {
            throw new RuntimeException(e);
        }
    }

}
