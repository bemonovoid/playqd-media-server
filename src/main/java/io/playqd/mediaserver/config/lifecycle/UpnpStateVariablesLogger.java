package io.playqd.mediaserver.config.lifecycle;

import io.playqd.mediaserver.service.upnp.server.service.StateVariableContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@Order(PlayqdApplicationRunnerOrder.UPNP_STATE_VARIABLES_LOGGER)
class UpnpStateVariablesLogger implements ApplicationRunner {

    private final StateVariableContextHolder stateVariableContextHolder;

    UpnpStateVariablesLogger(StateVariableContextHolder stateVariableContextHolder) {
        this.stateVariableContextHolder = stateVariableContextHolder;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        var stringBuilder = new StringBuilder();

        stringBuilder.append("\n\n---- State variables START-----\n");

        stateVariableContextHolder.getAll()
                .forEach(stateVariable -> {
                    stringBuilder
                            .append(stateVariable.name())
                            .append(" = ")
                            .append(stateVariable.value())
                            .append(" (")
                            .append("lastModifiedDate = ")
                            .append(stateVariable.lastModifiedDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                            .append(')')
                            .append('\n');
                });
        stringBuilder.append("---- State variables END-----\n");

        log.info(stringBuilder.toString());
    }
}
