package shaw.mousepillars.client.render;

import net.minecraft.client.renderer.entity.state.AvatarRenderState;

public class DefaultMouseAppearance implements MouseAppearance {

	public static final float MODEL_SCALE = 0.4F;

	@Override
	public void apply(AvatarRenderState state) {
		state.scale *= MODEL_SCALE;
	}
}
