package dev.asteria.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;

public final class AsteriaConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("asteria-client.json");

	private Map<HudModule, Boolean> modules = defaults();
	private int hudX = 6;
	private int hudY = 6;
	private int hudColor = 0xFFFFFF;
	private boolean background = true;

	public static AsteriaConfig load() {
		if (Files.isRegularFile(PATH)) {
			try {
				AsteriaConfig config = GSON.fromJson(Files.readString(PATH), AsteriaConfig.class);
				if (config != null) {
					config.ensureDefaults();
					return config;
				}
			} catch (IOException | RuntimeException ignored) {
			}
		}

		AsteriaConfig config = new AsteriaConfig();
		config.save();
		return config;
	}

	public boolean enabled(HudModule module) {
		return modules.getOrDefault(module, false);
	}

	public void toggle(HudModule module) {
		modules.put(module, !enabled(module));
		save();
	}

	public int hudX() {
		return hudX;
	}

	public int hudY() {
		return hudY;
	}

	public int hudColor() {
		return hudColor;
	}

	public boolean background() {
		return background;
	}

	public void save() {
		try {
			Files.createDirectories(PATH.getParent());
			Files.writeString(PATH, GSON.toJson(this));
		} catch (IOException ignored) {
		}
	}

	private void ensureDefaults() {
		Map<HudModule, Boolean> merged = defaults();
		if (modules != null) {
			merged.putAll(modules);
		}
		modules = merged;
	}

	private static Map<HudModule, Boolean> defaults() {
		Map<HudModule, Boolean> values = new EnumMap<>(HudModule.class);
		for (HudModule module : HudModule.values()) {
			values.put(module, false);
		}
		values.put(HudModule.FPS, true);
		values.put(HudModule.COORDINATES, true);
		values.put(HudModule.PING, true);
		return values;
	}
}