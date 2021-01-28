package io.github.paexception.mcsws.server;

import io.github.paexception.mcsws.server.config.ConfigHandler;
import io.github.paexception.mcsws.server.endpoints.ClientHandler;
import io.github.paexception.mcsws.server.endpoints.MinecraftServerConnection;
import io.github.paexception.mcsws.server.util.CLI;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

	private static final ConfigHandler configHandler = new ConfigHandler();
	private static final ExecutorService executorService = Executors.newCachedThreadPool();
	private static final Timer timer = new Timer();
	private static ServerSocket server;
	private static MinecraftServerConnection minecraftServerConnection;

	public static void main(String[] args) {
		executorService.submit(new CLI());
		start();
	}

	public static void start() {
		System.out.println("[INFO] Starting server...");
		configHandler.loadConfig();
		try {
			server = new ServerSocket(configHandler.getConfig().getPort());
			System.out.println("[INFO] Started server. Listening on " + server.getInetAddress().getHostName()
					+ ":" + server.getLocalPort());
			while (!server.isClosed()) {
				ClientHandler clientHandler = new ClientHandler(server.accept());
				executorService.submit(clientHandler);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void stop() {
		System.out.println("[INFO] Shutting down...");
		try {
			stopMinecraftServer();
			configHandler.storeConfig();
			server.close();
		} catch (IOException e) {
			System.out.println("[ERROR] Something went wrong shutting down the server");
			e.printStackTrace();
		}
		System.exit(0);
	}

	public static void stopMinecraftServer() {
		if (minecraftServerConnection != null) minecraftServerConnection.stopServer();
		minecraftServerConnection = null;
	}

	public static void startMinecraftServer() {
		minecraftServerConnection = new MinecraftServerConnection();
		executorService.submit(minecraftServerConnection);

		try {
			DatagramSocket socket = new DatagramSocket();


		} catch (SocketException e) {
			e.printStackTrace();
		}

		//TODO send magic paket
	}

	public static ConfigHandler getConfigHandler() {
		return configHandler;
	}

	public static Timer getTimer() {
		return timer;
	}

	public static MinecraftServerConnection getMinecraftServerConnection() {
		return minecraftServerConnection;
	}

	public static ExecutorService getExecutorService() {
		return executorService;
	}

}
