package shaw.mousepillars.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import shaw.mousepillars.MousePillars;

public record OpenConfigScreenPayload(BlockPos pos, boolean button) implements CustomPacketPayload {
	public static final Identifier ID = MousePillars.id("open_config_screen");
	public static final CustomPacketPayload.Type<OpenConfigScreenPayload> TYPE = new CustomPacketPayload.Type<>(ID);
	public static final StreamCodec<RegistryFriendlyByteBuf, OpenConfigScreenPayload> CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, OpenConfigScreenPayload::pos,
			ByteBufCodecs.BOOL, OpenConfigScreenPayload::button,
			OpenConfigScreenPayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
