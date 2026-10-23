package com.hbm.machine.reactor.rbmk.storage;
import com.hbm.machine.reactor.rbmk.column.RbmkColumnBlock;
import com.hbm.multiblock.DummyInventoryProxy;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
/** Source passive-style storage column, inventory proxy at EVERY non-core cell. */
public final class RbmkStorageBlock extends RbmkColumnBlock {
    @Override protected MapCodec<? extends RbmkStorageBlock> codec(){return MapCodec.unit(this);}@Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new RbmkStorageEntity(pos,state):new DummyInventoryProxy(pos,state);}@Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,RbmkStorageContent.ENTITY.get(),RbmkStorageEntity::tick):null;}
    private static void mark(Player player){if(!player.level().isClientSide){var data=player.getPersistentData();var persisted=data.getCompound(Player.PERSISTED_NBT_TAG);persisted.putLong("fbiMark",player.level().getGameTime()+20*60*20);data.put(Player.PERSISTED_NBT_TAG,persisted);}}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){mark(player);return super.useItemOn(stack,state,level,pos,player,hand,hit);}@Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){mark(player);return super.useWithoutItem(state,level,pos,player,hit);}
}
