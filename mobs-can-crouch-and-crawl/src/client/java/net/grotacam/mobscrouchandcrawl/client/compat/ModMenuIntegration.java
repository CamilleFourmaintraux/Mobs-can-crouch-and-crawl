package net.grotacam.mobscrouchandcrawl.client.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;

/** Config screen in Mod Menu, only when Cloth Config is installed. */
public class ModMenuIntegration implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		if (FabricLoader.getInstance().isModLoaded("cloth-config")) {
			return ClothConfigScreen::create;
		}
		return parent -> null;
	}
}
