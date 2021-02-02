package io.github.paexception.mcsws.plugin;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.UUID;

public class ServerConnection implements Runnable {

	private final MCSWS plugin;
	private ServerSocket server;
	private Socket socket;

	public ServerConnection(MCSWS plugin) {
		this.plugin = plugin;
	}

	public void stopServer() {
		try {
			this.server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addWakeupPermittedPlayer(String name, UUID uuid) {
		try {
			this.write("add." + name + "." + uuid);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void removeWakeupPermittedPlayer(String name, UUID uuid) {
		try {
			this.write("remove." + name + "." + uuid);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String listWakeupPermittedPlayers() {
		String uuids = null;
		try {
			this.write("list");
			uuids = this.read();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return uuids;
	}

	@Override
	public void run() {
		try {
			this.server = new ServerSocket(25566);
			while (!this.server.isClosed()) {
				this.socket = this.server.accept();
				while (true) {
					String read = this.read();
					if (read.equalsIgnoreCase("shutdown")) {
						this.plugin.getServer().getOnlinePlayers().forEach(player -> player.kickPlayer("§cThis server shutdown because none of the online player was permitted to keep it alive"));
						this.plugin.shutdown();
						break;
					} else if (read.equalsIgnoreCase("possible_shutdown")) {
						this.plugin.getServer().broadcastMessage("§cThis server will shutdown if a permitted player won't connect in the future!");
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void write(String msg) throws IOException {
		this.socket.getOutputStream().write(ByteBuffer.allocate(4).putInt(msg.getBytes().length).array());
		this.socket.getOutputStream().write(msg.getBytes());
		this.socket.getOutputStream().flush();
	}

	public String read() throws IOException {
		byte[] buffer = new byte[ByteBuffer.wrap(this.socket.getInputStream().readNBytes(4)).getInt()];
		this.socket.getInputStream().readNBytes(buffer, 0, buffer.length);

		return new String(buffer);
	}

}
