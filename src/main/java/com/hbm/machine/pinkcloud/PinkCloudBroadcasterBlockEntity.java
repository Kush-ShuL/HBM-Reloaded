package com.hbm.machine.pinkcloud;

import com.hbm.damage.HbmDamageSources;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/** TileEntityBroadcaster's always-on spherical exposure, without adding power or a GUI. */
public final class PinkCloudBroadcasterBlockEntity extends BlockEntity implements com.hbm.multiblock.MuffledMachine {
    public static Consumer<PinkCloudBroadcasterBlockEntity> clientTick=ignored->{},clientRemoved=ignored->{};
    public boolean muffled,tilted;
    public PinkCloudBroadcasterBlockEntity(BlockPos pos,BlockState state){super(PinkCloudBroadcasterContent.ENTITY.get(),pos,state);}
    public static void tick(Level level,BlockPos pos,BlockState state,PinkCloudBroadcasterBlockEntity broadcaster){
        double x=pos.getX()+.5,y=pos.getY()+.5,z=pos.getZ()+.5;
        for(var entity:level.getEntities(null,new AABB(x-25,y-25,z-25,x+25,y+25,z+25))){
            if(!(entity instanceof LivingEntity living))continue;
            double distance=Math.sqrt(living.distanceToSqr(x,y,z));
            if(distance<=25){var effect=living.getEffect(MobEffects.CONFUSION);if(effect==null||effect.getDuration()<100)living.addEffect(new MobEffectInstance(MobEffects.CONFUSION,300,0));}
            // The original uses ordinary attackEntityFrom with the absolute/armor-bypassing source.
            // Its invulnerability frames, incoming-damage events and death hooks remain active.
            if(distance<=15)living.hurt(HbmDamageSources.source(level,HbmDamageSources.broadcast),(float)((15-distance)/15*10));
        }
        if(level.isClientSide)clientTick.accept(broadcaster);
    }
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");}
    @Override public void onChunkUnloaded(){clientRemoved.accept(this);super.onChunkUnloaded();}
    @Override public void setRemoved(){clientRemoved.accept(this);super.setRemoved();}

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
