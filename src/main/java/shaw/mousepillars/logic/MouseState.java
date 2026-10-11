package shaw.mousepillars.logic;

import net.minecraft.world.entity.Entity;

import shaw.mousepillars.registry.ModAttachments;

/** Lectura del estado "ratón". Seguro de llamar desde cliente y servidor. */
public final class MouseState {
	private MouseState() {
	}

	public static boolean isMouse(Entity entity) {
		return Boolean.TRUE.equals(entity.getAttached(ModAttachments.MOUSE_FORM));
	}
}
