package io.github.paexception.mcsws.client;

import io.github.paexception.EncryptedSocket;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class Client {

	private static EncryptedSocket socket;

	public static void main(String[] args) {
		String host;
		int port;
		try {
			if (args.length < 2) throw new IllegalArgumentException("Host as first and port as second argument needed");
			host = args[0];
			port = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("That's not a valid port!");
		}
		try {
			System.out.println("Opening encrypted connection...");
			String[] users = getMinecraftUser();
			for (int i = 0; i < users.length; i++) {
				socket = new EncryptedSocket(host, port);
				socket.handshakeClient();
				socket.write(users[i]);
				String read = socket.read();
				if (read.equalsIgnoreCase("no_permission")) {
					System.out.println(users[i].split("\\.")[0] + "doesn't have the permission to wakeup the server");
					if (i == users.length - 1)
						System.err.println("You don't have the permission to wakeup the server");
				} else if (read.equalsIgnoreCase("await_connection")) {
					System.out.println("Connected and authorized!");
					System.out.println("Waiting for connection of player on server...");
					break;
				}
			}

			while (socket.getSocket().isConnected()) {
				String input = socket.read();
				if (input.equalsIgnoreCase("await_reconnect"))
					System.out.println("Waiting for reconnection...");
				else if (input.equalsIgnoreCase("await_connection"))
					System.out.println("Waiting for connection of player on server...");
				else if (input.equalsIgnoreCase("no_permission"))
					System.out.println("You are not allowed to wakeup the server!");
				else if (input.equalsIgnoreCase("player_not_connected"))
					System.out.println("Disconnecting because did not connect to minecraft server");
			}
		} catch (IOException e) {
			System.out.println("Disconnected");
			System.exit(0);
		} catch (GeneralSecurityException e) {
			System.err.println("An error occurred while encrypting:");
			e.printStackTrace();
		}
	}

	private static String[] getMinecraftUser() {
		String[] users = new String[0];
		try {
			FileInputStream fileInputStream = new FileInputStream(System.getProperty("user.home") + "\\AppData\\Roaming\\.minecraft\\usercache.json");
			String string = new String(fileInputStream.readAllBytes());
			fileInputStream.close();

			users = string.split("},\\{");
			for (int i = 0; i < users.length; i++)
				users[i] = users[i].substring(users[i].indexOf("name\":\"") + 7, users[i].indexOf("\"", users[i].indexOf("name\":\"") + 7))
						+ "."
						+ users[i].substring(users[i].indexOf("uuid\":\"") + 7, users[i].indexOf("\"", users[i].indexOf("uuid\":\"") + 7));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return users;
	}

}
