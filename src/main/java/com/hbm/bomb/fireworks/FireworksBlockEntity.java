package com.hbm.bomb.fireworks;

import com.hbm.registry.HbmSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class FireworksBlockEntity extends BlockEntity {
    public int color=0xFF0000,charges;
    public String message="NUCLEAR TECH";
    private int index,delay;
    public FireworksBlockEntity(BlockPos pos,BlockState state){super(FireworksContent.TILE.get(),pos,state);}
    static void tick(Level world,BlockPos pos,BlockState state,FireworksBlockEntity tile){
        if(world.isClientSide)return;
        if(world.hasNeighborSignal(pos)&&!tile.message.isEmpty()&&tile.charges>0){
            if(--tile.delay<=0){
                tile.delay=30;int character=tile.message.charAt(tile.index),mod=tile.index%9;
                double dx=(mod/3-1)*.3125,dz=(mod%3-1)*.3125;
                var entity=new EntityFireworks(world,pos.getX()+.5+dx,pos.getY()+1.5,pos.getZ()+.5+dz,tile.color,character);world.addFreshEntity(entity);
                world.playSound(null,entity.getX(),entity.getY(),entity.getZ(),HbmSounds.get("weapon.rocketFlame"),net.minecraft.sounds.SoundSource.BLOCKS,3,1);
                tile.charges--;tile.setChanged();FireworksEffects.launch(world,pos.getX()+.5+dx,pos.getY()+1.125,pos.getZ()+.5+dz);
                if(++tile.index>=tile.message.length()){tile.index=0;tile.delay=100;}
            }
        }else{tile.delay=0;tile.index=0;}
    }
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putInt("charges",charges);tag.putInt("color",color);tag.putString("message",message);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);charges=tag.getInt("charges");color=tag.getInt("color");message=tag.getString("message");}
    // The original transient index/delay are intentionally absent from NBT, and no public inventory/sync is invented.
}
