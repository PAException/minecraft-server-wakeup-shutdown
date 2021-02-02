package io.github.paexception.mcsws.plugin;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MCSWS extends JavaPlugin implements Listener {

	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private final ServerConnection serverConnection = new ServerConnection(this);

	public static boolean isUUID(String name) {
		if (name.length() > 36) return false;

		int dash1 = name.indexOf('-');
		int dash2 = name.indexOf('-', dash1 + 1);
		int dash3 = name.indexOf('-', dash2 + 1);
		int dash4 = name.indexOf('-', dash3 + 1);
		int dash5 = name.indexOf('-', dash4 + 1);

		// For any valid input, dash1 through dash4 will be positive and dash5
		// negative, but it's enough to check dash4 and dash5:
		// - if dash1 is -1, dash4 will be -1
		// - if dash1 is positive but dash2 is -1, dash4 will be -1
		// - if dash1 and dash2 is positive, dash3 will be -1, dash4 will be
		//   positive, but so will dash5
		return dash4 >= 0 && dash5 < 0;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if ((command.getName().equalsIgnoreCase("wakeup")
				|| command.getName().equalsIgnoreCase("wu")
				|| command.getName().equalsIgnoreCase("wup")) && sender.isOp())
			if (args.length > 0)
				if (args[0].equalsIgnoreCase("list")) {
					String uuids = this.serverConnection.listWakeupPermittedPlayers();
					sender.sendMessage("§bWakeup permitted players:");
					for (String s : uuids.split("\\.")) {
						if (isUUID(s)) {
							UUID uuid = UUID.fromString(s);
							sender.sendMessage("§f" + this.getServer()
									.getOfflinePlayer(uuid).getName() + " §7(" + uuid + ")");
						}
					}
					return true;
				} else if (args.length > 1)
					if (args[0].equalsIgnoreCase("add")) {
						if (isUUID(args[1])) {
							OfflinePlayer op = this.getServer().getOfflinePlayer(UUID.fromString(args[1]));
							if (op != null) {
								this.serverConnection.addWakeupPermittedPlayer(op.getName(), op.getUniqueId());
								sender.sendMessage("§aAdded §b" + op.getName() + " §aas a wakeup permitted player...");
							} else sender.sendMessage("§cCouldn't find player with the specific UUID."
									+ "Maybe the player hasn't played before?");
							return true;
						} else {
							Player player = this.getServer().getPlayer(args[1]);
							if (player != null) {
								this.serverConnection.addWakeupPermittedPlayer(player.getName(), player.getUniqueId());
								sender.sendMessage("§aAdded §b" + player.getName()
										+ " §aas a wakeup permitted player...");
							} else sender.sendMessage("§cCouldn't find player. Is he/she online?");
							return true;
						}
					} else if (args[0].equalsIgnoreCase("rm")
							|| args[0].equalsIgnoreCase("remove")) {
						if (isUUID(args[1])) {
							OfflinePlayer op = this.getServer().getOfflinePlayer(UUID.fromString(args[1]));
							if (op != null) {
								this.serverConnection.removeWakeupPermittedPlayer(op.getName(), op.getUniqueId());
								sender.sendMessage("§aRemoved §b" + op.getName() + " §aas a wakeup permitted player...");
							} else sender.sendMessage("§cCouldn't find player with the specific UUID."
									+ "Maybe the player hasn't played before?");
							return true;
						} else {
							Player player = this.getServer().getPlayer(args[1]);
							if (player != null) {
								this.serverConnection.removeWakeupPermittedPlayer(player.getName(), player.getUniqueId());
								sender.sendMessage("§aRemoved §b" + player.getName()
										+ " §aas a wakeup permitted player...");
							} else sender.sendMessage("§cCouldn't find player. Is he/she online?");
							return true;
						}
					} else sender.sendMessage("§cYou don't have the permission to perform this command!");

		return false;
	}

	@Override
	public void onDisable() {
		this.serverConnection.stopServer();
	}

	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(this, this);
		this.executor.submit(this.serverConnection);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		try {
			this.serverConnection.write("join."
					+ event.getPlayer().getName() + "." + event.getPlayer().getUniqueId());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void shutdown() {
		this.getServer().getWorlds().forEach(World::save);
		this.getServer().savePlayers();
		this.getServer().shutdown();
		System.exit(0);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		try {
			this.serverConnection.write("quit."
					+ event.getPlayer().getName() + "." + event.getPlayer().getUniqueId());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
