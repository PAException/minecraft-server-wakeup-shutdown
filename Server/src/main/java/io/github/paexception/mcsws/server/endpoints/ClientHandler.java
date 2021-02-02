package io.github.paexception.mcsws.server.endpoints;

import io.github.paexception.EncryptedSocket;
import io.github.paexception.mcsws.server.Server;
import io.github.paexception.mcsws.server.util.PlayerInfo;
import java.io.IOException;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ClientHandler implements Runnable {

	private static final List<ClientHandler> connected = new ArrayList<>();
	private final EncryptedSocket socket;
	private PlayerInfo playerInfo;

	public ClientHandler(Socket socket) {
		this.socket = new EncryptedSocket(socket);
	}

	public static boolean playerDisconnected(UUID uuid, String name) {
		Optional<ClientHandler> clientHandler = connected.stream()
				.filter(handler -> handler.getPlayerInfo().getUuid().equals(uuid)).findFirst();
		if (!clientHandler.isPresent()) return false;

		try {
			clientHandler.get().socket.write("await_reconnect");
		} catch (IOException | GeneralSecurityException ignored) {
		}

		Server.getMinecraftServerConnection().awaitConnection(
				new PlayerAwaitConnectionTask(
						clientHandler.get(),
						Server.getConfigHandler().getConfig().getMaxRejoinDelay() * 1000
				)
		);
		System.out.println("[INFO] " + name + "(" + uuid + ") disconnected from the server. Waiting for reconnect...");
		return true;
	}

	@Override
	public void run() {
		try {
			this.socket.handshakeServer();

			String[] info = this.socket.read().split("\\.");
			UUID uuid = UUID.fromString(info[1]);
			if (!Server.getConfigHandler().getConfig().getWakeupPermittedPlayers().contains(uuid)) {
				this.socket.write("no_permission");
				this.disconnect();
			} else {
				connected.add(this);
				this.playerInfo = new PlayerInfo(uuid, this.socket.getSocket().getInetAddress());
				this.playerInfo.setName(info[0]);

				if (Server.getMinecraftServerConnection() == null) {
					Server.startMinecraftServer();
					System.out.println("[INFO] " + info[0] + "(" + uuid + ") triggered the minecraft server to start");
				}
				Server.getMinecraftServerConnection().awaitConnection(
						new PlayerAwaitConnectionTask(
								this,
								Server.getConfigHandler().getConfig().getMaxJoinDelay() * 1000
						)
				);
				this.socket.write("await_connection");
			}
		} catch (IOException e) {
			System.out.println("[INFO] " + this.socket.getSocket().getInetAddress() + " threw an exception: " + e.getMessage());
			this.disconnect();
		} catch (GeneralSecurityException e) {
			System.out.println("[INFO] " + this.socket.getSocket() + " threw an exception: " + e.getMessage());
		}
	}

	public void disconnect() {
		try {
			this.socket.getSocket().close();
		} catch (IOException ignored) {
		}
		connected.remove(this);

		if (connected.size() == 0) Server.stopMinecraftServer();
	}

	public PlayerInfo getPlayerInfo() {
		return this.playerInfo;
	}

	public EncryptedSocket getSocket() {
		return this.socket;
	}

}
