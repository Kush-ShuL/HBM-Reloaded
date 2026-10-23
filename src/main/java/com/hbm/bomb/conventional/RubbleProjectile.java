package com.hbm.bomb.conventional;

import com.hbm.damage.HbmDamageSources;
import com.hbm.entity.projectile.EntityThrowableNT;
import com.hbm.registry.HbmSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/** Source EntityRubble, using the shared original EntityThrowableNT motion and impact tracing. */
public final class RubbleProjectile extends EntityThrowableNT {
    private static final EntityDataAccessor<BlockState> MATERIAL=SynchedEntityData.defineId(RubbleProjectile.class,EntityDataSerializers.BLOCK_STATE);
    public RubbleProjectile(EntityType<? extends RubbleProjectile> type,Level level){super(type,level);}
    public RubbleProjectile(Level level){this(ConventionalProjectiles.RUBBLE.get(),level);}
    public RubbleProjectile(Level level,double x,double y,double z){this(level);setPos(x,y,z);}
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder){super.defineSynchedData(builder);builder.define(MATERIAL,Blocks.AIR.defaultBlockState());}
    public BlockState material(){return entityData.get(MATERIAL);}
    @Override public net.minecraft.world.phys.AABB getBoundingBoxForCulling(){return getBoundingBox().inflate(1);}
    public void setMaterial(BlockState state){entityData.set(MATERIAL,state);}
    public void setMetaBasedOnBlock(BlockState state){setMaterial(state);}
    @Override protected float getAirDrag(){return 1F;}
    @Override protected void onImpact(HitResult impact){
        if(impact instanceof EntityHitResult hit)hit.getEntity().hurt(HbmDamageSources.source(level(),HbmDamageSources.rubble),15);
        if(tickCount>2){discard();
            if(level().isClientSide)level().playLocalSound(getX(),getY(),getZ(),HbmSounds.get("block.debris"),SoundSource.BLOCKS,1.5F,1,false);
            else{level().playSound(null,getX(),getY(),getZ(),HbmSounds.get("block.debris"),SoundSource.BLOCKS,1.5F,1);RubbleBurstEffects.send(level(),(int)Math.floor(getX()),(int)getY(),(int)Math.floor(getZ()),material(),getX(),getY(),getZ());}
        }
    }
    @Override protected void addAdditionalSaveData(CompoundTag tag){super.addAdditionalSaveData(tag);tag.put("block",NbtUtils.writeBlockState(material()));}
    @Override protected void readAdditionalSaveData(CompoundTag tag){super.readAdditionalSaveData(tag);setMaterial(NbtUtils.readBlockState(level().holderLookup(Registries.BLOCK),tag.getCompound("block")));}
}
