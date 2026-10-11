package shaw.mousepillars.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;

import shaw.mousepillars.logic.MouseForm;
import shaw.mousepillars.logic.MouseState;

@Mixin(Avatar.class)
public abstract class AvatarMixin {
    @Inject(method = "getDefaultDimensions", at = @At("HEAD"), cancellable = true)
    private void mousepillars$mouseDimensions(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        if (MouseState.isMouse((Entity) (Object) this)) {
            cir.setReturnValue(MouseForm.DIMENSIONS);
        }
    }
}