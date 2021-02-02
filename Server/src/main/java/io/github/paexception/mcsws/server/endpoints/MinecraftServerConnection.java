package io.github.paexception.mcsws.server.endpoints;

import io.github.paexception.mcsws.server.Server;
import io.github.paexception.mcsws.server.util.PlayerInfo;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.UUID;

public class MinecraftServerConnection implements Runnable {

	private final List<PlayerAwaitConnectionTask> awaitingConnections = new ArrayList<>();
	private Socket socket;

	@Override
	public void run() {
		try {
			this.socket = new Socket(
					Server.getConfigHandler().getConfig().getRemoteHost(),
					Server.getConfigHandler().getConfig().getRemotePort()
			);

			this.handle();
		} catch (IOException e) {
			Server.getTimer().schedule(new TimerTask() {
				@Override
				public void run() {
					MinecraftServerConnection.this.run();
				}
			}, 10000);
		}

	}

	private void handle() {
		System.out.println("[INFO] Established connection to minecraft server");
		this.awaitingConnections.forEach(task -> Server.getTimer().schedule(task, task.getTimout()));
		Server.getExecutorService().submit(new MinecraftServerConnectionInputStreamHandler(this.socket));
	}

	public void stopServer() {
		try {
			this.socket.getOutputStream().write("shutdown".getBytes().length);
			this.socket.getOutputStream().write("shutdown".getBytes());
			this.socket.close();
		} catch (IOException e) {
			this.socket = null;
		}
	}

	public void awaitConnection(PlayerAwaitConnectionTask task) {
		if (this.socket != null && this.socket.isConnected()) Server.getTimer().schedule(task, task.getTimout());
		this.awaitingConnections.add(task);
	}

	private class MinecraftServerConnectionInputStreamHandler implements Runnable {

		private final Socket socket;
		private String[] input;

		public MinecraftServerConnectionInputStreamHandler(Socket socket) {
			this.socket = socket;
		}

		/**
		 * Handles information send by the minecraft server in the following syntax:
		 * action.name.uuid
		 */
		@Override
		public void run() {
			try {
				while (this.socket.isConnected()) {
					this.input = this.read().split("\\.");
					if (this.input.length == 3 && PlayerInfo.isUUID(this.input[2])) {
						if (this.input[0].equalsIgnoreCase("join")) {
							System.out.println("[INFO] " + this.input[1] + "(" + this.input[2]
									+ ") successful connected to the server");
							MinecraftServerConnection.this.awaitingConnections.stream().filter(task ->
									UUID.fromString(this.input[2]).equals(task.getUUID()))
									.findFirst().ifPresent(task -> {
								task.getClientHandler().getPlayerInfo().connects();
								task.cancel();
							});
						} else if (this.input[0].equalsIgnoreCase("quit")) {
							if (ClientHandler.playerDisconnected(UUID.fromString(this.input[2]), this.input[1]))
								MinecraftServerConnection.this.socket
										.getOutputStream().write("possible_shutdown".getBytes());
						} else if (this.input[0].equalsIgnoreCase("add")) {
							Server.getConfigHandler().getConfig()
									.addWakeupPermittedPlayer(UUID.fromString(this.input[2]));
						} else if (this.input[0].equalsIgnoreCase("remove")) {
							Server.getConfigHandler().getConfig()
									.removeWakeupPermittedPlayer(UUID.fromString(this.input[2]));
						} else if (this.input[0].equalsIgnoreCase("list")) {
							final StringBuilder send = new StringBuilder();
							Server.getConfigHandler().getConfig().getWakeupPermittedPlayers().forEach(uuid -> {
								send.append(uuid).append(".");
							});
							send.deleteCharAt(send.length() - 1);
							this.write(send.toString());
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				if (!this.socket.isConnected()) {
					Server.stopMinecraftServer();
					Server.startMinecraftServer();
				}
			}
		}

		public String read() throws IOException {
			byte[] bytes = new byte[this.socket.getInputStream().read()];
			this.socket.getInputStream().readNBytes(bytes, 0, bytes.length);

			return new String(bytes);
		}

		public void write(String msg) throws IOException {
			this.socket.getOutputStream().write(msg.getBytes().length);
			this.socket.getOutputStream().write(msg.getBytes());
		}

	}

}
