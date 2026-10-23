package com.hbm.machine.alloy;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
public final class AlloyExtensionBlock extends BaseEntityBlock {
    private static final MapCodec<AlloyExtensionBlock> CODEC=simpleCodec(ignored->new AlloyExtensionBlock());
    public AlloyExtensionBlock() { super(Properties.of().mapColor(MapColor.STONE).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion()); }
    @Override protected MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state) { return new AlloyExtensionProxy(pos,state); }
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit) {
        if(player.isShiftKeyDown()||!(level.getBlockEntity(pos.below()) instanceof AlloyBlockEntity machine)) return InteractionResult.PASS;
        if(player instanceof ServerPlayer server) server.openMenu(machine,pos.below()); return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
