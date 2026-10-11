package shaw.mousepillars.block;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;

import shaw.mousepillars.blockentity.PillarBlockEntity;
import shaw.mousepillars.network.OpenConfigScreenPayload;
import shaw.mousepillars.registry.ModBlockEntities;

public class TransformationPillarBlock extends BaseEntityBlock {
	public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

	public TransformationPillarBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(HALF);
	}


	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return state.getValue(HALF) == DoubleBlockHalf.LOWER ? new PillarBlockEntity(pos, state) : null;
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if (level.isClientSide() || state.getValue(HALF) != DoubleBlockHalf.LOWER) {
			return null;
		}
		return createTickerHelper(type, ModBlockEntities.PILLAR, PillarBlockEntity::serverTick);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockPos pos = context.getClickedPos();
		Level level = context.getLevel();
		if (level.isOutsideBuildHeight(pos.above()) || !level.getBlockState(pos.above()).canBeReplaced(context)) {
			return null; // no hay espacio para el bloque de arriba
		}
		return this.defaultBlockState();
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), Block.UPDATE_ALL);
	}

	@Override
	protected BlockState updateShape(
			BlockState state,
			LevelReader level,
			ScheduledTickAccess ticks,
			BlockPos pos,
			Direction directionToNeighbour,
			BlockPos neighbourPos,
			BlockState neighbourState,
			RandomSource random
	) {
		DoubleBlockHalf half = state.getValue(HALF);
		boolean lookingAtOtherHalf = (half == DoubleBlockHalf.LOWER && directionToNeighbour == Direction.UP)
				|| (half == DoubleBlockHalf.UPPER && directionToNeighbour == Direction.DOWN);

		if (lookingAtOtherHalf) {
			boolean otherHalfIntact = neighbourState.is(this) && neighbourState.getValue(HALF) != half;
			return otherHalfIntact ? state : Blocks.AIR.defaultBlockState();
		}
		return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
	}

	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		// Si se rompe la mitad superior, se destruye la inferior "a mano" para que sea ella quien suelte el ítem
		// (la loot table solo da drop desde la mitad inferior) y en Creativo no suelte nada.
		if (!level.isClientSide() && state.getValue(HALF) == DoubleBlockHalf.UPPER) {
			BlockPos lowerPos = pos.below();
			if (level.getBlockState(lowerPos).is(this)) {
				level.destroyBlock(lowerPos, !player.isCreative(), player);
			}
		}
		return super.playerWillDestroy(level, pos, state, player);
	}

	// ---- Interacción ----

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (!player.isCreative()) {
			return InteractionResult.PASS; // en Supervivencia el pilar es un bloque inerte para el jugador
		}

		BlockPos basePos = state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
		if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
			// El cliente ya tiene los datos del BlockEntity (se sincronizan con el chunk); solo le decimos qué abrir.
			ServerPlayNetworking.send(serverPlayer, new OpenConfigScreenPayload(basePos, false));
		}
		return InteractionResult.SUCCESS;
	}
}
