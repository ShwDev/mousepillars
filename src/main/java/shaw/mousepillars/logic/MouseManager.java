package shaw.mousepillars.logic;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;

import shaw.mousepillars.registry.ModAttachments;

/**
 * Árbitro único de quién es ratón. Flujo por tick:
 *  1) los pilares (en su tick de BlockEntity) llaman a markInside() con los jugadores que están dentro de su área y encendidos;
 *  2) al final del tick del servidor, quien está marcado pasa a ratón y quien no, vuelve a humano.
 * Así no hace falta recordar "qué pilar transformó a quién": si el pilar se apaga, se rompe o su chunk se descarga,
 * deja de marcar y el jugador revierte solo.
 */
public final class MouseManager {
	/** Si es true, el jugador se queda ratón hasta que haya espacio para su hitbox normal (evita asfixiarse en un túnel de 1 bloque). */
	public static final boolean REQUIRE_ROOM_TO_RESTORE = true;

	private static final Set<UUID> INSIDE_THIS_TICK = new HashSet<>();

	private MouseManager() {
	}

	public static void init() {
		ServerTickEvents.END_SERVER_TICK.register(MouseManager::endTick);
		ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
			INSIDE_THIS_TICK.clear();
			PillarLinkRegistry.clear();
		});
	}

	public static void markInside(ServerPlayer player) {
		INSIDE_THIS_TICK.add(player.getUUID());
	}

	private static void endTick(MinecraftServer server) {
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			boolean shouldBeMouse = INSIDE_THIS_TICK.contains(player.getUUID()) && !player.isSpectator();
			boolean isMouse = MouseState.isMouse(player);

			if (shouldBeMouse && !isMouse) {
				becomeMouse(player);
			} else if (!shouldBeMouse && isMouse && (!REQUIRE_ROOM_TO_RESTORE || hasRoomToStand(player))) {
				becomeHuman(player);
			}
		}
		INSIDE_THIS_TICK.clear();

		if (server.getTickCount() % 200 == 0) {
			PillarLinkRegistry.purge(server.getTickCount());
		}
	}

	private static void becomeMouse(ServerPlayer player) {
		player.closeContainer();   // cierra cualquier menú abierto
		player.stopUsingItem();	// corta comer / arco / escudo en curso
		player.setAttached(ModAttachments.MOUSE_FORM, Boolean.TRUE);
	}

	private static void becomeHuman(ServerPlayer player) {
		player.removeAttached(ModAttachments.MOUSE_FORM);
	}

	private static boolean hasRoomToStand(ServerPlayer player) {
		double half = MouseForm.HUMAN_WIDTH / 2.0;
		AABB standing = new AABB(
				player.getX() - half, player.getY(), player.getZ() - half,
				player.getX() + half, player.getY() + MouseForm.HUMAN_HEIGHT, player.getZ() + half
		).deflate(1.0E-3);
		return player.level().noCollision(player, standing);
	}
}
