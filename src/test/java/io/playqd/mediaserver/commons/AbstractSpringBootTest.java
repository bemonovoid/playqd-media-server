package io.playqd.mediaserver.commons;

import io.playqd.mediaserver.config.ApplicationConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = ApplicationConfiguration.class)
public abstract class AbstractSpringBootTest {

}
