package shaw.mousepillars.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

import shaw.mousepillars.logic.MouseManager;
import shaw.mousepillars.logic.PillarLinkRegistry;
import shaw.mousepillars.registry.ModBlockEntities;

public class PillarBlockEntity extends BlockEntity {
	public static final int MIN_RANGE = 1;
	public static final int MAX_RANGE = 64;
	public static final int MAX_LINK_ID_LENGTH = 32;

	private boolean enabled = false;
	private int range = 5;
	private String linkId = "";

	public PillarBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.PILLAR, pos, state);
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public int getRange() {
		return this.range;
	}

	public String getLinkId() {
		return this.linkId;
	}

	public void setEnabled(boolean enabled) {
		if (this.enabled != enabled) {
			this.enabled = enabled;
			this.setChanged();
		}
	}

	/** Aplica la configuración recibida de la GUI. Sanea todo: nunca nos fiamos de lo que manda el cliente. */
	public void applyConfig(boolean enabled, int range, String linkId) {
		this.enabled = enabled;
		this.range = Math.max(MIN_RANGE, Math.min(MAX_RANGE, range));
		String cleaned = linkId == null ? "" : linkId.trim();
		this.linkId = cleaned.length() > MAX_LINK_ID_LENGTH ? cleaned.substring(0, MAX_LINK_ID_LENGTH) : cleaned;
		this.setChanged();
	}

	public AABB getArea() {
		double r = this.range / 2.0;
		BlockPos pos = this.worldPosition;
		return new AABB(
				pos.getX() + 0.5 - r, pos.getY() - r, pos.getZ() + 0.5 - r,
				pos.getX() + 0.5 + r, pos.getY() + 2 + r, pos.getZ() + 0.5 + r
		);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, PillarBlockEntity pillar) {
		if (!(level instanceof ServerLevel serverLevel)) {
			return;
		}

		int tick = serverLevel.getServer().getTickCount();

		// "Latido" para que el botón sepa qué pilares existen y están cargados con esta ID.
		if (!pillar.linkId.isEmpty()) {
			PillarLinkRegistry.heartbeat(serverLevel, pos, pillar.linkId, tick);
		}

		if (!pillar.enabled) {
			return;
		}

		AABB area = pillar.getArea();
		for (ServerPlayer player : serverLevel.players()) {
			if (!player.isSpectator() && area.contains(player.getX(), player.getY(), player.getZ())) {
				MouseManager.markInside(player);
			}
		}
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		output.putBoolean("enabled", this.enabled);
		output.putInt("range", this.range);
		output.putString("link_id", this.linkId);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		this.enabled = input.getBooleanOr("enabled", false);
		this.range = input.getIntOr("range", 5);
		this.linkId = input.getStringOr("link_id", "");
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
		return this.saveWithoutMetadata(registryLookup);
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (this.level != null) {
			BlockState state = this.getBlockState();
			this.level.sendBlockUpdated(this.worldPosition, state, state, Block.UPDATE_ALL);
		}
	}
}
