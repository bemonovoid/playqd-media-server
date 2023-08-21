package io.playqd.mediaserver.config.properties;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PACKAGE)
public class UpnpServiceProperties {

    private UpnpActionProperties action = new UpnpActionProperties();

    private UpnpStreamServerProperties streamServer;

}
