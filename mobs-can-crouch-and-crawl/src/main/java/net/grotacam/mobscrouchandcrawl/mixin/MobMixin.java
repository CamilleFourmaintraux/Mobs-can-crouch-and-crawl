package net.grotacam.mobscrouchandcrawl.mixin;

import net.grotacam.mobscrouchandcrawl.posture.PostureController;
import net.grotacam.mobscrouchandcrawl.posture.PostureMemory;
import net.minecraft.world.entity.Mob;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MobMixin implements PostureMemory.Holder {
	@Unique
	private @Nullable PostureMemory mobscrouchandcrawl$postureMemory;

	@Override
	public PostureMemory mobscrouchandcrawl$getPostureMemory() {
		if (this.mobscrouchandcrawl$postureMemory == null) {
			this.mobscrouchandcrawl$postureMemory = new PostureMemory();
		}
		return this.mobscrouchandcrawl$postureMemory;
	}

	/**
	 * Start of the tick: before the suffocation check of the base tick and before the AI moves the mob.
	 * Kept out of the goal system so AI-throttling mods cannot leave a mob stuck at a gap entrance.
	 */
	@Inject(method = "tick", at = @At("HEAD"))
	private void mobscrouchandcrawl$updatePosture(CallbackInfo ci) {
		PostureController.tick((Mob) (Object) this);
	}
}
