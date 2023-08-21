package io.playqd.mediaserver.service.mediasource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(StartScanAction.TYPE_NAME)
public class StartScanAction extends MediaSourceAction {

    static final String TYPE_NAME = "scan";

    boolean deleteAllBeforeScan;

    @Override
    @JsonIgnore
    public void accept(MediaSourceActionVisitor visitor) {
        visitor.visit(this);
    }
}
