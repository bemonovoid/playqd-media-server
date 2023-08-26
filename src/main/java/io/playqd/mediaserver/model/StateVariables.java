package io.playqd.mediaserver.model;

import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;

public enum StateVariables {

    LOCAL_DEVICE_ID("LocalDeviceID", Objects::toString, value -> value),

    SYSTEM_UPDATE_ID("SystemUpdateID", Objects::toString, Integer::valueOf);

    @Getter
    private final String variableName;

    @Getter
    private final Function<Serializable, String> serializer;

    @Getter
    private final Function<String, ? extends Serializable> deserializer;

    StateVariables(String variableName,
                   Function<Serializable, String> serializer,
                   Function<String, ? extends Serializable> deserializer) {
        this.variableName = variableName;
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

}
