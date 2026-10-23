package com.hbm.entity.effect;

import com.hbm.block.fluid.LegacyMaterials;
import com.hbm.bomb.conventional.RubbleProjectile;
import com.hbm.damage.HbmDamageSources;
import com.hbm.entity.LegacyTrackingRange;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** Original EntityBlackHole: source rays, gravity, lethal core and antimatter collapse, with real rubble. */
public class EntityBlackHole extends Entity implements LegacyTrackingRange {
    private static final ResourceLocation ANTIMATTER=ResourceLocation.fromNamespaceAndPath("hbm","pellet_antimatter");
    private static final ResourceLocation PONY=ResourceLocation.fromNamespaceAndPath("hbm","flame_pony");
    private static final EntityDataAccessor<Float> SIZE=SynchedEntityData.defineId(EntityBlackHole.class,EntityDataSerializers.FLOAT);
    public boolean breaksBlocks=true;
    public EntityBlackHole(EntityType<? extends EntityBlackHole> type,Level level){super(type,level);noCulling=true;noPhysics=true;}
    public EntityBlackHole(Level level){this(BlackHoleContent.BLACK_HOLE.get(),level);}
    public EntityBlackHole(Level level,float size){this(level);setSize(size);}
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder){builder.define(SIZE,.5F);}
    public float size(){return entityData.get(SIZE);}
    public void setSize(float size){entityData.set(SIZE,size);}
    public EntityBlackHole noBreak(){breaksBlocks=false;return this;}
    @Override public int legacyTrackingRange(){return 250;}
    @Override public boolean shouldRenderAtSqrDistance(double distance){return distance<25000;}
    @Override public void tick(){
        super.tick();float size=size();
        if(!level().isClientSide&&breaksBlocks){
            for(int k=0;k<size*2;k++){
                double phi=random.nextDouble()*(Math.PI*2),costheta=random.nextDouble()*2-1,theta=Math.acos(costheta);
                var ray=new Vec3(Math.sin(theta)*Math.cos(phi),Math.sin(theta)*Math.sin(phi),Math.cos(theta));int length=(int)Math.ceil(size*15);
                for(int i=0;i<length;i++){
                    //Source casts truncate toward zero; floor at negative world coordinates would change its rays.
                    var pos=new BlockPos((int)(getX()+ray.x*i),(int)(getY()+ray.y*i),(int)(getZ()+ray.z*i));var state=level().getBlockState(pos);
                    if(LegacyMaterials.of(state).isLiquid()){level().setBlock(pos,Blocks.AIR.defaultBlockState(),3);state=level().getBlockState(pos);}
                    if(!state.isAir()){
                        var rubble=new RubbleProjectile(level(),pos.getX()+.5,pos.getY(),pos.getZ()+.5);rubble.setMetaBasedOnBlock(state);level().addFreshEntity(rubble);
                        level().setBlock(pos,Blocks.AIR.defaultBlockState(),3);break;
                    }
                }
            }
        }
        double range=size*15;
        for(var entity:level().getEntities(this,new AABB(getX()-range,getY()-range,getZ()-range,getX()+range,getY()+range,getZ()+range))){
            if(entity instanceof Player player&&player.getAbilities().instabuild)continue;
            if(entity instanceof FallingBlockEntity falling&&!level().isClientSide&&entity.tickCount>1){
                var rubble=new RubbleProjectile(level(),entity.getX(),entity.getY(),entity.getZ());rubble.setMetaBasedOnBlock(falling.getBlockState());
                entity.discard();rubble.setYRot(0);rubble.setXRot(0);rubble.setDeltaMovement(entity.getDeltaMovement());level().addFreshEntity(rubble);
                //Source deliberately continues processing the original, now discarded falling entity on this iteration.
            }
            var vector=new Vec3(getX()-entity.getX(),getY()-entity.getY(),getZ()-entity.getZ());double distance=vector.length();if(distance>range)continue;
            vector=vector.normalize();if(!(entity instanceof ItemEntity))vector=vector.yRot((float)Math.toRadians(15));
            entity.setDeltaMovement(entity.getDeltaMovement().add(vector.x*.1,vector.y*.2,vector.z*.1));
            if(entity instanceof EntityBlackHole)continue;
            if(distance<size*1.5){
                entity.hurt(HbmDamageSources.source(level(),HbmDamageSources.blackhole),1000);
                if(!(entity instanceof LivingEntity))entity.discard();
                if(!level().isClientSide&&entity instanceof ItemEntity item){
                    // Match the actual swallowed item without resolving an unmigrated optional counterpart.
                    var id=BuiltInRegistries.ITEM.getKey(item.getItem().getItem());if(ANTIMATTER.equals(id)||PONY.equals(id)){
                        discard();level().explode(null,getX(),getY(),getZ(),5,false,Level.ExplosionInteraction.BLOCK);return;
                    }
                }
            }
        }
        var velocity=getDeltaMovement();setPos(position().add(velocity));setDeltaMovement(velocity.scale(.99));
    }
    @Override protected void addAdditionalSaveData(CompoundTag tag){tag.putFloat("size",size());tag.putBoolean("breaksBlocks",breaksBlocks);}
    @Override protected void readAdditionalSaveData(CompoundTag tag){setSize(tag.getFloat("size"));breaksBlocks=tag.getBoolean("breaksBlocks");}
}
