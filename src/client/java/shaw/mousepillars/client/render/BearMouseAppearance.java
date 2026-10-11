package shaw.mousepillars.client.render;

import net.minecraft.client.renderer.entity.state.AvatarRenderState;

/** Aspecto de prueba: el oso de Blockbench. Oculta el cuerpo del jugador y deja que BearMouseLayer dibuje el oso. */
public class BearMouseAppearance implements MouseAppearance {
	/**
	 * Escala del oso (mide ~1.16 bloques de alto sin escalar). 0.34 lo deja en ~0.4, igual que la hitbox
	 * definida en MouseForm. Sube o baja este valor para ajustar el tamaño visual.
	 */
	public static final float BEAR_SCALE = 0.34F;

	@Override
	public void apply(AvatarRenderState state) {
		state.scale *= BEAR_SCALE;
		state.isInvisible = true; // oculta el modelo humano; las capas (como la del oso) se siguen dibujando
		state.setData(BearMouseLayer.IS_MOUSE, Boolean.TRUE);
	}
}
