package io.playqd.mediaserver.service.upnp.service.contentdirectory;

import jakarta.annotation.Nonnull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ObjectIdMatcherResult {

    private final boolean matched;
    private final ObjectIdPattern pattern;
    private final Map<String, String> extractedTemplateVariables;

    static ObjectIdMatcherResult matched(@Nonnull ObjectIdPattern pattern,
                                         @Nonnull Map<String, String> templateVariables) {
        return new ObjectIdMatcherResult(true, pattern, templateVariables);
    }

    static ObjectIdMatcherResult unMatched() {
        return new ObjectIdMatcherResult(false, null, Collections.emptyMap());
    }

}
