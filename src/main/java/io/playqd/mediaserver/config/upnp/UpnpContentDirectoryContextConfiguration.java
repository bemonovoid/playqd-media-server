package io.playqd.mediaserver.config.upnp;

import io.playqd.mediaserver.config.lifecycle.ApplicationRunnerOrder;
import io.playqd.mediaserver.config.lifecycle.UpnpStateVariablesInitializer;
import io.playqd.mediaserver.persistence.StateVariableDao;
import io.playqd.mediaserver.persistence.jpa.dao.BrowseResult;
import io.playqd.mediaserver.service.upnp.service.StateVariableContextHolder;
import io.playqd.mediaserver.service.upnp.service.StateVariableContextHolderImpl;
import io.playqd.mediaserver.service.upnp.service.UpnpActionHandler;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.BrowseActionDelegate;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.BrowseContext;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.SimpleActionContext;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.impl.BrowseActionDelegateImpl;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.impl.BrowseActionHandler;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.impl.GetSystemIdActionHandler;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

@Configuration
@Import(UpnpDaoContextConfiguration.class)
@ConditionalOnProperty(prefix = "playqd.upnp", name = "enabled", havingValue = "true", matchIfMissing = true)
public class UpnpContentDirectoryContextConfiguration {

  @Bean
  StateVariableContextHolder stateVariableContextHolder(StateVariableDao stateVariableDao) {
    return new StateVariableContextHolderImpl(stateVariableDao);
  }

  @Bean
  @Order(ApplicationRunnerOrder.UPNP_STATE_VARIABLES_INITIALIZER)
  ApplicationRunner upnpStateVariablesInitializer(StateVariableContextHolder contextHolder) {
    return new UpnpStateVariablesInitializer(contextHolder);
  }

  @Bean
  UpnpActionHandler<SimpleActionContext, Integer> getSystemIdActionHandler(StateVariableContextHolder contextHolder) {
    return new GetSystemIdActionHandler(contextHolder);
  }

  @Bean
  UpnpActionHandler<BrowseContext, BrowseResult> browseActionHandler(BrowseActionDelegate browseActionDelegate) {
    return new BrowseActionHandler(browseActionDelegate);
  }

  @Bean
  BrowseActionDelegate browseActionDelegate(ApplicationContext context) {
    return new BrowseActionDelegateImpl(context::getBean);
  }

}
