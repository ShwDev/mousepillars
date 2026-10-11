package shaw.mousepillars.logic;

import net.minecraft.world.entity.EntityDimensions;

public final class MouseForm {
	public static final float WIDTH = 0.4F;
	public static final float HEIGHT = 0.4F;
	public static final float EYE_HEIGHT = 0.3F;

	public static final EntityDimensions DIMENSIONS = EntityDimensions.scalable(WIDTH, HEIGHT).withEyeHeight(EYE_HEIGHT);

	public static final double HUMAN_WIDTH = 0.6;
	public static final double HUMAN_HEIGHT = 1.8;

	private MouseForm() {
	}
}
