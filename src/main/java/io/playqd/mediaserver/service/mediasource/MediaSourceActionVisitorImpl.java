package io.playqd.mediaserver.service.mediasource;

import org.springframework.stereotype.Component;

@Component
class MediaSourceActionVisitorImpl implements MediaSourceActionVisitor {

    private final MediaSourceScanner mediaSourceScanner;

    public MediaSourceActionVisitorImpl(MediaSourceScanner mediaSourceScanner) {
        this.mediaSourceScanner = mediaSourceScanner;
    }

    @Override
    public void visit(MediaSourceAction action) {

    }

    @Override
    public void visit(StartScanAction action) {
        visit((MediaSourceAction) action);
        mediaSourceScanner.scan(action.getId());
    }
}
