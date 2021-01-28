package io.github.paexception.mcsws.server.config;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public class Config {

	private final String remoteHost;
	private final int remotePort;
	private final int port;
	private final int maxJoinDelay;
	private final int maxRejoinDelay;
	private final Set<UUID> wakeupPermittedPlayers;

	public Config(String remoteHost, int remotePort, int port, int maxJoinDelay, int maxRejoinDelay, Set<UUID> wakeupPermittedPlayers) {
		this.remoteHost = remoteHost;
		this.remotePort = remotePort;
		this.port = port;
		this.maxJoinDelay = maxJoinDelay;
		this.maxRejoinDelay = maxRejoinDelay;
		this.wakeupPermittedPlayers = wakeupPermittedPlayers;
	}

	public static Config getDefault() {
		return new Config(
				"localhost",
				25566,
				25555,
				300,
				120,
				Collections.singleton(UUID.fromString("34644296-5aba-4444-b468-c8f34711fbab"))
		);
	}

	public String getRemoteHost() {
		return this.remoteHost;
	}

	public int getRemotePort() {
		return this.remotePort;
	}

	public int getPort() {
		return this.port;
	}

	public int getMaxJoinDelay() {
		return this.maxJoinDelay;
	}

	public int getMaxRejoinDelay() {
		return this.maxRejoinDelay;
	}

	public Set<UUID> getWakeupPermittedPlayers() {
		return Collections.unmodifiableSet(this.wakeupPermittedPlayers);
	}

	public void addWakeupPermittedPlayer(UUID uuid) {
		this.wakeupPermittedPlayers.add(uuid);
	}

	public void removeWakeupPermittedPlayer(UUID uuid) {
		this.wakeupPermittedPlayers.remove(uuid);
	}

}
