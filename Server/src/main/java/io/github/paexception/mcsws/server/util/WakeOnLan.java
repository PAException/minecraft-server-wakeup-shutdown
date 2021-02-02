package io.github.paexception.mcsws.server.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class WakeOnLan {

	public static void wakeOnLan(byte[] mac, String broadcast, int packageCount) {
		try {
			DatagramSocket socket = new DatagramSocket();

			// WOL packet contains a 6-bytes trailer and 16 times a 6-bytes the MAC address
			byte[] bytes = new byte[17 * 6];

			for (int i = 0; i < 6; i++) bytes[i] = (byte) 0xff;

			for (int n = 6; n < bytes.length; n += 6)
				System.arraycopy(mac, 0, bytes, n, 6);

			DatagramPacket packet = new DatagramPacket(
					bytes,
					bytes.length,
					InetAddress.getByName(broadcast),
					40000
			);

			socket.send(packet);

			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
