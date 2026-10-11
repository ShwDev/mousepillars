package shaw.mousepillars.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

import shaw.mousepillars.blockentity.PillarBlockEntity;

/**
 * Índice "ID de vinculación -> pilares", sin persistencia y sin enganchar eventos de carga/descarga de chunks.
 * Cada pilar cargado deja un "latido" (el tick en que se vio por última vez) en su propio tick; el botón solo
 * considera pilares con latido del tick actual o el anterior. Los pilares rotos o en chunks descargados
 * dejan de latir y desaparecen solos del índice.
 */
public final class PillarLinkRegistry {
	public enum ToggleResult { NONE_FOUND, TURNED_ON, TURNED_OFF }

	private static final Map<String, Map<GlobalPos, Integer>> BY_LINK_ID = new HashMap<>();

	private PillarLinkRegistry() {
	}

	public static void heartbeat(ServerLevel level, BlockPos pos, String linkId, int tick) {
		BY_LINK_ID.computeIfAbsent(linkId, k -> new HashMap<>()).put(GlobalPos.of(level.dimension(), pos.immutable()), tick);
	}

	/**
	 * Si algún pilar de esa ID está ON, apaga todos; si ninguno lo está, enciende todos.
	 * Así un grupo con estados mezclados siempre converge en un solo gesto.
	 */
	public static ToggleResult toggle(MinecraftServer server, String linkId) {
		Map<GlobalPos, Integer> entries = BY_LINK_ID.get(linkId);
		if (entries == null) {
			return ToggleResult.NONE_FOUND;
		}

		int tick = server.getTickCount();
		List<PillarBlockEntity> targets = new ArrayList<>();
		for (Map.Entry<GlobalPos, Integer> entry : entries.entrySet()) {
			if (entry.getValue() < tick - 1) {
				continue; // sin latido reciente: roto o descargado
			}
			ServerLevel level = server.getLevel(entry.getKey().dimension());
			if (level != null
					&& level.getBlockEntity(entry.getKey().pos()) instanceof PillarBlockEntity pillar
					&& pillar.getLinkId().equals(linkId)) {
				targets.add(pillar);
			}
		}

		if (targets.isEmpty()) {
			return ToggleResult.NONE_FOUND;
		}

		boolean anyOn = targets.stream().anyMatch(PillarBlockEntity::isEnabled);
		boolean newState = !anyOn;
		targets.forEach(pillar -> pillar.setEnabled(newState));
		return newState ? ToggleResult.TURNED_ON : ToggleResult.TURNED_OFF;
	}

	/** Limpieza periódica de latidos viejos para que el mapa no crezca indefinidamente. */
	public static void purge(int tick) {
		BY_LINK_ID.values().forEach(entries -> entries.values().removeIf(last -> last < tick - 1));
		BY_LINK_ID.values().removeIf(Map::isEmpty);
	}

	public static void clear() {
		BY_LINK_ID.clear();
	}
}
