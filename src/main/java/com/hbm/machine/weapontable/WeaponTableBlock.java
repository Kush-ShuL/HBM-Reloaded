package com.hbm.machine.weapontable;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
public final class WeaponTableBlock extends Block {
    public WeaponTableBlock(){super(Properties.of().strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops());}
    @Override protected MapCodec<? extends Block> codec(){return MapCodec.unit(this);}
    private InteractionResult open(Level level,Player player){
        if(level.isClientSide)return InteractionResult.SUCCESS;if(player.isShiftKeyDown())return InteractionResult.PASS;
        if(player instanceof ServerPlayer server)server.openMenu(new SimpleMenuProvider((id,inventory,viewer)->new WeaponTableMenu(id,inventory),Component.translatable("container.weaponsTable")));
        return InteractionResult.CONSUME;
    }
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return open(level,player);}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){return open(level,player)==InteractionResult.PASS?ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION:ItemInteractionResult.sidedSuccess(level.isClientSide);}
}
