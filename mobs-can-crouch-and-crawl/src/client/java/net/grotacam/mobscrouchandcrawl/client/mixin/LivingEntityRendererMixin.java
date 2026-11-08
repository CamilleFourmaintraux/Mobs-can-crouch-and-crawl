package net.grotacam.mobscrouchandcrawl.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.grotacam.mobscrouchandcrawl.posture.Postures;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Lays crawling mobs down. Vanilla only does this in the player renderer, so this copies its crawling
 * rotation for the mobs of the tag.
 */
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {
	@Inject(method = "setupRotations", at = @At("TAIL"))
	private void mobscrouchandcrawl$crawlRotation(LivingEntityRenderState state, PoseStack poseStack, float bodyRot, float scale, CallbackInfo ci) {
		if (!(state instanceof HumanoidRenderState humanoid) || humanoid.isFallFlying || !Postures.isCrouchCrawlType(state.entityType)) {
			return;
		}

		float swimAmount = humanoid.swimAmount;
		if (swimAmount <= 0.0F) {
			return;
		}

		// Same as the player renderer: lying flat on land, following the head pitch in water.
		float target = humanoid.isInWater ? -90.0F - humanoid.xRot : -90.0F;
		poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(swimAmount, 0.0F, target)));
		if (humanoid.isVisuallySwimming) {
			poseStack.translate(0.0F, -1.0F, 0.3F);
		}
	}
}
