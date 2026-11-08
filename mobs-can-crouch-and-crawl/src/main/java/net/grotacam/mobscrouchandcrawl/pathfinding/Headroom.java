package net.grotacam.mobscrouchandcrawl.pathfinding;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Where a mob's feet rest at a path node, used to measure the room above it. Shared by the path
 * search and the posture controller so both agree on what fits.
 */
public final class Headroom {
	private Headroom() {
	}

	public static double floorAt(BlockGetter level, BlockPos pos) {
		double floor = WalkNodeEvaluator.getFloorLevel(level, pos);
		if (floor <= pos.getY() - 1) {
			floor = pos.getY(); // Nothing to stand on below (water, falling): measure from the node itself.
		}

		// Low blocks in the node itself (carpet, snow layers, daylight sensor...) are walked on, not ducked under.
		VoxelShape shape = level.getBlockState(pos).getCollisionShape(level, pos);
		if (!shape.isEmpty()) {
			double top = shape.max(Direction.Axis.Y);
			if (top < 1.0) {
				floor = Math.max(floor, pos.getY() + top);
			}
		}
		return floor;
	}
}
