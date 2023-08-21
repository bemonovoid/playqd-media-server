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
import org.jupnp.http.Headers;
import org.jupnp.model.ServerClientTokens;
import org.jupnp.model.UnsupportedDataException;
import org.jupnp.model.message.*;
import org.jupnp.model.message.header.UpnpHeader;
import org.jupnp.transport.impl.DatagramProcessorImpl;
import org.jupnp.transport.spi.DatagramProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

/**
 * UMS impl of DatagramProcessor.
 *
 * Fix DatagramProcessorImpl trace null char as it log the DatagramPacket buffer
 * directly (check on JUPnP update if it's corrected).
 * Set our server header.
 */
@Slf4j
public class PlayqdDatagramProcessor extends DatagramProcessorImpl {

	private static final String SERVER_HTTP_TOKEN = new ServerClientTokens("UMS", "1.0").getHttpToken();

	/**
	 * Overrided as JUPnP logger show null char from the datagram data buffer.
	 * Check on JUPnP update if it's corrected, then remove the overriding method.
	 * @param receivedOnAddress
	 * @param datagram
	 * @return
	 * @throws UnsupportedDataException
	 */
	@Override
	public IncomingDatagramMessage read(InetAddress receivedOnAddress, DatagramPacket datagram) throws UnsupportedDataException {

		try {
			log.trace("Reading message data from: {}:{}\n{}\n{}\n{}",
					datagram.getAddress(),
					datagram.getPort(),
					"===================================== DATAGRAM BEGIN ============================================",
					new String(datagram.getData()).trim(),
					"-===================================== DATAGRAM END ============================================="
			);

			ByteArrayInputStream is = new ByteArrayInputStream(datagram.getData());

			String[] startLine = Headers.readLine(is).split(" ");
			if (startLine[0].startsWith("HTTP/1.")) {
				return readResponseMessage(receivedOnAddress, datagram, is, Integer.parseInt(startLine[1]), startLine[2], startLine[0]);
			} else {
				return readRequestMessage(receivedOnAddress, datagram, is, startLine[0], startLine[2]);
			}

		} catch (Exception ex) {
			throw new UnsupportedDataException("Could not parse headers: " + ex, ex, datagram.getData());
		}
	}

	/**
	 * Overrided as there is no simple way to change the product/version for datagrams message.
	 *
	 * @param message
	 * @return
	 * @throws UnsupportedDataException
	 */
	@Override
	public DatagramPacket write(OutgoingDatagramMessage message) throws UnsupportedDataException {
		if (message.getHeaders().containsKey(UpnpHeader.Type.SERVER)) {
			message.getHeaders().set(UpnpHeader.Type.SERVER.getHttpName(), SERVER_HTTP_TOKEN);
		}

		StringBuilder statusLine = new StringBuilder();

		UpnpOperation operation = message.getOperation();

		if (operation instanceof UpnpRequest requestOperation) {
			statusLine.append(requestOperation.getHttpMethodName()).append(" * ");
			statusLine.append("HTTP/1.").append(operation.getHttpMinorVersion()).append("\r\n");
		} else if (operation instanceof UpnpResponse responseOperation) {
			statusLine.append("HTTP/1.").append(operation.getHttpMinorVersion()).append(" ");
			statusLine.append(responseOperation.getStatusCode()).append(" ").append(responseOperation.getStatusMessage());
			statusLine.append("\r\n");
		} else {
			throw new UnsupportedDataException(
					"Message operation is not request or response, don't know how to process: " + message
			);
		}

		// UDA 1.0, 1.1.2: No body but message must have a blank line after header
		StringBuilder messageData = new StringBuilder();
		messageData.append(statusLine);

		messageData.append(message.getHeaders().toString()).append("\r\n");


		log.trace("Writing message data for {} to: {}:{}\n{}\n{}\n{}",
				message,
				message.getDestinationAddress(),
				message.getDestinationPort(),
				"===================================== DATAGRAM BEGIN ============================================",
				messageData.toString().substring(0, messageData.length() - 2),
				"-===================================== DATAGRAM END ============================================="
		);

		// According to HTTP 1.0 RFC, headers and their values are US-ASCII
		// TODO: Probably should look into escaping rules, too
		byte[] data = messageData.toString().getBytes(StandardCharsets.US_ASCII);

		log.trace("Writing new datagram packet with " + data.length + " bytes for: " + message);
		return new DatagramPacket(data, data.length, message.getDestinationAddress(), message.getDestinationPort());
	}
}
