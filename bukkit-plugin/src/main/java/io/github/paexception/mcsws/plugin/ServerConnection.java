package io.github.paexception.mcsws.plugin;

import org.bukkit.plugin.Plugin;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

public class ServerConnection {

	private final Plugin plugin;
	private ServerSocket server;

	public ServerConnection(Plugin plugin) {
		this.plugin = plugin;
	}

	public void startServer() {
		try {
			this.server = new ServerSocket(25566);
			while (!this.server.isClosed()) {
				Socket socket = this.server.accept();
				while (true) {
					String read = new String(socket.getInputStream().readAllBytes());
					if (read.equalsIgnoreCase("shutdown")) {
						this.plugin.getServer().getOnlinePlayers().forEach(player -> player.kickPlayer("§cThis server shutdown because none of the online player was permitted to keep it alive"));
						this.stopServer();
						this.plugin.getServer().shutdown();
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

	public void stopServer() {
		try {
			this.server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addWakeupPermittedPlayer(String name, UUID uuid) {

	}

	public void removeWakeupPermittedPlayer(String name, UUID uuid) {

	}

	public String listWakeupPermittedPlayers() {
		return null;
	}

}
