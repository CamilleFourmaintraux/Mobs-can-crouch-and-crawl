package net.grotacam.mobscrouchandcrawl.client.compat;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.grotacam.mobscrouchandcrawl.config.ModConfig;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Only loaded when Cloth Config is installed. */
public final class ClothConfigScreen {
	private static final String PREFIX = "config.mobscrouchandcrawl.";

	private ClothConfigScreen() {
	}

	public static Screen create(Screen parent) {
		ModConfig config = ModConfig.get();
		ConfigBuilder builder = ConfigBuilder.create()
			.setParentScreen(parent)
			.setTitle(Component.translatable(PREFIX + "title"))
			.setSavingRunnable(ModConfig::save);
		ConfigEntryBuilder entries = builder.entryBuilder();
		ConfigCategory general = builder.getOrCreateCategory(Component.translatable(PREFIX + "category.general"));

		general.addEntry(entries.startTextDescription(Component.translatable(PREFIX + "server_note")).build());

		general.addEntry(entries.startBooleanToggle(Component.translatable(PREFIX + "enable_crouching"), config.enableCrouching)
			.setDefaultValue(true)
			.setTooltip(Component.translatable(PREFIX + "enable_crouching.tooltip"))
			.setSaveConsumer(value -> config.enableCrouching = value)
			.build());

		general.addEntry(entries.startBooleanToggle(Component.translatable(PREFIX + "enable_crawling"), config.enableCrawling)
			.setDefaultValue(true)
			.setTooltip(Component.translatable(PREFIX + "enable_crawling.tooltip"))
			.setSaveConsumer(value -> config.enableCrawling = value)
			.build());

		general.addEntry(entries.startDoubleField(Component.translatable(PREFIX + "crouch_speed"), config.crouchSpeedMultiplier)
			.setDefaultValue(ModConfig.DEFAULT_SPEED_MULTIPLIER)
			.setMin(ModConfig.MIN_SPEED_MULTIPLIER)
			.setMax(ModConfig.MAX_SPEED_MULTIPLIER)
			.setTooltip(Component.translatable(PREFIX + "crouch_speed.tooltip"))
			.setSaveConsumer(value -> config.crouchSpeedMultiplier = value)
			.build());

		general.addEntry(entries.startDoubleField(Component.translatable(PREFIX + "crawl_speed"), config.crawlSpeedMultiplier)
			.setDefaultValue(ModConfig.DEFAULT_SPEED_MULTIPLIER)
			.setMin(ModConfig.MIN_SPEED_MULTIPLIER)
			.setMax(ModConfig.MAX_SPEED_MULTIPLIER)
			.setTooltip(Component.translatable(PREFIX + "crawl_speed.tooltip"))
			.setSaveConsumer(value -> config.crawlSpeedMultiplier = value)
			.build());

		return builder.build();
	}
}
