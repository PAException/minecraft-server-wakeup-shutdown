package io.github.paexception.mcsws.server.config;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public class Config {

	private final String remoteMac;
	private final String remoteHost;
	private final int remotePort;
	private final String subnetmask;
	private final int wakeOnLanPackageCount;
	private final int port;
	private final int maxJoinDelay;
	private final int maxRejoinDelay;
	private final Set<UUID> wakeupPermittedPlayers;

	public Config(
			String remoteMac,
			String remoteHost,
			int remotePort,
			String subnetmask,
			int wakeOnLanPackageCount,
			int port,
			int maxJoinDelay,
			int maxRejoinDelay,
			Set<UUID> wakeupPermittedPlayers
	) {
		this.remoteMac = remoteMac;
		this.remoteHost = remoteHost;
		this.remotePort = remotePort;
		this.subnetmask = subnetmask;
		this.wakeOnLanPackageCount = wakeOnLanPackageCount;
		this.port = port;
		this.maxJoinDelay = maxJoinDelay;
		this.maxRejoinDelay = maxRejoinDelay;
		this.wakeupPermittedPlayers = wakeupPermittedPlayers;
	}

	public static byte[] convertMac(String mac) {
		mac = mac.replaceAll("\\.", "").replaceAll("-", "").replaceAll(":", "");
		if (mac.length() != 12) throw new IllegalArgumentException("Invalid Mac: " + mac);
		byte[] bytes = new byte[6];
		for (int i = 0; i < bytes.length; i++)
			bytes[i] = (byte) ((Integer.parseInt(String.valueOf(mac.charAt(i * 2)), 16) * 16)
					+ (Integer.parseInt(String.valueOf(mac.charAt((i * 2) + 1)), 16)));

		return bytes;
	}

	public void addWakeupPermittedPlayer(UUID uuid) {
		this.wakeupPermittedPlayers.add(uuid);
	}

	public void removeWakeupPermittedPlayer(UUID uuid) {
		this.wakeupPermittedPlayers.remove(uuid);
	}

	public static Config getDefault() {
		return new Config(
				"<mac>",
				"localhost",
				25566,
				"255.255.255.255",
				5,
				25566,
				300,
				120,
				Collections.singleton(UUID.fromString("34644296-5aba-4444-b468-c8f34711fbab"))
		);
	}

	public String getRemoteMac() {
		return this.remoteMac;
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

	public String getSubnetmask() {
		return this.subnetmask;
	}

	public int getWakeOnLanPackageCount() {
		return this.wakeOnLanPackageCount;
	}

}
