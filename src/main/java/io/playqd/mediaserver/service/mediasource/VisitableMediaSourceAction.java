package io.playqd.mediaserver.service.mediasource;

public interface VisitableMediaSourceAction {

    void accept(MediaSourceActionVisitor visitor);
}
