package com.hbm.entity.missile;

import com.hbm.bomb.nuclear.*;
import com.hbm.missile.rusted.RustedMissileContent;
import com.hbm.registry.HbmContent;
import java.util.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

/** Real source Tier4 shared debris/radar/three-nozzle plume and the actual doomsday inheritance chain. */
public abstract class EntityMissileTier4 extends EntityMissileBaseNT {
    protected EntityMissileTier4(EntityType<?> type,Level level){super(type,level);}
    @Override public List<ItemStack> getDebris(){return new ArrayList<>(List.of(new ItemStack(HbmContent.item("plate_titanium"),16),new ItemStack(HbmContent.item("plate_steel"),20),new ItemStack(HbmContent.item("plate_aluminium"),12),new ItemStack(HbmContent.item("thruster_large")))) ;}
    @Override public String getUnlocalizedName(){return "radar.target.tier4";}@Override public int getBlipLevel(){return TIER4;}
    @Override protected void spawnContrail(){
        var thrust=LegacyVector.createVectorHelper(0,0,1);switch(rotation()){case 2->thrust.rotateAroundY(-(float)Math.PI/2F);case 4->thrust.rotateAroundY(-(float)Math.PI);case 3->thrust.rotateAroundY(-(float)Math.PI/2F*3F);}
        thrust.rotateAroundY((getYRot()+90)*(float)Math.PI/180F);rotateAroundX(thrust,getXRot()*(float)Math.PI/180F);thrust.rotateAroundY(-(getYRot()+90)*(float)Math.PI/180F);
        spawnContrailWithOffset(thrust.xCoord,thrust.yCoord,thrust.zCoord);spawnContrailWithOffset(0,0,0);spawnContrailWithOffset(-thrust.xCoord,-thrust.zCoord,-thrust.zCoord);
    }
    public static final class EntityMissileNuclear extends EntityMissileTier4 {
        public EntityMissileNuclear(EntityType<?> type,Level level){super(type,level);}public EntityMissileNuclear(Level level,float x,float y,float z,int a,int b){this(com.hbm.missile.completion.MissileContent.type("missile_nuclear"),level);initialize(x,y,z,a,b);}
        @Override public void onMissileImpact(HitResult hit){int radius=NuclearConfig.radius("missile");level().addFreshEntity(NuclearExplosionMK5.statFac(level(),radius,getX(),getY(),getZ()));NuclearTorex.statFacStandard(level(),getX(),getY(),getZ(),radius);}
        @Override public ItemStack getDebrisRareDrop(){return new ItemStack(com.hbm.missile.completion.MissileContent.item("warhead_nuclear"));}@Override public ItemStack getMissileItemForInfo(){return new ItemStack(com.hbm.missile.completion.MissileContent.item("missile_nuclear"));}
    }
    public static final class EntityMissileMirv extends EntityMissileTier4 {
        public EntityMissileMirv(EntityType<?> type,Level level){super(type,level);}public EntityMissileMirv(Level level,float x,float y,float z,int a,int b){this(com.hbm.missile.completion.MissileContent.type("missile_nuclear_cluster"),level);initialize(x,y,z,a,b);}
        @Override public void onMissileImpact(HitResult hit){int radius=NuclearConfig.radius("missile")*2;level().addFreshEntity(NuclearExplosionMK5.statFac(level(),radius,getX(),getY(),getZ()));NuclearTorex.statFacStandard(level(),getX(),getY(),getZ(),radius);}
        @Override public ItemStack getDebrisRareDrop(){return new ItemStack(com.hbm.missile.completion.MissileContent.item("warhead_mirv"));}@Override public ItemStack getMissileItemForInfo(){return new ItemStack(com.hbm.missile.completion.MissileContent.item("missile_nuclear_cluster"));}
    }
    public static final class EntityMissileVolcano extends EntityMissileTier4 {
        public EntityMissileVolcano(EntityType<?> type,Level level){super(type,level);}public EntityMissileVolcano(Level level,float x,float y,float z,int a,int b){this(com.hbm.missile.completion.MissileContent.type("missile_volcano"),level);initialize(x,y,z,a,b);}
        @Override public void onMissileImpact(HitResult hit){com.hbm.explosion.ExplosionLarge.explode(level(),getX(),getY(),getZ(),10,true,true,true);for(int x=-1;x<=1;x++)for(int y=-1;y<=1;y++)for(int z=-1;z<=1;z++)level().setBlockAndUpdate(net.minecraft.core.BlockPos.containing(getX()+x,getY()+y,getZ()+z),com.hbm.block.fluid.HbmWorldFluids.block("volcanic_lava_block").defaultBlockState());level().setBlockAndUpdate(blockPosition(),com.hbm.missile.completion.volcano.VolcanoContent.NORMAL.get().defaultBlockState());}
        @Override public ItemStack getDebrisRareDrop(){return new ItemStack(com.hbm.missile.completion.MissileContent.item("warhead_volcano"));}@Override public ItemStack getMissileItemForInfo(){return new ItemStack(com.hbm.missile.completion.MissileContent.item("missile_volcano"));}
    }
    /** Actual pristine source doomsday, shared with the separately registered rusted subtype. */
    public static class EntityMissileDoomsday extends EntityMissileTier4 {
        public EntityMissileDoomsday(EntityType<?> type,Level level){super(type,level);}
        public EntityMissileDoomsday(Level level,float x,float y,float z,int a,int b){this(com.hbm.missile.completion.MissileContent.type("missile_doomsday"),level);initialize(x,y,z,a,b);}
        @Override public ItemStack getMissileItemForInfo(){return new ItemStack(com.hbm.missile.completion.MissileContent.item("missile_doomsday"));}
        @Override public void onMissileImpact(HitResult hit){int radius=NuclearConfig.radius("missile")*2;level().addFreshEntity(NuclearExplosionMK5.statFac(level(),radius,getX(),getY(),getZ()).moreFallout(100));NuclearTorex.statFacStandard(level(),getX(),getY(),getZ(),radius);}
        @Override public List<ItemStack> getDebris(){return null;}@Override public ItemStack getDebrisRareDrop(){return null;}@Override public String getUnlocalizedName(){return "radar.target.doomsday";}
    }
    public static final class EntityMissileDoomsdayRusted extends EntityMissileDoomsday {
        public EntityMissileDoomsdayRusted(EntityType<? extends EntityMissileDoomsdayRusted> type,Level level){super(type,level);}
        public EntityMissileDoomsdayRusted(Level level,float x,float y,float z,int targetX,int targetZ){this(RustedMissileContent.ENTITY.get(),level);initialize(x,y,z,targetX,targetZ);}
        @Override public void onMissileImpact(HitResult hit){int radius=NuclearConfig.radius("missile");level().addFreshEntity(NuclearExplosionMK5.statFac(level(),radius,getX(),getY(),getZ()).moreFallout(100));NuclearTorex.statFacStandard(level(),getX(),getY(),getZ(),radius);}
        @Override public ItemStack getMissileItemForInfo(){return new ItemStack(RustedMissileContent.ITEM.get());}
    }
}
