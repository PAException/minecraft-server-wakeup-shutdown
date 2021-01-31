package io.github.paexception.mcsws.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.TimerTask;
import java.util.UUID;

public class Client {

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
			Socket socket = new Socket(host, port);
			byte[] buffer = socket.getInputStream().readAllBytes();
			PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(buffer));
			Encryption encryption = new Encryption();
			socket.getOutputStream().write(encryption.encryptRSA(encryption.getKey().getEncoded(), publicKey));

			String[] users = getMinecraftUser();


			while (socket.isConnected()) {
				String input = new String(socket.getInputStream().readAllBytes());
				if (input.equalsIgnoreCase("await_reconnect")) {
					System.out.println("Waiting for reconnection...");
				}else if (input.equalsIgnoreCase("await_connection")) {
					System.out.println("Waiting for connection...");
				}else if (input.equalsIgnoreCase("no_permission")) {
					System.out.println("You are not allowed to start the server!");
				}
			}
			//name.uuid

			//handle: await_reconnect,


		} catch (IOException e) {
			System.out.println("Can't connect to host!");
			System.exit(1);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
		}
	}

	private static String[] getMinecraftUser() {
		String[] id = new String[0];
		try {
			FileInputStream fileInputStream = new FileInputStream(System.getProperty("user.home") + "\\AppData\\Roaming\\.minecraft\\usercache.json");
			String string = new String(fileInputStream.readAllBytes());
			fileInputStream.close();

			id = string.split("},\\{");
			for (int i = 0; i < id.length; i++) {
				id[i] = id[i];
				int indexs=id[i].indexOf("name\":\"") +7;//returns the index of is substring
				int indexe=id[i].indexOf("\",\"");//returns the index of index substring
				int indexs2=id[i].indexOf("uuid\":\"") +7;//returns the index of is substring
				int indexe2=id[i].indexOf("\",\"",indexs2);//returns the index of index substring
				id[i] = id[i].substring( indexs,  indexe) + "." + id[i].substring( indexs2,  indexe2);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return id;
	}

}
