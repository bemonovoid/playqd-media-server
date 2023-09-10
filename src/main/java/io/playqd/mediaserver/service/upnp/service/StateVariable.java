package io.playqd.mediaserver.service.upnp.service;

import java.time.LocalDateTime;

public record StateVariable(StateVariableName name, String value, LocalDateTime lastModifiedDate) {
}
