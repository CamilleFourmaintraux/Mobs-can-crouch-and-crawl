package net.grotacam.mobscrouchandcrawl.client.mixin;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import net.grotacam.mobscrouchandcrawl.client.CrawlAnimation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.monster.zombie.AbstractZombieModel;
import net.minecraft.client.renderer.entity.state.UndeadRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Zombies and husks: keeps the player's swimming arm stroke while crawling. */
@Mixin(AbstractZombieModel.class)
public abstract class AbstractZombieModelMixin {
	@WrapWithCondition(
		method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/ZombieRenderState;)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/model/AnimationUtils;animateZombieArms(Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;ZLnet/minecraft/client/renderer/entity/state/UndeadRenderState;)V"
		)
	)
	private boolean mobscrouchandcrawl$keepSwimmingArms(ModelPart leftArm, ModelPart rightArm, boolean aggressive, UndeadRenderState state) {
		return !CrawlAnimation.isCrawling(state);
	}
}
