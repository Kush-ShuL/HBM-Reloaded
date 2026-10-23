package com.hbm.bomb.conventional;

import com.hbm.registry.HbmSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** TileEntityCharge: source deliberately synchronizes but does not persist its timer or armed flag. */
public final class TimedChargeBlockEntity extends BlockEntity {
    public boolean started;
    public int timer;
    public TimedChargeBlockEntity(BlockPos pos,BlockState state){super(ConventionalContent.CHARGE_ENTITY.get(),pos,state);}
    public static void tick(Level level,BlockPos pos,BlockState state,TimedChargeBlockEntity charge){
        if(charge.started){charge.timer--;if(charge.timer%20==0&&charge.timer>0)level.playSound(null,pos,HbmSounds.get("weapon.fstbmbPing"),SoundSource.BLOCKS,1,1);if(charge.timer<=0)((TimedChargeBlock)state.getBlock()).explode(level,pos.getX(),pos.getY(),pos.getZ());}
        if(!charge.isRemoved())charge.synchronize();
    }
    public void synchronize(){if(level!=null)level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),net.minecraft.world.level.block.Block.UPDATE_CLIENTS);}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=super.getUpdateTag(lookup);tag.putInt("timer",timer);tag.putBoolean("started",started);return tag;}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){super.handleUpdateTag(tag,lookup);timer=tag.getInt("timer");started=tag.getBoolean("started");}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
    @Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
    public String display(){return pad(timer/1200)+":"+pad(timer/20%60);}
    private static String pad(int value){String text=Integer.toString(value);return text.length()==1?"0"+text:text;}
}
