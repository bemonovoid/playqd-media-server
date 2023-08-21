package io.playqd.mediaserver.templates;

import freemarker.template.Configuration;
import io.playqd.mediaserver.exception.PlayqdException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.Map;

@Slf4j
@Service
class TemplateServiceImpl implements TemplateService {

    private final Configuration configuration;

    TemplateServiceImpl(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String processToString(String templateName, Map<String, Object> data) {
        try {
            var template = configuration.getTemplate(templateName);
            var stringWriter = new StringWriter();
            template.process(data, stringWriter);
            return stringWriter.getBuffer().toString();
        } catch (Exception e) {
            throw new PlayqdException(e);
        }
    }
}
