package io.playqd.mediaserver.service.upnp.service.contentdirectory.impl;

import io.playqd.mediaserver.service.upnp.service.StateVariableContextHolder;
import io.playqd.mediaserver.service.upnp.service.StateVariableName;
import io.playqd.mediaserver.service.upnp.service.UpnpActionHandler;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.SimpleActionContext;

public class GetSystemIdActionHandler implements UpnpActionHandler<SimpleActionContext, Integer> {

  private final StateVariableContextHolder stateVariableContextHolder;

  public GetSystemIdActionHandler(StateVariableContextHolder stateVariableContextHolder) {
    this.stateVariableContextHolder = stateVariableContextHolder;
  }

  @Override
  public Integer handle(SimpleActionContext context) {
    return stateVariableContextHolder.getOrThrow(StateVariableName.UPNP_SYSTEM_UPDATE_ID);
  }

}
