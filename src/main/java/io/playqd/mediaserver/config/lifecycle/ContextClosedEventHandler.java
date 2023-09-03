package io.playqd.mediaserver.config.lifecycle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
class ContextClosedEventHandler {

  private static final String APPLICATION_PID_FILE = "application.pid";

  @EventListener(ContextClosedEvent.class)
  public void handleContextClosedEvent(ContextClosedEvent event) {
    Executors.newSingleThreadScheduledExecutor().schedule(ContextClosedEventHandler::killSelf, 2, TimeUnit.SECONDS);
  }

  private static void killSelf() {
    var pidFile = new File(APPLICATION_PID_FILE);
    if (pidFile.exists()) {
      try {
        var fileLines = Files.readAllLines(pidFile.toPath());
        if (CollectionUtils.isEmpty(fileLines)) {
          log.error("Was unable to retrieve pid from {} file. File was empty. This process won't be killed.",
              APPLICATION_PID_FILE);
          return;
        }
        if (fileLines.size() > 1) {
          log.warn("Unexpected content in {} file. Expected lines: 1, but was : {}. " +
                  "Will attempt to read the pid from the first line.", APPLICATION_PID_FILE, fileLines.size());
        }
        var pid = "";
        try {
          pid = Files.readAllLines(pidFile.toPath()).get(0);
          ProcessHandle.of(Long.parseLong(pid))
              .filter(ProcessHandle::isAlive)
              .ifPresentOrElse(
                  ContextClosedEventHandler::killInternal,
                  () -> log.warn("I am no longer alive. Hope I said bye bye."));
        } catch (NumberFormatException e) {
          log.warn("Unable to parse pid from {} file, pid must be a number, but was: {}", APPLICATION_PID_FILE, pid);
        }
      } catch (IOException e) {
        log.error("Something went wrong when killing the process.", e);
      }
    } else {
      log.error("Was unable to retrieve pid from {} file. File does not exist. This process won't be killed.",
          APPLICATION_PID_FILE);
    }
  }

  private static void killInternal(ProcessHandle processHandle) {
    try {
      var command = "taskkill /F /T /PID " + processHandle.pid();
      Runtime.getRuntime().exec(command);
    }  catch (IOException e) {
      log.error("Something went wrong when killing the process.", e);
    }
  }

}
