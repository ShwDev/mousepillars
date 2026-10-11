package shaw.mousepillars.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import shaw.mousepillars.MousePillars;
import shaw.mousepillars.blockentity.PillarBlockEntity;

public record UpdateButtonConfigPayload(BlockPos pos, String linkId) implements CustomPacketPayload {
	public static final Identifier ID = MousePillars.id("update_button_config");
	public static final CustomPacketPayload.Type<UpdateButtonConfigPayload> TYPE = new CustomPacketPayload.Type<>(ID);
	public static final StreamCodec<RegistryFriendlyByteBuf, UpdateButtonConfigPayload> CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, UpdateButtonConfigPayload::pos,
			ByteBufCodecs.stringUtf8(PillarBlockEntity.MAX_LINK_ID_LENGTH), UpdateButtonConfigPayload::linkId,
			UpdateButtonConfigPayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
