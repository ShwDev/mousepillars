package shaw.mousepillars.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import shaw.mousepillars.MousePillars;

/**
 * Bloques "de utilería" que existen solo para que el juego cargue un modelo JSON y el renderer pueda dibujarlo.
 * No tienen ítem ni se pueden obtener en el inventario: BEAR_DISPLAY es la forma de ratón de prueba (el oso).
 */
public final class ModDisplayBlocks {
	public static final Block BEAR_DISPLAY = register("bear_display");

	private ModDisplayBlocks() {
	}

	private static Block register(String name) {
		ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, MousePillars.id(name));
		Block block = new Block(BlockBehaviour.Properties.of().setId(key));
		return Registry.register(BuiltInRegistries.BLOCK, key, block);
	}

	public static void init() {
	}
}
