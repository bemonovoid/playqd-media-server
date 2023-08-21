package io.playqd.mediaserver.service.upnp.server;

import lombok.extern.slf4j.Slf4j;
import org.jupnp.model.message.*;
import org.jupnp.model.message.header.UpnpHeader;
import org.jupnp.transport.spi.AbstractStreamClient;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@Slf4j
public class JdkStreamClient extends AbstractStreamClient<JdkStreamClientConfiguration, HttpRequest> {

    private final JdkStreamClientConfiguration configuration;
    private final HttpClient client;

    public JdkStreamClient(JdkStreamClientConfiguration configuration) {
        this.configuration = configuration;
        this.client = HttpClient.newBuilder()
                .executor(configuration.getRequestExecutorService())
                .build();
    }

    @Override
    protected HttpRequest createRequest(StreamRequestMessage requestMessage) {

        var requestBuilder = HttpRequest.newBuilder().uri(requestMessage.getUri());

        var method = requestMessage.getOperation().getMethod();

        switch (method) {
            case GET -> requestBuilder = requestBuilder.GET();
            case POST -> {
                if (requestMessage.getBodyType() == UpnpMessage.BodyType.BYTES) {
                    requestBuilder = requestBuilder.POST(
                            HttpRequest.BodyPublishers.ofByteArray(requestMessage.getBodyBytes()));
                } else {
                    var charset = StandardCharsets.UTF_8;
                    if (requestMessage.getContentTypeCharset() != null) {
                        charset = Charset.forName(requestMessage.getContentTypeCharset());
                    }
                    requestBuilder = requestBuilder.POST(
                            HttpRequest.BodyPublishers.ofString(requestMessage.getBodyString(), charset));
                }
            }
            case SUBSCRIBE -> {
                requestBuilder = requestBuilder.method(
                        requestMessage.getOperation().getHttpMethodName(),
                        HttpRequest.BodyPublishers.noBody());
            }
            default ->
                    throw new IllegalArgumentException(String.format("%s method not yet supported", method.getHttpName()));
        }
        if (!requestMessage.getHeaders().containsKey(UpnpHeader.Type.USER_AGENT)) {
            var userAgent = getConfiguration().getUserAgentValue(
                    requestMessage.getUdaMajorVersion(), requestMessage.getUdaMinorVersion());
            requestBuilder.setHeader("User-Agent", userAgent);
        }
        if (requestMessage.getOperation().getHttpMinorVersion() != 0) {
            requestBuilder = requestBuilder.version(HttpClient.Version.HTTP_1_1);
            // This closes the http connection immediately after the call.
//            requestBuilder.setHeader("Connection", "close");
        }
        for (Map.Entry<String, List<String>> headerEntry : requestMessage.getHeaders().entrySet()) {
            for (String value : headerEntry.getValue()) {
                requestBuilder.setHeader(headerEntry.getKey(), value);
            }
        }

        return requestBuilder.build();
    }

    @Override
    protected Callable<StreamResponseMessage> createCallable(StreamRequestMessage requestMessage, HttpRequest request) {
        return () -> {
            try {
                HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

                log.trace("Received HTTP response: " + response.statusCode());

                // Status
                UpnpResponse responseOperation = new UpnpResponse(response.statusCode(), String.format("Response status code: %s", response.statusCode()));

                // Message
                StreamResponseMessage responseMessage = new StreamResponseMessage(responseOperation);

                // Headers

                responseMessage.setHeaders(new UpnpHeaders(response.headers().map()));

                // Body

                var body = response.body();
                if (body == null || body.length == 0) {
                    log.trace("HTTP response message has no content");
                    return responseMessage;
                }

                if (responseMessage.isContentTypeMissingOrText()) {
                    log.trace("HTTP response message contains text entity");
                    responseMessage.setBodyCharacters(body);
                } else {
                    log.trace("HTTP response message contains binary entity");
                    responseMessage.setBody(UpnpMessage.BodyType.BYTES, body);
                }
                if (log.isTraceEnabled()) {
                    StreamsLoggerHelper.logStreamClientResponseMessage(responseMessage, requestMessage);
                }
                return responseMessage;

            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Override
    protected void abort(HttpRequest request) {

    }

    @Override
    protected boolean logExecutionException(Throwable t) {
        return false;
    }

    @Override
    public void stop() {

    }

    @Override
    public JdkStreamClientConfiguration getConfiguration() {
        return this.configuration;
    }
}
