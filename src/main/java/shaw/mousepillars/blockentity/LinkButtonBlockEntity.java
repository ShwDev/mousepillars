package shaw.mousepillars.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import shaw.mousepillars.registry.ModBlockEntities;

public class LinkButtonBlockEntity extends BlockEntity {
	private String linkId = "";

	public LinkButtonBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.LINK_BUTTON, pos, state);
	}

	public String getLinkId() {
		return this.linkId;
	}

	public void applyConfig(String linkId) {
		String cleaned = linkId == null ? "" : linkId.trim();
		this.linkId = cleaned.length() > PillarBlockEntity.MAX_LINK_ID_LENGTH
				? cleaned.substring(0, PillarBlockEntity.MAX_LINK_ID_LENGTH)
				: cleaned;
		this.setChanged();
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		output.putString("link_id", this.linkId);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
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
