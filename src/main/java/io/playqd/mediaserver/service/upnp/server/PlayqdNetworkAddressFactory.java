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

import org.jupnp.transport.impl.NetworkAddressFactoryImpl;
import org.jupnp.transport.spi.InitializationException;

import java.net.InetAddress;
import java.net.NetworkInterface;

/**
 * NetworkAddressFactory that use MediaServer config to find the upnp interface, ip and port.
 */
public class PlayqdNetworkAddressFactory extends NetworkAddressFactoryImpl {

	private static InetAddress inetAddress;
	private static String hostname;
	private static NetworkInterface networkInterface;

	public PlayqdNetworkAddressFactory() throws InitializationException {
		this(5001, DEFAULT_MULTICAST_RESPONSE_LISTEN_PORT);
	}

	public PlayqdNetworkAddressFactory(int streamListenPort, int multicastResponsePort) throws InitializationException {
		this.streamListenPort = streamListenPort;
		this.multicastResponsePort = multicastResponsePort;
	}

	@Override
	protected void discoverNetworkInterfaces() throws InitializationException {
		synchronized (networkInterfaces) {
			if (networkInterface == null) {
				initNetworkInterface();
			}
			networkInterfaces.add(networkInterface);
		}
	}

	@Override
	protected void discoverBindAddresses() throws InitializationException {
		synchronized (bindAddresses) {
			bindAddresses.add(inetAddress);
		}
	}

	private static void initNetworkInterface() {
		NetworkInterfaceAssociation ia = NetworkConfiguration.getNetworkInterfaceAssociationFromConfig();
		if (ia != null) {
			inetAddress = ia.getAddr();
			hostname = inetAddress.getHostAddress();
			networkInterface = ia.getIface();
		}
	}
}
