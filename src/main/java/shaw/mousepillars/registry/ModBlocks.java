package shaw.mousepillars.registry;

import java.util.function.Function;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import shaw.mousepillars.MousePillars;
import shaw.mousepillars.block.LinkButtonBlock;
import shaw.mousepillars.block.TransformationPillarBlock;

public final class ModBlocks {
	public static final TransformationPillarBlock TRANSFORMATION_PILLAR = registerBlock(
			"transformation_pillar",
			TransformationPillarBlock::new,
			BlockBehaviour.Properties.of().strength(2.0F, 6.0F).sound(SoundType.STONE)
	);

	public static final LinkButtonBlock LINK_BUTTON = registerBlock(
			"link_button",
			LinkButtonBlock::new,
			BlockBehaviour.Properties.of().noCollision().strength(0.5F).sound(SoundType.STONE)
	);

	private ModBlocks() {
	}

	private static <T extends Block> T registerBlock(String name, Function<BlockBehaviour.Properties, T> factory, BlockBehaviour.Properties properties) {
		Identifier id = MousePillars.id(name);
		ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, id);
		ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, id);

		T block = factory.apply(properties.setId(blockKey));
		Registry.register(BuiltInRegistries.BLOCK, blockKey, block);

		BlockItem item = new BlockItem(block, new Item.Properties().useBlockDescriptionPrefix().setId(itemKey));
		Registry.register(BuiltInRegistries.ITEM, itemKey, item);
		return block;
	}

	public static void init() {
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(tab -> {
			tab.accept(TRANSFORMATION_PILLAR.asItem());
			tab.accept(LINK_BUTTON.asItem());
		});
	}
}
