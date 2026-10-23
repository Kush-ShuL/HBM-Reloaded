package com.hbm.railway.cart;

import com.hbm.registry.HbmContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

/** The separately registered source type9, not ItemModMinecart's EMPTY/EntityMinecartOre. */
public final class SourceMinecartTest extends SourceCartEntity {
    private static final EntityDataAccessor<Integer> X_SIGN=SynchedEntityData.defineId(SourceMinecartTest.class,EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> Z_SIGN=SynchedEntityData.defineId(SourceMinecartTest.class,EntityDataSerializers.INT);
    private int fuse=-1;

    public SourceMinecartTest(EntityType<?> type,Level level){
        super(type,level,SourceCartKind.EMPTY);
        setMaxSpeedAirLateral(.4F);
        setMaxSpeedAirVertical(-1F);
        setDragAir(.94999998807907104D);
    }
    public SourceMinecartTest(Level level,double x,double y,double z){this(SourceCartBaseContent.TEST.get(),level);setPos(x,y,z);}
    @Override protected void defineSynchedData(SynchedEntityData.Builder data){super.defineSynchedData(data);data.define(X_SIGN,0);data.define(Z_SIGN,0);}
    public int sourceMinecartType(){return 9;}
    public int xSign(){return entityData.get(X_SIGN);}
    public int zSign(){return entityData.get(Z_SIGN);}
    public int getFuse(){return fuse;}
    public boolean isIgnited(){return fuse>-1;}
    @Override public int legacyTrackingRange(){return 1000;}
    @Override public float getMaxCartSpeedOnRail(){return 1.2F;}
    @Override public double getSlopeAdjustment(){return .0078125D;}
    @Override public boolean canBeCollidedWith(){return !isRemoved();}
    @Override public boolean canCollideWith(Entity other){return other.isPushable()&&!isPassengerOfSameVehicle(other);}
    @Override public ItemStack getCartItem(){return new ItemStack(Items.MINECART);}
    @Override protected Item getDropItem(){return Items.MINECART;}
    @Override public BlockState getDefaultDisplayBlockState(){return HbmContent.block("crate").defaultBlockState();}

    @Override public void tick(){
        if(!ready(level(),getBoundingBox().expandTowards(getDeltaMovement()).inflate(2,1,2)))return;
        // The old bridged base updates watchers23/24 before physics, retaining both at complete rest.
        var motion=getDeltaMovement();
        if(motion.x<0)entityData.set(X_SIGN,-1);if(motion.x>0)entityData.set(X_SIGN,1);
        if(motion.x==0&&motion.z!=0)entityData.set(X_SIGN,0);
        if(motion.z<0)entityData.set(Z_SIGN,-1);if(motion.z>0)entityData.set(Z_SIGN,1);
        if(motion.z==0&&motion.x!=0)entityData.set(Z_SIGN,0);
        super.tick();
    }
    @Override protected void applyNaturalSlowdown(){
        // Exact source float-derived drag constants; native rail integration remains the sole engine.
        double drag=isVehicle()?.996999979019165D:.9599999785423279D;
        setDeltaMovement(getDeltaMovement().multiply(drag,0,drag));
    }
    @Override protected void afterSourceTick(){
        if(fuse>0){--fuse;level().addParticle(ParticleTypes.SMOKE,getX(),getY()+.5D,getZ(),0,0,0);}
        else if(fuse==0)explodeCart(getDeltaMovement().horizontalDistanceSqr());
        if(horizontalCollision){double speed=getDeltaMovement().horizontalDistanceSqr();if(speed>=.009999999776482582D)explodeCart(speed);}
    }
    @Override protected void destroy(DamageSource source){
        // Source always drops the named vanilla cart first, including on explosive/fire destruction.
        super.destroy(source);
        double speed=getDeltaMovement().horizontalDistanceSqr();
        if(!source.is(DamageTypeTags.IS_EXPLOSION))spawnAtLocation(new ItemStack(Items.TNT),0);
        if(source.is(DamageTypeTags.IS_FIRE)||source.is(DamageTypeTags.IS_EXPLOSION)||speed>=.009999999776482582D)explodeCart(speed);
    }
    protected void explodeCart(double speedSquared){
        if(!level().isClientSide){
            double speed=Math.sqrt(speedSquared);if(speed>5D)speed=5D;
            float strength=(float)(4D+random.nextDouble()*1.5D*speed);
            // Old createExplosion(...,true) means terrain destruction, not flaming terrain.
            level().explode(this,getX(),getY(),getZ(),strength,false,Level.ExplosionInteraction.BLOCK);
            discard();
        }
    }
    @Override public boolean causeFallDamage(float distance,float multiplier,DamageSource source){
        if(distance>=3F){float scale=distance/10F;explodeCart(scale*scale);}
        return super.causeFallDamage(distance,multiplier,source);
    }
    @Override public void activateMinecart(int x,int y,int z,boolean powered){if(powered&&fuse<0)ignite();}
    @Override public void handleEntityEvent(byte event){if(event==10)ignite();else super.handleEntityEvent(event);}
    public void ignite(){
        fuse=80;
        if(!level().isClientSide){level().broadcastEntityEvent(this,(byte)10);level().playSound(null,getX(),getY(),getZ(),SoundEvents.TNT_PRIMED,SoundSource.BLOCKS,1,1);}
    }
    @Override public float getBlockExplosionResistance(Explosion explosion,BlockGetter world,BlockPos pos,BlockState state,FluidState fluid,float resistance){
        return isIgnited()&&(state.is(BlockTags.RAILS)||world.getBlockState(pos.above()).is(BlockTags.RAILS))?0:super.getBlockExplosionResistance(explosion,world,pos,state,fluid,resistance);
    }
    @Override public boolean shouldBlockExplode(Explosion explosion,BlockGetter world,BlockPos pos,BlockState state,float strength){
        return !(isIgnited()&&(state.is(BlockTags.RAILS)||world.getBlockState(pos.above()).is(BlockTags.RAILS)))&&super.shouldBlockExplode(explosion,world,pos,state,strength);
    }
    @Override protected void addAdditionalSaveData(CompoundTag tag){super.addAdditionalSaveData(tag);tag.remove("base");tag.putInt("TNTFuse",fuse);}
    @Override protected void readAdditionalSaveData(CompoundTag tag){super.readAdditionalSaveData(tag);if(tag.contains("TNTFuse",99))fuse=tag.getInt("TNTFuse");}
}
