package shaw.mousepillars.client.render;

import net.minecraft.client.renderer.entity.state.AvatarRenderState;

public class BearMouseAppearance implements MouseAppearance {

	public static final float BEAR_SCALE = 0.34F;

	@Override
	public void apply(AvatarRenderState state) {
		state.scale *= BEAR_SCALE;
		state.isInvisible = true;
		state.setData(BearMouseLayer.IS_MOUSE, Boolean.TRUE);
	}
}
