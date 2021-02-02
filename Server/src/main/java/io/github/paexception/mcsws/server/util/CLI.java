package io.github.paexception.mcsws.server.util;

import io.github.paexception.mcsws.server.Server;
import io.github.paexception.mcsws.server.endpoints.ClientHandler;
import java.util.Scanner;
import java.util.UUID;

public class CLI implements Runnable {

	private final Scanner sc = new Scanner(System.in);

	@Override
	public void run() {
		String[] input;
		while (true) {
			input = this.sc.nextLine().split(" ");
			if (input.length > 0) {
				if (input[0].equalsIgnoreCase("stop")) break;
				else if (input[0].equalsIgnoreCase("reload")
						|| input[0].equalsIgnoreCase("rl")) {
					Server.getConfigHandler().loadConfig();
					System.out.println("[INFO] Reloaded config! (Networking changes apply by restarting the server)");
				} else if (input[0].equalsIgnoreCase("mc")) {
					if (input.length == 1) System.out.println("mc <start|stop>");
					else if (input[1].equalsIgnoreCase("start"))
						if (Server.getMinecraftServerConnection() == null) {
							Server.startMinecraftServer();
							System.out.println("[INFO] Triggered minecraft server to start");
						} else System.out.println("[INFO] Minecraft server already running or starting");
					else if (Server.getMinecraftServerConnection() != null) {
						Server.startMinecraftServer();
						System.out.println("[INFO] Shutting down minecraft server");
					} else System.out.println("[INFO] Minecraft server already stopped");
				} else if (input[0].equalsIgnoreCase("list")) {
					System.out.println("STATUS | PLAYER | IP | UUID");
					ClientHandler.getConnected().forEach(clientHandler -> {
						PlayerInfo pi = clientHandler.getPlayerInfo();
						String status;
						if (pi.isConnected()) status = "CONNECTED";
						else if (pi.wasConnected()) status = "WAITING FOR RECONNECTION";
						else status = "WAITING FOR CONNECTION";
						System.out.println(status + " | "
								+ pi.getName() + " | "
								+ pi.getInetAddress().getHostAddress() + " | "
								+ pi.getUuid()
						);
					});
				} else if (input[0].equalsIgnoreCase("help")
						|| input[0].equalsIgnoreCase("?")) {
					System.out.println("Available commands:");
					System.out.println("help|? => shows this page");
					System.out.println("reload|rl => reloads the config");
					System.out.println("stop => stops this server");
					System.out.println("list => lists all active connections");
					System.out.println("mc <start|stop> => starts or stops the minecraft server");
					System.out.println("wup list => lists all wakeup permitted players");
					System.out.println("wup add => adds a new wakeup permitted player");
					System.out.println("wup remove|rm => removes a wakeup permitted player");
				} else if (input[0].equalsIgnoreCase("wup")) {
					if (input.length > 1) {
						if (input[1].equalsIgnoreCase("list")) {
							Server.getConfigHandler().getConfig()
									.getWakeupPermittedPlayers().forEach(System.out::println);
						} else if (input.length > 2) {
							if (PlayerInfo.isUUID(input[1].replaceAll("\n", ""))) {
								input[1] = input[1].replaceAll("\n", "");
								if (input[0].equalsIgnoreCase("add")) {
									UUID uuid = UUID.fromString(input[2]);
									if (Server.getConfigHandler().getConfig().getWakeupPermittedPlayers()
											.contains(uuid)) System.out.println("Already added");
									else {
										Server.getConfigHandler().getConfig().addWakeupPermittedPlayer(uuid);
										System.out.println("[INFO] Added " + input[1] + " as a wakeup permitted player");
									}
								}
								if (input[0].equalsIgnoreCase("remove")
										|| input[0].equalsIgnoreCase("rm")) {
									UUID uuid = UUID.fromString(input[2]);
									if (!Server.getConfigHandler().getConfig().getWakeupPermittedPlayers()
											.contains(uuid)) System.out.println("Wasn't even permitted");
									else {
										Server.getConfigHandler().getConfig().removeWakeupPermittedPlayer(uuid);
										System.out.println("[INFO] Removed " + input[1] + " as a wakeup permitted player");
									}
								}
							} else System.out.println("That's not an uuid!");
						} else System.out.println("You have to specify a uuid!");
					} else {
						System.out.println("wup list => lists all wakeup permitted players");
						System.out.println("wup add => adds a new wakeup permitted player");
						System.out.println("wup remove|rm => removes a wakeup permitted player");
					}
				} else System.out.println("Unknown command! Try \"help\" or \"?\"");
			}
		}

		Server.stop();
	}

}
