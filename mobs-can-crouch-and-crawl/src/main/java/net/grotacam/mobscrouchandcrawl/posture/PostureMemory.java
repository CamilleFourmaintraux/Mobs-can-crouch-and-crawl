package net.grotacam.mobscrouchandcrawl.posture;

import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.pathfinder.Path;
import org.jspecify.annotations.Nullable;

/**
 * Per-mob state of the posture controller. Only created for mobs that can crouch and crawl.
 */
public final class PostureMemory {
	/** False until the first full check, so a mob loaded inside a low gap gets down before suffocating. */
	boolean initialized;

	/** Cached look-ahead result, valid for one path and one next-node index. */
	@Nullable Path lookAheadPath;
	int lookAheadIndex = -1;
	int lookAheadTick;
	@Nullable Pose lookAheadPose;

	/** Duck interface implemented by {@code Mob} through a mixin. */
	public interface Holder {
		PostureMemory mobscrouchandcrawl$getPostureMemory();
	}
}
