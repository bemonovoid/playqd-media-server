package io.playqd.mediaserver.commons;

import io.playqd.mediaserver.config.ApplicationConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({ ApplicationConfiguration.class})
public abstract class JpaDaoTest {

}
