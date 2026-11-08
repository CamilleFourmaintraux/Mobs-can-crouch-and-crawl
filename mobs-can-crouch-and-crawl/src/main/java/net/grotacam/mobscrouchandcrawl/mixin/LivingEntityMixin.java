package net.grotacam.mobscrouchandcrawl.mixin;

import net.grotacam.mobscrouchandcrawl.posture.Postures;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Smaller hitbox and lower eyes while crouching or crawling. Runs on both sides: the client gets the
 * same hitbox as the server when the mod is installed there too.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	@Inject(method = "getDimensions", at = @At("RETURN"), cancellable = true)
	private void mobscrouchandcrawl$lowPostureDimensions(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
		if (!Postures.isLowPosture(pose) || !Postures.canUsePostures((LivingEntity) (Object) this)) {
			return;
		}

		// Applied after the scale attribute, so scaled zombies keep the same proportions.
		cir.setReturnValue(cir.getReturnValue().scale(1.0F, Postures.heightFactor(pose)));
	}
}
