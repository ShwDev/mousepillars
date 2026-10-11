package shaw.mousepillars.client.render;

/** Contenedor del aspecto activo del ratón. Un solo punto de acceso para el mixin de render. */
public final class MouseAppearances {
	private static MouseAppearance current = new DefaultMouseAppearance();

	private MouseAppearances() {
	}

	public static MouseAppearance get() {
		return current;
	}

	public static void set(MouseAppearance appearance) {
		current = appearance;
	}
}
