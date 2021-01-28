package io.github.paexception.mcsws.plugin;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class MCSWS extends JavaPlugin implements Listener {

	private final Server server = new Server(this);

	public static void main(String[] args) {

	}

	@Override
	public void onDisable() {

	}

	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(this, this);
		server.start();
	}



}
