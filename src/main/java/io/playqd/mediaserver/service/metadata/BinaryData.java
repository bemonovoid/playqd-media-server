package io.playqd.mediaserver.service.metadata;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Supplier;

@FunctionalInterface
public interface BinaryData extends Supplier<byte[]>, Serializable {

    byte[] get();

    static BinaryData fromLocation(String location) {
        return new BinaryDataFromLocation(location);
    }

    @Slf4j
    class BinaryDataFromLocation implements BinaryData {

        private final String location;

        private BinaryDataFromLocation(String location) {
            this.location = location;
        }

        @Override
        public byte[] get() {
            try {
                return Files.readAllBytes(Paths.get(location));
            } catch (IOException e) {
                log.error("Failed to read album folder image file content.", e);
                return new byte[0];
            }
        }
    }
}
