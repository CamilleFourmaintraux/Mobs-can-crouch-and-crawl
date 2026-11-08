package net.grotacam.mobscrouchandcrawl.posture;

import net.grotacam.mobscrouchandcrawl.MobsCrouchAndCrawl;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import org.jspecify.annotations.Nullable;

/**
 * Shared rules: which mobs can crouch and crawl, and how much each posture shrinks them.
 * Used by the server logic and by the client rendering.
 */
public final class Postures {
	/** Mobs allowed to crouch and crawl. Their renderer must be a humanoid one. */
	public static final TagKey<EntityType<?>> CAN_CROUCH_AND_CRAWL =
		TagKey.create(Registries.ENTITY_TYPE, MobsCrouchAndCrawl.id("can_crouch_and_crawl"));

	/** Crouching height relative to standing: 1.95 -> 1.4625 for a zombie, fits under 1.5 blocks. */
	public static final float CROUCH_HEIGHT_FACTOR = 0.75F;
	/** Crawling height relative to standing: 1.95 -> 0.975 for a zombie, fits under 1 block. */
	public static final float CRAWL_HEIGHT_FACTOR = 0.5F;

	private Postures() {
	}

	public static boolean isCrouchCrawlType(@Nullable EntityType<?> type) {
		return type != null && type.is(CAN_CROUCH_AND_CRAWL);
	}

	/** Babies are already small enough to go through 1-block gaps, so they keep vanilla behaviour. */
	public static boolean canUsePostures(LivingEntity entity) {
		return isCrouchCrawlType(entity.getType()) && !entity.isBaby();
	}

	/** Crawling reuses the player's crawling pose, which is the swimming pose out of water. */
	public static boolean isLowPosture(Pose pose) {
		return pose == Pose.CROUCHING || pose == Pose.SWIMMING;
	}

	public static float heightFactor(Pose pose) {
		return switch (pose) {
			case CROUCHING -> CROUCH_HEIGHT_FACTOR;
			case SWIMMING -> CRAWL_HEIGHT_FACTOR;
			default -> 1.0F;
		};
	}

	/** Orders postures from tallest (0) to lowest (2). */
	public static int lowness(Pose pose) {
		return switch (pose) {
			case CROUCHING -> 1;
			case SWIMMING -> 2;
			default -> 0;
		};
	}

	public static Pose lowest(Pose a, Pose b) {
		return lowness(a) >= lowness(b) ? a : b;
	}
}
