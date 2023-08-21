package io.playqd.mediaserver.persistence.jpa.entity.embeddable;

import io.playqd.mediaserver.model.ExternalMediaServiceName;
import io.playqd.mediaserver.model.MediaItemExternalRef;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class EmbeddableMediaItemExternalRef implements MediaItemExternalRef {

    private String refId;

    private ExternalMediaServiceName serviceName;
}
