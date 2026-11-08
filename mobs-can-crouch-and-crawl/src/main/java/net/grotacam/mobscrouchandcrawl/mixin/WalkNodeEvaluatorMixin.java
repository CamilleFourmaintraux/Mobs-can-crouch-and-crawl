package net.grotacam.mobscrouchandcrawl.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.grotacam.mobscrouchandcrawl.pathfinding.Headroom;
import net.grotacam.mobscrouchandcrawl.pathfinding.LowClearancePlanning;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WalkNodeEvaluator.class)
public abstract class WalkNodeEvaluatorMixin {
	@Shadow
	private boolean hasCollisions(AABB aabb) {
		throw new AssertionError();
	}

	@Unique
	private @Nullable LowClearancePlanning mobscrouchandcrawl$planning() {
		return ((LowClearancePlanning.Holder) this).mobscrouchandcrawl$getLowClearancePlanning();
	}

	/** Jumping up a block: the room needed above the take-off spot is that of the lowest posture. */
	@ModifyExpressionValue(
		method = "tryJumpOn",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;getBbHeight()F")
	)
	private float mobscrouchandcrawl$jumpHeadroom(float original) {
		LowClearancePlanning planning = this.mobscrouchandcrawl$planning();
		return planning == null ? original : Math.min(original, planning.lowestHeight());
	}

	/**
	 * Every accepted node goes through here. Measures its headroom: standing is free, crouching or
	 * crawling costs extra (so the mob prefers standing paths), and a node where nothing fits is blocked.
	 */
	@Inject(method = "getNodeAndUpdateCostToMax", at = @At("RETURN"))
	private void mobscrouchandcrawl$applyHeadroom(int x, int y, int z, PathType pathType, float malus, CallbackInfoReturnable<Node> cir) {
		LowClearancePlanning planning = this.mobscrouchandcrawl$planning();
		Node node = cir.getReturnValue();
		if (planning == null || node.costMalus < 0.0F) {
			return;
		}

		double floor = Headroom.floorAt(planning.level(), new BlockPos(x, y, z));
		double centerX = x + 0.5;
		double centerZ = z + 0.5;

		if (this.mobscrouchandcrawl$fits(planning, centerX, floor, centerZ, planning.standingHeight())) {
			return;
		}
		if (this.mobscrouchandcrawl$fits(planning, centerX, floor, centerZ, planning.crouchHeight())) {
			node.costMalus = Math.max(node.costMalus, malus + planning.crouchCost());
			return;
		}
		if (this.mobscrouchandcrawl$fits(planning, centerX, floor, centerZ, planning.crawlHeight())) {
			node.costMalus = Math.max(node.costMalus, malus + planning.crawlCost());
			return;
		}
		node.costMalus = -1.0F;
	}

	/** Same margins as vanilla's own headroom check in tryJumpOn; collision results are cached per path search. */
	@Unique
	private boolean mobscrouchandcrawl$fits(LowClearancePlanning planning, double centerX, double floor, double centerZ, float height) {
		if (height <= 0.0F) {
			return false; // Posture disabled.
		}
		double halfWidth = planning.halfWidth();
		return !this.hasCollisions(new AABB(
			centerX - halfWidth, floor + 0.001, centerZ - halfWidth,
			centerX + halfWidth, floor + height - 0.002, centerZ + halfWidth
		));
	}
}
