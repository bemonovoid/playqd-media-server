package io.playqd.mediaserver.persistence.jpa.entity;

import io.playqd.mediaserver.api.rest.controller.RestControllerApiBasePath;
import io.playqd.mediaserver.config.properties.PlayqdProperties;
import jakarta.persistence.PostLoad;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
class AudioFileEntityEventListener {

    @Autowired
    private ObjectFactory<PlayqdProperties> objectFactory;

    @PostLoad
    public void afterLoad(AudioFileJpaEntity entity) {
        var hostAddress = objectFactory.getObject().buildHostAddress();
        var uri = String.format("http://%s%s/%s", hostAddress, RestControllerApiBasePath.AUDIO_STREAM, entity.getId());
        entity.withAudioFilesStreamUri(uri);
    }
}
