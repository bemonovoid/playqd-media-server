package io.playqd.mediaserver.config.upnp;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ConditionalOnProperty(prefix = "playqd.upnp", name = "enabled", havingValue = "true", matchIfMissing = true)
public @interface ConditionalOnUpnpEnabled {

}
