package io.github.paexception.mcsws.server.endpoints;

import io.github.paexception.mcsws.server.Server;
import io.github.paexception.mcsws.server.util.Encryption;
import io.github.paexception.mcsws.server.util.PlayerInfo;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ClientHandler implements Runnable {

	private static final List<ClientHandler> connected = new ArrayList<>();
	private final Encryption encryption = new Encryption();
	private Socket socket;
	private PlayerInfo playerInfo;

	public ClientHandler(Socket socket) {
		this.socket = socket;
	}

	public static void playerDisconnected(UUID uuid, String name) {
		Optional<ClientHandler> clientHandler = connected.stream()
				.filter(handler -> handler.getPlayerInfo().getUuid().equals(uuid)).findFirst();
		if (!clientHandler.isPresent()) return;

		try {
			clientHandler.get().write("await_reconnect");
		} catch (IOException ignored) {
		}

		Server.getMinecraftServerConnection().awaitConnection(
				new PlayerAwaitConnectionTask(
						clientHandler.get(),
						Server.getConfigHandler().getConfig().getMaxRejoinDelay()
				)
		);
		System.out.println("[INFO] " + name + "(" + uuid + ") disconnected from the server. Waiting for reconnect...");
	}

	@Override
	public void run() {
		byte[] buffer;
		try {
			this.socket.getOutputStream().write(this.encryption.getKeyPair().getPublic().getEncoded());
			buffer = this.socket.getInputStream().readAllBytes();
			this.encryption.setKey(new SecretKeySpec(this.encryption.decryptRSA(buffer), "AES"));

			buffer = this.socket.getInputStream().readAllBytes();
			String[] info = new String(this.encryption.decryptAES(buffer)).split("\\.");
			String name = info[0];
			UUID uuid = UUID.fromString(info[1]);
			if (!Server.getConfigHandler().getConfig().getWakeupPermittedPlayers().contains(uuid)) {
				this.write("no_permission");
				this.disconnect();
			} else {
				connected.add(this);
				this.playerInfo = new PlayerInfo(uuid, this.socket.getInetAddress());
				this.playerInfo.setName(name);

				if (Server.getMinecraftServerConnection() == null) {
					Server.startMinecraftServer();
					System.out.println("[INFO] " + name + "(" + uuid + ") triggered the minecraft server to start");
				}
				Server.getMinecraftServerConnection().awaitConnection(
						new PlayerAwaitConnectionTask(
								this,
								Server.getConfigHandler().getConfig().getMaxJoinDelay()
						)
				);
				this.write("await_connection");
			}
		} catch (IOException e) {
			e.printStackTrace();
			this.disconnect();
		}
	}

	public void write(String msg) throws IOException {
		this.socket.getOutputStream().write(this.encryption.encryptAES(msg));
	}

	public void disconnect() {
		try {
			this.socket.close();
		} catch (IOException e) {
			this.socket = null;
		}
		connected.remove(this);

		if (connected.size() == 0) Server.stopMinecraftServer();
	}

	public PlayerInfo getPlayerInfo() {
		return this.playerInfo;
	}

}
