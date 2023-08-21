package io.playqd.mediaserver.service.mediasource;


public interface MediaSourceWatcherService {

    void watch(MediaSource mediaSource);

    void stop(long sourceId);
}
