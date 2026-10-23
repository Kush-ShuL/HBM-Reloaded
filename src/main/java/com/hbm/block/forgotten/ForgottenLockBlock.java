package com.hbm.block.forgotten;

import com.hbm.registry.HbmContent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** Original BlockForgottenLock. META3's unused named constant is not a damage/kill implementation. */
public final class ForgottenLockBlock extends ForgottenBrickBlock {
    public ForgottenLockBlock(){super(true);}
    private InteractionResult activate(Level level,BlockPos pos,Player player,BlockHitResult hit){
        var held=player.getMainHandItem();boolean cracked=held.is(HbmContent.item("key_red_cracked"));
        if((!held.is(HbmContent.item("key_red"))&&!cracked)||hit.getDirection().getAxis()==net.minecraft.core.Direction.Axis.Y)return InteractionResult.PASS;
        // Source activation cannot run a second time at a lock that the first cold callback already reserved for replacement.
        if(level instanceof ServerLevel server&&ForgottenTunnel.pendingAt(server,pos))return InteractionResult.CONSUME;
        if(cracked)held.shrink(1); // Deliberately before remote check, even creative, matching the source client prediction.
        if(level instanceof ServerLevel server)ForgottenTunnel.generate(server,pos,hit.getDirection(),new double[]{player.getX(),player.getY(),player.getZ()});
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return activate(level,pos,player,hit);}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){return activate(level,pos,player,hit).consumesAction()?ItemInteractionResult.sidedSuccess(level.isClientSide):ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;}
}
