package net.grotacam.mobscrouchandcrawl.pathfinding;

import net.grotacam.mobscrouchandcrawl.config.ModConfig;
import net.grotacam.mobscrouchandcrawl.posture.Postures;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.BlockGetter;
import org.jspecify.annotations.Nullable;

/**
 * Settings of one pathfinding run in which the mob may plan through low gaps.
 *
 * <p>The node evaluator then only checks the block at the mob's feet (as for a 1-block-tall mob) and
 * measures the real headroom of every node: nodes where only crouching or crawling fits cost more,
 * so the mob prefers a path where it can stay standing.
 *
 * @param crouchHeight 0 when crouching is disabled
 * @param crawlHeight  0 when crawling is disabled
 */
public record LowClearancePlanning(
	BlockGetter level,
	float halfWidth,
	float standingHeight,
	float crouchHeight,
	float crawlHeight,
	float lowestHeight,
	float crouchCost,
	float crawlCost
) {
	/** Only while chasing a target, or while already crouching or crawling (to find the way out). */
	public static @Nullable LowClearancePlanning create(BlockGetter level, Mob mob) {
		ModConfig config = ModConfig.get();
		if (!config.anyPostureEnabled() || !Postures.canUsePostures(mob)) {
			return null;
		}
		if (mob.getTarget() == null && !Postures.isLowPosture(mob.getPose())) {
			return null;
		}

		EntityDimensions standing = mob.getDimensions(Pose.STANDING);
		float crouchHeight = config.enableCrouching ? mob.getDimensions(Pose.CROUCHING).height() : 0.0F;
		float crawlHeight = config.enableCrawling ? mob.getDimensions(Pose.SWIMMING).height() : 0.0F;
		return new LowClearancePlanning(
			level,
			standing.width() / 2.0F,
			standing.height(),
			crouchHeight,
			crawlHeight,
			config.enableCrawling ? crawlHeight : crouchHeight,
			extraCost(config.crouchSpeedMultiplier),
			extraCost(config.crawlSpeedMultiplier)
		);
	}

	/** A node crossed at 30% speed takes about 3.3 times longer: 2.3 extra, on top of the normal cost of 1. */
	private static float extraCost(double speedMultiplier) {
		return (float) (1.0 / speedMultiplier - 1.0);
	}

	/** Duck interface implemented by {@code NodeEvaluator} through a mixin. */
	public interface Holder {
		@Nullable LowClearancePlanning mobscrouchandcrawl$getLowClearancePlanning();
	}
}
