package io.github.paexception.mcsws.server.util;

import java.net.InetAddress;
import java.util.UUID;

public class PlayerInfo {

	private final UUID uuid;
	private final InetAddress inetAddress;
	private String name;
	private boolean isConnected;
	private boolean wasConnected;

	public PlayerInfo(UUID uuid, InetAddress inetAddress) {
		this.uuid = uuid;
		this.inetAddress = inetAddress;
	}

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

	public void connects() {
		this.isConnected = true;
		this.wasConnected = true;
	}

	public void disconnects() {
		this.isConnected = false;
	}

	public boolean wasConnected() {
		return this.wasConnected;
	}

	public UUID getUuid() {
		return this.uuid;
	}

	public InetAddress getInetAddress() {
		return this.inetAddress;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isConnected() {
		return this.isConnected;
	}

}
