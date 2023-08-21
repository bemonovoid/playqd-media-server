package io.playqd.mediaserver.config;

import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        WebMvcConfigurer.super.extendMessageConverters(converters);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.stream()
                .filter(MappingJackson2XmlHttpMessageConverter.class::isInstance)
                .map(MappingJackson2XmlHttpMessageConverter.class::cast)
                .findFirst()
                .ifPresent(converter -> converter.getObjectMapper().registerModule(new JaxbAnnotationModule()));
    }

    @Bean
    Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return customizer -> customizer.modules(new JaxbAnnotationModule());
    }

}
