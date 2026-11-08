package net.grotacam.mobscrouchandcrawl.posture;

import net.grotacam.mobscrouchandcrawl.MobsCrouchAndCrawl;
import net.grotacam.mobscrouchandcrawl.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.grotacam.mobscrouchandcrawl.pathfinding.Headroom;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * Server-side posture logic, run at the start of every tick of a mob that can crouch and crawl.
 *
 * <p>The mob takes the tallest posture that fits both where it stands and on the next nodes of its
 * path, so it gets down before entering a low gap and stands up once it is out. A standing mob with
 * no path costs nothing; a standing mob following a path does one collision check per new node.
 */
public final class PostureController {
	private static final Identifier CROUCH_SPEED_ID = MobsCrouchAndCrawl.id("crouching_speed");
	private static final Identifier CRAWL_SPEED_ID = MobsCrouchAndCrawl.id("crawling_speed");
	/** Number of upcoming path nodes checked, so the mob gets down a little before the gap. */
	private static final int LOOK_AHEAD_NODES = 2;
	/** The look-ahead is refreshed at least this often, in case blocks changed along the path. */
	private static final int LOOK_AHEAD_REFRESH_TICKS = 10;
	private static final double FIT_EPSILON = 1.0E-7;

	private PostureController() {
	}

	public static void tick(Mob mob) {
		if (mob.level().isClientSide() || !Postures.isCrouchCrawlType(mob.getType())) {
			return;
		}

		Pose current = mob.getPose();
		if (current != Pose.STANDING && !Postures.isLowPosture(current)) {
			return; // Dying, sleeping...: not ours to change.
		}

		ModConfig config = ModConfig.get();
		PostureMemory memory = ((PostureMemory.Holder) mob).mobscrouchandcrawl$getPostureMemory();
		if (!config.anyPostureEnabled() || !mob.isAlive() || mob.isBaby() || mob.isPassenger() || mob.noPhysics) {
			if (current != Pose.STANDING && fits(mob, Pose.STANDING, mob.position())) {
				apply(mob, Pose.STANDING, config);
			}
			return;
		}

		Path path = mob.getNavigation().getPath();
		boolean followingPath = path != null && !path.isDone();
		if (current == Pose.STANDING && !followingPath && memory.initialized) {
			return;
		}

		// A standing mob already fits where it is (vanilla collisions keep it there), except right after loading.
		Pose allowed = current == Pose.STANDING && memory.initialized
			? Pose.STANDING
			: tallestFitting(mob, mob.position(), config, true);
		memory.initialized = true;

		if (followingPath) {
			Pose ahead = lookAhead(mob, path, memory, config);
			if (ahead != null) {
				allowed = Postures.lowest(allowed, ahead);
			}
		}

		if (allowed != current) {
			apply(mob, allowed, config);
		}
	}

	private static @Nullable Pose lookAhead(Mob mob, Path path, PostureMemory memory, ModConfig config) {
		int index = path.getNextNodeIndex();
		if (memory.lookAheadPath == path
			&& memory.lookAheadIndex == index
			&& mob.tickCount - memory.lookAheadTick < LOOK_AHEAD_REFRESH_TICKS) {
			return memory.lookAheadPose;
		}

		Pose result = Pose.STANDING;
		int end = Math.min(path.getNodeCount(), index + LOOK_AHEAD_NODES);
		for (int i = index; i < end; i++) {
			Pose fitting = tallestFitting(mob, nodePosition(mob, path.getNodePos(i)), config, false);
			if (fitting == null) {
				break; // No posture fits there: leave it to vanilla navigation.
			}
			result = Postures.lowest(result, fitting);
		}

		memory.lookAheadPath = path;
		memory.lookAheadIndex = index;
		memory.lookAheadTick = mob.tickCount;
		memory.lookAheadPose = result;
		return result;
	}

	/** Where the mob's feet will be at a path node: centered, on the floor of that block. */
	private static Vec3 nodePosition(Mob mob, BlockPos pos) {
		return new Vec3(pos.getX() + 0.5, Headroom.floorAt(mob.level(), pos), pos.getZ() + 0.5);
	}

	/**
	 * Tallest enabled posture whose hitbox fits at the given position. When none fits, returns the lowest
	 * enabled posture if {@code fallbackToLowest}, otherwise null.
	 */
	private static @Nullable Pose tallestFitting(Mob mob, Vec3 position, ModConfig config, boolean fallbackToLowest) {
		if (fits(mob, Pose.STANDING, position)) {
			return Pose.STANDING;
		}
		if (config.enableCrouching && fits(mob, Pose.CROUCHING, position)) {
			return Pose.CROUCHING;
		}
		if (config.enableCrawling && fits(mob, Pose.SWIMMING, position)) {
			return Pose.SWIMMING;
		}
		if (!fallbackToLowest) {
			return null;
		}
		return config.enableCrawling ? Pose.SWIMMING : Pose.CROUCHING;
	}

	private static boolean fits(Mob mob, Pose pose, Vec3 position) {
		return mob.level().noCollision(mob, mob.getDimensions(pose).makeBoundingBox(position).deflate(FIT_EPSILON));
	}

	private static void apply(Mob mob, Pose pose, ModConfig config) {
		mob.setPose(pose); // Also refreshes the hitbox and is synced to clients.

		AttributeInstance speed = mob.getAttribute(Attributes.MOVEMENT_SPEED);
		if (speed == null) {
			return;
		}

		speed.removeModifier(CROUCH_SPEED_ID);
		speed.removeModifier(CRAWL_SPEED_ID);
		if (pose == Pose.CROUCHING) {
			addSpeedModifier(speed, CROUCH_SPEED_ID, config.crouchSpeedMultiplier);
		} else if (pose == Pose.SWIMMING) {
			addSpeedModifier(speed, CRAWL_SPEED_ID, config.crawlSpeedMultiplier);
		}
	}

	private static void addSpeedModifier(AttributeInstance speed, Identifier id, double multiplier) {
		if (multiplier != 1.0) {
			// Transient: never saved, so nothing is left behind if the mod is removed.
			speed.addTransientModifier(new AttributeModifier(id, multiplier - 1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
		}
	}
}
