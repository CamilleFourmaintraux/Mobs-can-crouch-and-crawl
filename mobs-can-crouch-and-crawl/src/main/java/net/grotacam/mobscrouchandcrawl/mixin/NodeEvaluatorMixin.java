package net.grotacam.mobscrouchandcrawl.mixin;

import net.grotacam.mobscrouchandcrawl.pathfinding.LowClearancePlanning;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NodeEvaluator.class)
public abstract class NodeEvaluatorMixin implements LowClearancePlanning.Holder {
	@Shadow
	protected int entityHeight;

	@Unique
	private @Nullable LowClearancePlanning mobscrouchandcrawl$lowClearancePlanning;

	@Override
	public @Nullable LowClearancePlanning mobscrouchandcrawl$getLowClearancePlanning() {
		return this.mobscrouchandcrawl$lowClearancePlanning;
	}

	@Inject(method = "prepare", at = @At("TAIL"))
	private void mobscrouchandcrawl$prepareLowClearance(PathNavigationRegion region, Mob mob, CallbackInfo ci) {
		this.mobscrouchandcrawl$lowClearancePlanning = (Object) this instanceof WalkNodeEvaluator
			? LowClearancePlanning.create(region, mob)
			: null;
		if (this.mobscrouchandcrawl$lowClearancePlanning != null) {
			// Only the feet block decides if a node is passable; the headroom is measured per node instead.
			this.entityHeight = 1;
		}
	}

	@Inject(method = "done", at = @At("TAIL"))
	private void mobscrouchandcrawl$clearLowClearance(CallbackInfo ci) {
		this.mobscrouchandcrawl$lowClearancePlanning = null;
	}
}
