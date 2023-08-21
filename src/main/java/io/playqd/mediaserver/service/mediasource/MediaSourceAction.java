package io.playqd.mediaserver.service.mediasource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PACKAGE)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(@JsonSubTypes.Type(value = StartScanAction.class, name = StartScanAction.TYPE_NAME))
public abstract class MediaSourceAction implements VisitableMediaSourceAction {

    @Positive
    private long id;

    @Override
    @JsonIgnore
    public void accept(MediaSourceActionVisitor visitor) {
        visitor.visit(this);
    }
}
