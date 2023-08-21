package io.playqd.mediaserver.service.mediasource;

public interface MediaSourceActionVisitor {

    void visit(MediaSourceAction action);

    void visit(StartScanAction action);
}
