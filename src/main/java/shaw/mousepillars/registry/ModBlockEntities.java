package shaw.mousepillars.registry;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import shaw.mousepillars.MousePillars;
import shaw.mousepillars.blockentity.LinkButtonBlockEntity;
import shaw.mousepillars.blockentity.PillarBlockEntity;

public final class ModBlockEntities {
	public static final BlockEntityType<PillarBlockEntity> PILLAR =
			register("transformation_pillar", PillarBlockEntity::new, ModBlocks.TRANSFORMATION_PILLAR);

	public static final BlockEntityType<LinkButtonBlockEntity> LINK_BUTTON =
			register("link_button", LinkButtonBlockEntity::new, ModBlocks.LINK_BUTTON);

	private ModBlockEntities() {
	}

	private static <T extends BlockEntity> BlockEntityType<T> register(
			String name,
			FabricBlockEntityTypeBuilder.Factory<? extends T> factory,
			Block... blocks
	) {
		Identifier id = MousePillars.id(name);
		return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.<T>create(factory, blocks).build());
	}

	public static void init() {
	}
}
