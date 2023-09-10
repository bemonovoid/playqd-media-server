package io.playqd.mediaserver.service.upnp.service.contentdirectory;

import org.springframework.util.AntPathMatcher;

import java.util.Arrays;
import java.util.Map;

public final class ObjectIdAntMatchers {

    private static final AntPathMatcher OBJECT_ID_MATCHER = new AntPathMatcher("/");

    public static ObjectIdMatcherResult match(String path) {
        return Arrays.stream(ObjectIdPattern.values())
                .filter(pattern -> matches(pattern, path))
                .findFirst()
                .map(pattern -> ObjectIdMatcherResult.matched(pattern, extractUriTemplateVariables(pattern, path)))
                .orElseGet(ObjectIdMatcherResult::unMatched);
    }

    public static ObjectIdMatcherResult match(ObjectIdPattern pattern, String path) {
        if (matches(pattern, path)) {
            return ObjectIdMatcherResult.matched(pattern, extractUriTemplateVariables(pattern, path));
        }
        return ObjectIdMatcherResult.unMatched();
    }

    public static boolean matches(String pattern, String path) {
        return OBJECT_ID_MATCHER.match(pattern, path);
    }

    public static boolean matches(ObjectIdPattern pattern, String path) {
        return OBJECT_ID_MATCHER.match(pattern.getPattern(), path);
    }

    private static Map<String, String> extractUriTemplateVariables(ObjectIdPattern pattern, String path) {
        return OBJECT_ID_MATCHER.extractUriTemplateVariables(pattern.getPattern(), path);
    }

    private ObjectIdAntMatchers() {

    }

}
