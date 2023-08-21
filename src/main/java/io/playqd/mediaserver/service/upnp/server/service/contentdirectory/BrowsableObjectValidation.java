package io.playqd.mediaserver.service.upnp.server.service.contentdirectory;

import javax.xml.transform.Source;

public interface BrowsableObjectValidation {

    boolean isValid(Source source);
}
