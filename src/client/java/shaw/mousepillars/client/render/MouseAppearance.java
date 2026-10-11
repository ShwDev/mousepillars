package shaw.mousepillars.client.render;

import net.minecraft.client.renderer.entity.state.AvatarRenderState;

/**
 * PUNTO DE REEMPLAZO del aspecto del ratón.
 *
 * El resto del mod solo sabe que "existe un MouseAppearance"; no sabe cómo se dibuja el ratón.
 * apply() se llama al final de la extracción del estado de render de un jugador que es ratón
 * (una vez por jugador y frame, ya en el hilo de render), así que es el sitio para:
 *  - encoger/transformar el modelo vanilla (lo que hace DefaultMouseAppearance),
 *  - o marcar el estado con una RenderStateDataKey de Fabric para que una RenderLayer propia
 *	dibuje tu modelo de ratón (y ocultar las partes del modelo de jugador que no quieras).
 *
 * Para cambiarlo: crea tu implementación y llama a MouseAppearances.set(...) desde MousePillarsClient.
 */
public interface MouseAppearance {
	void apply(AvatarRenderState state);
}
