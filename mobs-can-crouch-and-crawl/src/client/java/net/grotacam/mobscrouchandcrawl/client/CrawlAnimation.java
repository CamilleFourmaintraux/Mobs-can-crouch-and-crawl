package net.grotacam.mobscrouchandcrawl.client;

import net.grotacam.mobscrouchandcrawl.posture.Postures;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

public final class CrawlAnimation {
	private CrawlAnimation() {
	}

	/** True while a mob of the tag is crawling or still blending in or out of the crawling pose. */
	public static boolean isCrawling(HumanoidRenderState state) {
		return state.swimAmount > 0.0F && Postures.isCrouchCrawlType(state.entityType);
	}
}
