package io.playqd.mediaserver.service.upnp.service.contentdirectory;

import io.playqd.mediaserver.service.upnp.service.ActionContext;

public class SimpleActionContext extends ActionContext<Void> {

  public SimpleActionContext() {
    super();
  }

  @Override
  public Void getRequest() {
    return null;
  }

}
