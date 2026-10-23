package com.hbm.world.structure.logic;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

/** Visible source runtime host remains a full colliding opaque stone cube, even when its icons are disguised. */
public class LogicBlock extends BaseEntityBlock {
    public LogicBlock() { this(Properties.of().mapColor(MapColor.STONE).strength(0,0).requiresCorrectToolForDrops()); }
    protected LogicBlock(Properties properties) { super(properties); }
    @Override protected MapCodec<? extends LogicBlock> codec() { return simpleCodec(LogicBlock::new); }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new LogicRuntime(pos, state); }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) { return createTickerHelper(type, WandLogicContent.RUNTIME_ENTITY.get(), LogicRuntime::tick); }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof LogicRuntime tile)) return InteractionResult.PASS;
        var local = hit.getLocation().subtract(pos.getX(),pos.getY(),pos.getZ());
        return tile.interact(new LogicProgram.Use(player,hit.getDirection(),(float)local.x,(float)local.y,(float)local.z)) ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }
}
