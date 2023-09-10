package io.playqd.mediaserver.config;

import io.playqd.mediaserver.api.soap.interceptor.PrettyPrintPayloadLoggingInterceptor;
import io.playqd.mediaserver.config.properties.PlayqdProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

import java.util.List;

/**
 * http://localhost:8080/ws/countries.wsdl
 */
@EnableWs
@Configuration
class PlayqdWsConfigurerAdapter extends WsConfigurerAdapter {

    private static final String[] SERVICE_URL_MAPPINGS = {"/ws/*"};

    @Autowired
    private PlayqdProperties playqdProperties;

    @Bean
    ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, SERVICE_URL_MAPPINGS);
    }

    @Override
    public void addInterceptors(List<EndpointInterceptor> interceptors) {
        interceptors.add(new PrettyPrintPayloadLoggingInterceptor(playqdProperties.getLogging()));
    }

    @Bean
    public XsdSchema browseActionSchema() {
        return new SimpleXsdSchema(new ClassPathResource("/schemas/upnp/browse.xsd"));
    }

    @Bean(name = "contentDirectoryBrowseAction")
    DefaultWsdl11Definition contentDirectoryBrowseActionWsdl11Definition(XsdSchema browseActionSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("BrowsePort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace("http://www.playqd.io/webservice/model/xsd");
        wsdl11Definition.setSchema(browseActionSchema);
        return wsdl11Definition;
    }

//    @Bean
//    Jaxb2Marshaller jaxb2Marshaller() {
//        var mar = new Jaxb2Marshaller();
//        mar.setPackagesToScan("io.playqd.upnp.ws.xsd.upnp");
////        mar.setProcessExternalEntities(true);
////        mar.setCheckForXmlRootElement(false);
//        return mar;
//    }
}
