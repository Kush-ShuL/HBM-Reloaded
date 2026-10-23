package com.hbm.bomb.conventional;

import com.hbm.registry.HbmSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/** Original clearance arming: never kill the placer until every living entity has left twice the radius. */
public final class LandmineBlockEntity extends BlockEntity {
    private boolean primed;
    public boolean waitingForPlayer;
    public LandmineBlockEntity(BlockPos pos,BlockState state){super(LandmineContent.ENTITY.get(),pos,state);}
    public boolean primed(){return primed;}
    public static void tick(Level level,BlockPos pos,BlockState state,LandmineBlockEntity mine){
        if(!(state.getBlock() instanceof LandmineBlock block))return;
        double range=block.range(),height=block.height();if(mine.waitingForPlayer){range=25;height=25;}else if(!mine.primed){range*=2;height*=2;}
        if(!level.getBlockState(pos.above()).isAir())return;
        var entities=level.getEntities((Entity)null,new AABB(pos.getX()-range,pos.getY()-height,pos.getZ()-range,pos.getX()+range+1,pos.getY()+height,pos.getZ()+range+1),entity->true);
        for(var entity:entities){
            var category=entity.getType().getCategory();if(category==MobCategory.WATER_CREATURE||category==MobCategory.WATER_AMBIENT||category==MobCategory.UNDERGROUND_WATER_CREATURE||category==MobCategory.AXOLOTLS||category==MobCategory.AMBIENT)continue;
            if(mine.waitingForPlayer){if(entity instanceof Player){mine.waitingForPlayer=false;mine.setChanged();return;}}
            else if(entity instanceof LivingEntity){if(mine.primed){block.explode(level,pos.getX(),pos.getY(),pos.getZ());if(entity instanceof Player player)player.awardStat(LandmineContent.MINES_STAT.get());}return;}
        }
        if(!mine.primed&&!mine.waitingForPlayer){level.playSound(null,pos,HbmSounds.get("weapon.fstbmbStart"),SoundSource.BLOCKS,3,1);mine.primed=true;mine.setChanged();}
    }
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putBoolean("primed",primed);tag.putBoolean("waiting",waitingForPlayer);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);primed=tag.getBoolean("primed");waitingForPlayer=tag.getBoolean("waiting");}
}
