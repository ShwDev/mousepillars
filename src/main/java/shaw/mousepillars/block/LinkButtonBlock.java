package shaw.mousepillars.block;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import shaw.mousepillars.blockentity.LinkButtonBlockEntity;
import shaw.mousepillars.logic.PillarLinkRegistry;
import shaw.mousepillars.network.OpenConfigScreenPayload;

public class LinkButtonBlock extends FaceAttachedHorizontalDirectionalBlock implements EntityBlock {
	private static final VoxelShape CEILING_X = Block.box(6, 14, 5, 10, 16, 11);
	private static final VoxelShape CEILING_Z = Block.box(5, 14, 6, 11, 16, 10);
	private static final VoxelShape FLOOR_X = Block.box(6, 0, 5, 10, 2, 11);
	private static final VoxelShape FLOOR_Z = Block.box(5, 0, 6, 11, 2, 10);
	private static final VoxelShape NORTH = Block.box(5, 6, 14, 11, 10, 16);
	private static final VoxelShape SOUTH = Block.box(5, 6, 0, 11, 10, 2);
	private static final VoxelShape WEST = Block.box(14, 6, 5, 16, 10, 11);
	private static final VoxelShape EAST = Block.box(0, 6, 5, 2, 10, 11);

	public LinkButtonBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(FACE, AttachFace.WALL));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, FACE);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new LinkButtonBlockEntity(pos, state);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		Direction facing = state.getValue(FACING);
		return switch (state.getValue(FACE)) {
			case FLOOR -> facing.getAxis() == Direction.Axis.X ? FLOOR_X : FLOOR_Z;
			case CEILING -> facing.getAxis() == Direction.Axis.X ? CEILING_X : CEILING_Z;
			case WALL -> switch (facing) {
				case EAST -> EAST;
				case WEST -> WEST;
				case SOUTH -> SOUTH;
				default -> NORTH;
			};
		};
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (!(level.getBlockEntity(pos) instanceof LinkButtonBlockEntity button)) {
			return InteractionResult.PASS;
		}

		// Creativo: configurar
		if (player.isCreative() && !player.isShiftKeyDown()) {
			if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
				ServerPlayNetworking.send(serverPlayer, new OpenConfigScreenPayload(pos, true));
			}
			return InteractionResult.SUCCESS;
		}

		if (level instanceof ServerLevel serverLevel) {
			String linkId = button.getLinkId();
			if (linkId.isEmpty()) {
				player.sendOverlayMessage(Component.translatable("message.mousepillars.no_link_id"));
			} else {
				PillarLinkRegistry.ToggleResult result = PillarLinkRegistry.toggle(serverLevel.getServer(), linkId);
				if (result == PillarLinkRegistry.ToggleResult.NONE_FOUND) {
					player.sendOverlayMessage(Component.translatable("message.mousepillars.no_pillars", linkId));
				} else {
					boolean on = result == PillarLinkRegistry.ToggleResult.TURNED_ON;
					level.playSound(null, pos, SoundEvents.STONE_BUTTON_CLICK_ON, SoundSource.BLOCKS, 0.3F, on ? 0.6F : 0.5F);
					player.sendOverlayMessage(Component.translatable(on ? "message.mousepillars.pillars_on" : "message.mousepillars.pillars_off", linkId));
				}
			}
		}
		return InteractionResult.SUCCESS;
	}
}
