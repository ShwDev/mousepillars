package shaw.mousepillars.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

import shaw.mousepillars.blockentity.LinkButtonBlockEntity;
import shaw.mousepillars.blockentity.PillarBlockEntity;

public final class ModNetworking {
	private static final double MAX_EDIT_DISTANCE_SQR = 64.0;

	private ModNetworking() {
	}

	public static void init() {
		PayloadTypeRegistry.clientboundPlay().register(OpenConfigScreenPayload.TYPE, OpenConfigScreenPayload.CODEC);
		PayloadTypeRegistry.serverboundPlay().register(UpdatePillarConfigPayload.TYPE, UpdatePillarConfigPayload.CODEC);
		PayloadTypeRegistry.serverboundPlay().register(UpdateButtonConfigPayload.TYPE, UpdateButtonConfigPayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(UpdatePillarConfigPayload.TYPE, (payload, context) -> {
			ServerPlayer player = context.player();
			if (!canEdit(player, payload.pos())) {
				return;
			}
			if (player.level().getBlockEntity(payload.pos()) instanceof PillarBlockEntity pillar) {
				pillar.applyConfig(payload.enabled(), payload.range(), payload.linkId());
			}
		});

		ServerPlayNetworking.registerGlobalReceiver(UpdateButtonConfigPayload.TYPE, (payload, context) -> {
			ServerPlayer player = context.player();
			if (!canEdit(player, payload.pos())) {
				return;
			}
			if (player.level().getBlockEntity(payload.pos()) instanceof LinkButtonBlockEntity button) {
				button.applyConfig(payload.linkId());
			}
		});
	}

	/** Validación del servidor: un cliente modificado podría mandar este paquete sin haber abierto ninguna GUI. */
	private static boolean canEdit(ServerPlayer player, BlockPos pos) {
		return player.isCreative()
				&& player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= MAX_EDIT_DISTANCE_SQR;
	}
}
