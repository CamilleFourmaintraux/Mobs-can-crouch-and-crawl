package net.grotacam.mobscrouchandcrawl;

import net.fabricmc.api.ModInitializer;
import net.grotacam.mobscrouchandcrawl.config.ModConfig;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MobsCrouchAndCrawl implements ModInitializer {
	public static final String MOD_ID = "mobscrouchandcrawl";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModConfig.load();
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
