package com.hbm.world.structure.logic;

import com.hbm.api.tool.ToolableBlock;
import com.hbm.block.LegacyBlockTraits;
import com.hbm.hazard.HbmHazards;
import com.hbm.interfaces.IBomb;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

/** The original authoring cube: disguise is saved for replacement and does not replace its arrow texture. */
public final class WandLogicBlock extends BaseEntityBlock implements ToolableBlock, IBomb {
    public static final IntegerProperty META = IntegerProperty.create("legacy_meta", 0, 15);
    public WandLogicBlock() { this(Properties.of().mapColor(MapColor.METAL).strength(0, 0).requiresCorrectToolForDrops()); }
    private WandLogicBlock(Properties properties) { super(properties); registerDefaultState(stateDefinition.any().setValue(META, 0)); }
    @Override protected MapCodec<? extends WandLogicBlock> codec() { return simpleCodec(WandLogicBlock::new); }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(META); }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return defaultBlockState().setValue(META, new int[]{3, 2, 0, 1}[Mth.floor(context.getRotation() * 4F / 360F + .5D) & 3]); }
    @Override public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (level.getBlockEntity(pos) instanceof LogicAuthoring tile) tile.placedRotation = new int[]{3, 4, 2, 5}[Mth.floor(placer.getYRot() * 4F / 360F + .5D) & 3];
    }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new LogicAuthoring(pos, state); }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) { return createTickerHelper(type, WandLogicContent.WAND_ENTITY.get(), LogicAuthoring::tick); }
    @Override protected ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack sourceHeld = player.getMainHandItem();
        if (!player.isShiftKeyDown() && sourceHeld.getItem() instanceof BlockItem item && item.getBlock() != this) {
            int metadata = sourceHeld.getOrDefault(HbmHazards.LEGACY_METADATA.get(), sourceHeld.getDamageValue()) & 15;
            var desired = com.hbm.world.structure.LegacyStructureStates.resolve(net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(item.getBlock()).toString(), metadata, 0);
            if (LegacyBlockTraits.renderAsNormalBlock(level, pos, desired) && level.getBlockEntity(pos) instanceof LogicAuthoring tile) { tile.applyDisguise(item.getBlock(), metadata); return ItemInteractionResult.SUCCESS; }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
    @Override public boolean onScrew(Level level, Player player, BlockPos pos, Direction side, float x, float y, float z, ToolType tool) { return level.getBlockEntity(pos) instanceof LogicAuthoring tile && tile.cycle(tool, player.isShiftKeyDown()); }
    @Override public BombReturnCode explode(Level level, int x, int y, int z) { if (!(level.getBlockEntity(new BlockPos(x,y,z)) instanceof LogicAuthoring tile)) return null; tile.trigger(); return BombReturnCode.TRIGGERED; }
}
