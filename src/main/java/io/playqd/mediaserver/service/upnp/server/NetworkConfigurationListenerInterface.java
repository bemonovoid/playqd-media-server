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

import java.net.InetAddress;
import java.net.NetworkInterface;

public interface NetworkConfigurationListenerInterface {
	void networkInterfaceAdded(NetworkInterface networkInterface, InetAddress address);
	void networkInterfaceRemoved(NetworkInterface networkInterface, InetAddress address);
	void networkInterfaceUp(NetworkInterfaceAssociation networkInterfaceAssociation);
	void networkInterfaceDown(NetworkInterfaceAssociation networkInterfaceAssociation);
	void networkInterfaceAddressAdded(NetworkInterface networkInterface, InetAddress address);
	void networkInterfaceDefaultAddressChanged(NetworkInterface networkInterface, InetAddress address);
	void networkInterfaceWithAddressAdded();
	void networkInterfaceAddressRemoved(NetworkInterface networkInterface, InetAddress address);
}
