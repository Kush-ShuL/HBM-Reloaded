package com.hbm.block.devtesters;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
public final class EventTesterBlock extends BaseEntityBlock {
    private static final MapCodec<EventTesterBlock> CODEC=simpleCodec(ignored->new EventTesterBlock());
    public EventTesterBlock(){super(Properties.of().mapColor(net.minecraft.world.level.material.MapColor.METAL).strength(2.5F,0).sound(SoundType.STONE));}
    @Override protected MapCodec<EventTesterBlock> codec(){return CODEC;}
    @Override public RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new EventTesterEntity(pos,state);}
    private void open(Level level,BlockPos pos,Player player){if(player instanceof ServerPlayer server&&level.getBlockEntity(pos) instanceof EventTesterEntity storage)server.openMenu(storage);}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){open(level,pos,player);return InteractionResult.sidedSuccess(level.isClientSide);}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){open(level,pos,player);return ItemInteractionResult.sidedSuccess(level.isClientSide);}
}
