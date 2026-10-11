package shaw.mousepillars.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;

import shaw.mousepillars.logic.MouseForm;
import shaw.mousepillars.logic.MouseState;

@Mixin(Player.class)
public abstract class PlayerMixin {
	@Unique
	private boolean mousepillars$dimensionsAppliedAsMouse;

	@Inject(method = "tick", at = @At("TAIL"))
	private void mousepillars$refreshDimensionsOnChange(CallbackInfo ci) {
		Player self = (Player) (Object) this;
		boolean isMouse = MouseState.isMouse(self);
		if (isMouse != this.mousepillars$dimensionsAppliedAsMouse) {
			this.mousepillars$dimensionsAppliedAsMouse = isMouse;
			self.refreshDimensions();
		}
	}
}
