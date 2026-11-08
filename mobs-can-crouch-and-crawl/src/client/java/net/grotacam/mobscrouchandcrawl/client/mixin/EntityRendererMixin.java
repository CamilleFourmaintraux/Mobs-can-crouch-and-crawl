package net.grotacam.mobscrouchandcrawl.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.grotacam.mobscrouchandcrawl.posture.Postures;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Lowers crouching mobs by 2 pixels, like the player renderer does for a sneaking player.
 */
@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {
	@ModifyReturnValue(method = "getRenderOffset", at = @At("RETURN"))
	private Vec3 mobscrouchandcrawl$crouchOffset(Vec3 original, @Local(argsOnly = true) EntityRenderState state) {
		if (state instanceof HumanoidRenderState humanoid && humanoid.isCrouching && Postures.isCrouchCrawlType(state.entityType)) {
			return original.add(0.0, humanoid.scale * -2.0F / 16.0, 0.0);
		}
		return original;
	}
}
