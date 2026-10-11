package shaw.mousepillars;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import shaw.mousepillars.logic.MouseManager;
import shaw.mousepillars.logic.MouseRestrictions;
import shaw.mousepillars.network.ModNetworking;
import shaw.mousepillars.registry.ModAttachments;
import shaw.mousepillars.registry.ModBlockEntities;
import shaw.mousepillars.registry.ModBlocks;
import shaw.mousepillars.registry.ModDisplayBlocks;

public class MousePillars implements ModInitializer {
	public static final String MOD_ID = "mousepillars";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// El orden importa: los bloques deben existir antes que los BlockEntityType que los referencian.
		ModAttachments.init();
		ModBlocks.init();
		ModDisplayBlocks.init();
		ModBlockEntities.init();
		ModNetworking.init();
		MouseManager.init();
		MouseRestrictions.init();

		LOGGER.info("MousePillars cargado");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
