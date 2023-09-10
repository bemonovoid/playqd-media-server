package io.playqd.mediaserver.config.upnp;

import io.playqd.mediaserver.config.properties.PlayqdProperties;
import io.playqd.mediaserver.service.upnp.server.UpnpServiceContextHolder;
import io.playqd.mediaserver.service.upnp.service.StateVariableContextHolder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "playqd.upnp", name = "enabled", havingValue = "true", matchIfMissing = true)
public class UpnpServerContextConfiguration {

  @Bean
  UpnpServiceContextHolder upnpServiceContextHolder(PlayqdProperties playqdProperties,
                                                    StateVariableContextHolder stateVariableContextHolder) {
    return new UpnpServiceContextHolder(playqdProperties, stateVariableContextHolder);
  }
}
