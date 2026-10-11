package shaw.mousepillars.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

import shaw.mousepillars.logic.MouseState;

@Mixin(Inventory.class)
public abstract class InventoryMixin {
	@Shadow
	@Final
	private Player player;

	@Inject(method = "setSelectedSlot", at = @At("HEAD"), cancellable = true)
	private void mousepillars$lockHotbar(int slot, CallbackInfo ci) {
		if (MouseState.isMouse(this.player)) {
			ci.cancel();
		}
	}
}
