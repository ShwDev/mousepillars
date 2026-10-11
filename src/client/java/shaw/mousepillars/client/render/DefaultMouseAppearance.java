package shaw.mousepillars.client.render;

import net.minecraft.client.renderer.entity.state.AvatarRenderState;

/** Aspecto temporal: el propio modelo del jugador, reducido para que encaje con la hitbox del ratón. */
public class DefaultMouseAppearance implements MouseAppearance {
	/** Escala visual. Con 0.4 el modelo (1.8 de alto) queda en ~0.72; sube o baja hasta que case con tu hitbox. */
	public static final float MODEL_SCALE = 0.4F;

	@Override
	public void apply(AvatarRenderState state) {
		state.scale *= MODEL_SCALE;
	}
}
