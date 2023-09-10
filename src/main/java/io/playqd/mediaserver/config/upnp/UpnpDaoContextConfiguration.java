package io.playqd.mediaserver.config.upnp;

import io.playqd.mediaserver.persistence.BrowsableObjectDao;
import io.playqd.mediaserver.persistence.StateVariableDao;
import io.playqd.mediaserver.persistence.jpa.dao.JpaBrowsableObjectDao;
import io.playqd.mediaserver.persistence.jpa.dao.JpaStateVariableDao;
import io.playqd.mediaserver.persistence.jpa.repository.BrowsableObjectRepository;
import io.playqd.mediaserver.persistence.jpa.repository.StateVariableRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "playqd.upnp", name = "enabled", havingValue = "true", matchIfMissing = true)
public class UpnpDaoContextConfiguration {

  @Bean
  StateVariableDao stateVariableDao(StateVariableRepository repository) {
    return new JpaStateVariableDao(repository);
  }

  @Bean
  BrowsableObjectDao browsableObjectDao(BrowsableObjectRepository browsableObjectRepository) {
    return new JpaBrowsableObjectDao(browsableObjectRepository);
  }

}
