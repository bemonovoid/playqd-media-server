/*
 * This file is part of Universal Media Server, based on PS3 Media Server.
 *
 * This program is a free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; version 2 of the License only.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package io.playqd.mediaserver.service.upnp.server;

import lombok.extern.slf4j.Slf4j;
import org.jupnp.model.message.StreamRequestMessage;
import org.jupnp.model.message.StreamResponseMessage;
import org.jupnp.model.message.UpnpMessage;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Uniformize logging for UPnP HTTP streams
 */
@Slf4j
public class StreamsLoggerHelper {

	private static final String HTTPSERVER_REQUEST_BEGIN =  "================================== HTTPSERVER REQUEST BEGIN =====================================";
	private static final String HTTPSERVER_REQUEST_END =    "================================== HTTPSERVER REQUEST END =======================================";
	private static final String HTTPSERVER_RESPONSE_BEGIN = "================================== HTTPSERVER RESPONSE BEGIN ====================================";
	private static final String HTTPSERVER_RESPONSE_END =   "================================== HTTPSERVER RESPONSE END ======================================";
	private static final String HTTPCLIENT_REQUEST_BEGIN =  "==================================== HTTPCLIENT REQUEST BEGIN ====================================";
	private static final String HTTPCLIENT_REQUEST_END =    "==================================== HTTPCLIENT REQUEST END ======================================";
	private static final String HTTPCLIENT_RESPONSE_BEGIN = "==================================== HTTPCLIENT RESPONSE BEGIN ===================================";
	private static final String HTTPCLIENT_RESPONSE_END =   "==================================== HTTPCLIENT RESPONSE END =====================================";

	/**
	 * This class is not meant to be instantiated.
	 */
	private StreamsLoggerHelper() {
	}

	public static void logStreamServerRequestMessage(StreamRequestMessage requestMessage) {
		String formattedRequest = getFormattedRequest(requestMessage);
		String formattedHeaders = getFormattedHeaders(requestMessage);
		String formattedBody = getFormattedBody(requestMessage);
		log.trace(
			"Received a request from {}:\n{}\n{}{}{}{}",
			requestMessage.getConnection().getRemoteAddress().getHostAddress(),
			HTTPSERVER_REQUEST_BEGIN,
			formattedRequest,
			formattedHeaders,
			formattedBody,
			HTTPSERVER_REQUEST_END
		);
	}

	public static void logStreamServerResponseMessage(StreamResponseMessage responseMessage, StreamRequestMessage requestMessage) {
		String formattedResponse = getFormattedResponse(responseMessage);
		String formattedHeaders = getFormattedHeaders(responseMessage);
		String formattedBody = getFormattedBody(responseMessage);

		log.trace(
			"Send a response to {}:\n{}\n{}{}{}{}",
			requestMessage.getConnection().getRemoteAddress().getHostAddress(),
			HTTPSERVER_RESPONSE_BEGIN,
			formattedResponse,
			formattedHeaders,
			formattedBody,
			HTTPSERVER_RESPONSE_END
		);
	}

	public static void logStreamClientRequestMessage(StreamRequestMessage requestMessage) {
		String formattedRequest = getFormattedRequest(requestMessage);
		String formattedHeaders = getFormattedHeaders(requestMessage);
		String formattedBody = getFormattedBody(requestMessage);
		log.trace(
			"Send a request to {}:\n{}\n{}{}{}{}",
			requestMessage.getUri().getHost(),
			HTTPCLIENT_REQUEST_BEGIN,
			formattedRequest,
			formattedHeaders,
			formattedBody,
			HTTPCLIENT_REQUEST_END
		);
	}

	public static void logStreamClientResponseMessage(StreamResponseMessage responseMessage, StreamRequestMessage requestMessage) {
		String formattedResponse = getFormattedResponse(responseMessage);
		String formattedHeaders = getFormattedHeaders(responseMessage);
		String formattedBody = getFormattedBody(responseMessage);
		log.trace(
			"Received a response from {}:\n{}\n{}{}{}{}",
			requestMessage.getUri().getHost(),
			HTTPCLIENT_RESPONSE_BEGIN,
			formattedResponse,
			formattedHeaders,
			formattedBody,
			HTTPCLIENT_RESPONSE_END
		);
	}

	private static String getFormattedRequest(StreamRequestMessage requestMessage) {
		StringBuilder request = new StringBuilder();
		request.append(requestMessage.getOperation().getHttpMethodName()).append(" ").append(requestMessage.getUri().getPath());
		request.append(" HTTP/1.").append(requestMessage.getOperation().getHttpMinorVersion()).append("\n");
		return request.toString();
	}

	private static String getFormattedResponse(StreamResponseMessage responseMessage) {
		StringBuilder response = new StringBuilder();
		response.append("HTTP/1.").append(responseMessage.getOperation().getHttpMinorVersion());
		response.append(" ").append(responseMessage.getOperation().getResponseDetails()).append("\n");
		return response.toString();
	}

	private static String getFormattedHeaders(UpnpMessage message) {
		StringBuilder headers = new StringBuilder();
		for (Map.Entry<String, List<String>> entry : message.getHeaders().entrySet()) {
			if (StringUtils.hasText(entry.getKey())) {
				for (String value : entry.getValue()) {
					headers.append("  ").append(entry.getKey()).append(": ").append(value).append("\n");
				}
			}
		}
		if (headers.length() > 0) {
			headers.insert(0, "\nHEADER:\n");
		}
		return headers.toString();
	}

	private static String getFormattedBody(UpnpMessage message) {
		String formattedBody = "";
		//message.isBodyNonEmptyString throw StringIndexOutOfBoundsException if string is empty
		try {
			boolean bodyNonEmpty = message.getBody() != null &&
					((message.getBody() instanceof String && ((String) message.getBody()).length() > 0) ||
					(message.getBody() instanceof byte[] && ((byte[]) message.getBody()).length > 0));
			if (bodyNonEmpty && message.isBodyNonEmptyString()) {
				if (message.getContentTypeHeader().isText()) {
					try {
//						var xmlMapper = new XmlMapper().readValue(message.getBodyString(), String.class);
//						xmlMapper.
//						formattedBody = StringUtil.prettifyXML(message.getBodyString(), StandardCharsets.UTF_8, 4);
						formattedBody = message.getBodyString();
					} catch (Exception e) {
						formattedBody = "  Content isn't valid XML, using text formatting: " + e.getMessage()  + "\n";
						formattedBody += "    " + message.getBodyString().replace("\n", "\n    ");
					}
				} else {
					formattedBody = message.getBodyString();
				}
			}
		} catch (Exception e) {
			formattedBody = "";
		}
		if (StringUtils.hasText(formattedBody)) {
			formattedBody = "\nCONTENT:\n" + formattedBody;
		} else {
			formattedBody = "";
		}
		return formattedBody;
	}

}
