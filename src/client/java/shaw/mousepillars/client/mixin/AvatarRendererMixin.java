package shaw.mousepillars.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;

import shaw.mousepillars.client.render.MouseAppearances;
import shaw.mousepillars.logic.MouseState;

@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererMixin<AvatarlikeEntity extends Avatar & ClientAvatarEntity> {
	@Inject(
			method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V",
			at = @At("RETURN")
	)
	private void mousepillars$applyMouseAppearance(AvatarlikeEntity entity, AvatarRenderState state, float partialTicks, CallbackInfo ci) {
		if (MouseState.isMouse(entity)) {
			MouseAppearances.get().apply(state);
		}
	}
}
