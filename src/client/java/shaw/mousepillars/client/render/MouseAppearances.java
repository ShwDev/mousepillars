package shaw.mousepillars.client.render;

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
