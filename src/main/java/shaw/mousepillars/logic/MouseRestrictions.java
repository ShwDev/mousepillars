package shaw.mousepillars.logic;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockState;

import shaw.mousepillars.registry.ModBlocks;

/**
 * Restricciones de gameplay del ratón basadas en eventos de Fabric (no en mixins):
 * sin usar ítems, sin colocar/romper bloques, sin atacar ni interactuar con entidades.
 * Excepción: puede pulsar el botón de control, y un jugador en Creativo puede abrir la GUI del pilar
 * (si no, quien configura un pilar desde dentro de su propia área quedaría atrapado fuera de la GUI).
 * Estos eventos corren en cliente y servidor; el servidor es el que manda.
 * La parte de "selección de hotbar" vive en InventoryMixin y la de pantallas/HUD en el cliente.
 */
public final class MouseRestrictions {
	private MouseRestrictions() {
	}

	public static void init() {
		UseItemCallback.EVENT.register((player, level, hand) ->
				MouseState.isMouse(player) ? InteractionResult.FAIL : InteractionResult.PASS);

		UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
			if (!MouseState.isMouse(player)) {
				return InteractionResult.PASS;
			}
			BlockState target = level.getBlockState(hitResult.getBlockPos());
			boolean allowed = target.is(ModBlocks.LINK_BUTTON)
					|| (target.is(ModBlocks.TRANSFORMATION_PILLAR) && player.isCreative());
			return allowed ? InteractionResult.PASS : InteractionResult.FAIL;
		});

		UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) ->
				MouseState.isMouse(player) ? InteractionResult.FAIL : InteractionResult.PASS);

		AttackBlockCallback.EVENT.register((player, level, hand, pos, direction) ->
				MouseState.isMouse(player) ? InteractionResult.FAIL : InteractionResult.PASS);

		AttackEntityCallback.EVENT.register((player, level, hand, entity, hitResult) ->
				MouseState.isMouse(player) ? InteractionResult.FAIL : InteractionResult.PASS);
	}
}
