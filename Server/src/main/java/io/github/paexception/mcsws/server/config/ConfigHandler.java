package io.github.paexception.mcsws.server.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConfigHandler {

	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private static final File file = new File("config.json");
	private Config config;

	public void loadConfig() {
		try {
			if (file.exists() && !file.isDirectory())
					this.config = gson.fromJson(new InputStreamReader(new FileInputStream(file)), Config.class);
			else {
				file.createNewFile();
				this.config = Config.getDefault();
				this.storeConfig();
			}
			if (this.config == null) this.config = Config.getDefault();
			System.out.println("[INFO] Loaded config");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("[ERROR] Something went wrong creating the config file. Going with default settings");
			this.config = Config.getDefault();
			e.printStackTrace();
		}
	}

	public void storeConfig() {
		try {
			if (!file.exists() || file.isDirectory()) file.createNewFile();
			String json = gson.toJson(this.config);
			json+="\n";
			FileOutputStream out = new FileOutputStream(file);
			out.write(json.getBytes());
			out.close();
			System.out.println("[INFO] Saved config");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("[ERROR] Something went wrong saving the config file");
			e.printStackTrace();
		}
	}

	public Config getConfig() {
		return this.config;
	}

}
