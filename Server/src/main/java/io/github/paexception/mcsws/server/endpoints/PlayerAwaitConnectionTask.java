package io.github.paexception.mcsws.server.endpoints;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.TimerTask;
import java.util.UUID;

public class PlayerAwaitConnectionTask extends TimerTask {

	private final ClientHandler clientHandler;
	private final int timout;

	public PlayerAwaitConnectionTask(ClientHandler clientHandler, int timout) {
		this.clientHandler = clientHandler;
		this.timout = timout;
	}

	@Override
	public void run() {
		if (this.clientHandler != null) {
			try {
				this.clientHandler.getSocket().write("player_not_connected");
			} catch (IOException | GeneralSecurityException ignored) {
			}
			this.clientHandler.disconnect();
		}
	}

	public int getTimout() {
		return this.timout;
	}

	public UUID getUUID() {
		return this.clientHandler.getPlayerInfo().getUuid();
	}

}
