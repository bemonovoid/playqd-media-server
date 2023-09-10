package io.playqd.mediaserver.config.lifecycle;

import io.playqd.mediaserver.service.upnp.service.StateVariableContextHolder;
import io.playqd.mediaserver.service.upnp.service.StateVariableName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.time.format.DateTimeFormatter;

@Slf4j
public class UpnpStateVariablesInitializer implements ApplicationRunner {

    private final StateVariableContextHolder stateVariableContextHolder;

    public UpnpStateVariablesInitializer(StateVariableContextHolder stateVariableContextHolder) {
        this.stateVariableContextHolder = stateVariableContextHolder;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        initSystemUpdateId();

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

    private void initSystemUpdateId() {
        stateVariableContextHolder.getOrUpdate(StateVariableName.SYSTEM_UPDATE_ID, () -> 1);
    }
}
