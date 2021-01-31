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
		try {
			FileInputStream fileInputStream = new FileInputStream(new File(System.getProperty(System.getProperty("user.home") + "\\AppData\\Roaming\\.minecraft\\usercache.json")));
			String string = new String(fileInputStream.readAllBytes());

		} catch (IOException e) {
			e.printStackTrace();
		}

		return new String[0];
	}

}
