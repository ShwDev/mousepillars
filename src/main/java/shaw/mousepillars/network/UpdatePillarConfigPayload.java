package shaw.mousepillars.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import shaw.mousepillars.MousePillars;
import shaw.mousepillars.blockentity.PillarBlockEntity;

public record UpdatePillarConfigPayload(BlockPos pos, boolean enabled, int range, String linkId) implements CustomPacketPayload {
	public static final Identifier ID = MousePillars.id("update_pillar_config");
	public static final CustomPacketPayload.Type<UpdatePillarConfigPayload> TYPE = new CustomPacketPayload.Type<>(ID);
	public static final StreamCodec<RegistryFriendlyByteBuf, UpdatePillarConfigPayload> CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, UpdatePillarConfigPayload::pos,
			ByteBufCodecs.BOOL, UpdatePillarConfigPayload::enabled,
			ByteBufCodecs.VAR_INT, UpdatePillarConfigPayload::range,
			ByteBufCodecs.stringUtf8(PillarBlockEntity.MAX_LINK_ID_LENGTH), UpdatePillarConfigPayload::linkId,
			UpdatePillarConfigPayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
