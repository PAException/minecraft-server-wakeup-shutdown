package io.github.paexception.mcsws.server.util;

import io.github.paexception.mcsws.server.Server;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class WakeOnLan {

	private final byte[] mac;

	public WakeOnLan(byte[] mac) {
		this.mac = mac;
	}

	public void wakeOnLan() {
		try {
			DatagramSocket socket = new DatagramSocket();

			byte[] bytes = new byte[16 * 7];

			for (int i = 0; i < 6; i++) bytes[i] = (byte) 0xff;

			for (int n = 6; n < bytes.length; n += 6)
				System.arraycopy(this.mac, 0, bytes, n, 6);

			DatagramPacket packet = new DatagramPacket(
					bytes,
					bytes.length,
					InetAddress.getByName(Server.getConfigHandler().getConfig().getSubnetmask()),
					40000
			);

			for (int i = 0; i < Server.getConfigHandler().getConfig().getWakeOnLanPackageCount(); i++)
				socket.send(packet);

			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
