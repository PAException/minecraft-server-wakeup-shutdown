package io.github.paexception.mcsws.server.util;

import io.github.paexception.mcsws.server.Server;
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
				else if (input[0].equalsIgnoreCase("reload") || input[0].equalsIgnoreCase("rl")) {
					Server.getConfigHandler().loadConfig();
					System.out.println("[INFO] Reloaded config! (Networking changes apply by restarting the server)");
				} else if (input[0].equalsIgnoreCase("startmc")) {
					if (Server.getMinecraftServerConnection() == null) {
						Server.startMinecraftServer();
						System.out.println("[INFO] Triggered minecraft server to start");
					} else System.out.println("[INFO] Minecraft server already running or starting");
				} else if (input.length > 1 && PlayerInfo.isUUID(input[1].replaceAll("\n", ""))) {
					input[1] = input[1].replaceAll("\n", "");
					if (input[0].equalsIgnoreCase("add")) {
						Server.getConfigHandler().getConfig().addWakeupPermittedPlayer(UUID.fromString(input[1]));
						System.out.println("[INFO] Added " + input[1] + " as a wakeup permitted player");
					}
					if (input[0].equalsIgnoreCase("remove") || input[0].equalsIgnoreCase("rm")) {
						Server.getConfigHandler().getConfig().removeWakeupPermittedPlayer(UUID.fromString(input[1]));
						System.out.println("[INFO] Removed " + input[1] + " as a wakeup permitted player");
					}
				}
			}
		}

		Server.stop();
	}

}
