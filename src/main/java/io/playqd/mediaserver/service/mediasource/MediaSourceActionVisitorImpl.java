package io.playqd.mediaserver.service.mediasource;

import org.springframework.stereotype.Component;

@Component
class MediaSourceActionVisitorImpl implements MediaSourceActionVisitor {

    private final MediaSourceScannerService mediaSourceScannerService;

    public MediaSourceActionVisitorImpl(MediaSourceScannerService mediaSourceScannerService) {
        this.mediaSourceScannerService = mediaSourceScannerService;
    }

    @Override
    public void visit(MediaSourceAction action) {

    }

    @Override
    public void visit(StartScanAction action) {
        visit((MediaSourceAction) action);
        mediaSourceScannerService.scan(action.getId());
    }
}
