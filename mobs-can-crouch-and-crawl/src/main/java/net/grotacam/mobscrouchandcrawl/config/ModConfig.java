package net.grotacam.mobscrouchandcrawl.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;
import net.grotacam.mobscrouchandcrawl.MobsCrouchAndCrawl;

/**
 * Server-side settings, stored in config/mobscrouchandcrawl.json.
 */
public final class ModConfig {
	public static final double MIN_SPEED_MULTIPLIER = 0.05;
	public static final double MAX_SPEED_MULTIPLIER = 1.0;
	public static final double DEFAULT_SPEED_MULTIPLIER = 0.3;

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final String FILE_NAME = MobsCrouchAndCrawl.MOD_ID + ".json";
	private static ModConfig instance = new ModConfig();

	public boolean enableCrouching = true;
	public boolean enableCrawling = true;
	public double crouchSpeedMultiplier = DEFAULT_SPEED_MULTIPLIER;
	public double crawlSpeedMultiplier = DEFAULT_SPEED_MULTIPLIER;

	public static ModConfig get() {
		return instance;
	}

	public static void load() {
		Path file = path();
		ModConfig loaded = null;
		if (Files.exists(file)) {
			try (Reader reader = Files.newBufferedReader(file)) {
				loaded = GSON.fromJson(reader, ModConfig.class);
			} catch (IOException | JsonParseException e) {
				MobsCrouchAndCrawl.LOGGER.error("Could not read {}, using default settings", file, e);
			}
		}

		instance = loaded != null ? loaded : new ModConfig();
		instance.validate();
		save();
	}

	public static void save() {
		Path file = path();
		try {
			Files.createDirectories(file.getParent());
			try (Writer writer = Files.newBufferedWriter(file)) {
				GSON.toJson(instance, writer);
			}
		} catch (IOException e) {
			MobsCrouchAndCrawl.LOGGER.error("Could not write {}", file, e);
		}
	}

	public boolean anyPostureEnabled() {
		return this.enableCrouching || this.enableCrawling;
	}

	private void validate() {
		this.crouchSpeedMultiplier = clampSpeed(this.crouchSpeedMultiplier);
		this.crawlSpeedMultiplier = clampSpeed(this.crawlSpeedMultiplier);
	}

	private static double clampSpeed(double value) {
		if (Double.isNaN(value)) {
			return DEFAULT_SPEED_MULTIPLIER;
		}
		return Math.max(MIN_SPEED_MULTIPLIER, Math.min(MAX_SPEED_MULTIPLIER, value));
	}

	private static Path path() {
		return FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
	}
}
