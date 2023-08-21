package io.playqd.mediaserver.service.upnp.server.service;

public interface UpnpActionHandler<T extends ActionContext<?>, R> {

    /**
     *
     * @param context
     * @throws UpnpActionHandlerException if fails
     * @return
     */
    R handle(T context);
}
