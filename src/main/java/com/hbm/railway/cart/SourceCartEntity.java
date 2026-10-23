package com.hbm.railway.cart;
import com.hbm.entity.LegacyTrackingRange;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
/** Actual source NTM minecart base, using the sole native/NeoForge rail integrator. */
public abstract class SourceCartEntity extends AbstractMinecart implements LegacyTrackingRange {
    private static final EntityDataAccessor<Integer> BASE=SynchedEntityData.defineId(SourceCartEntity.class,EntityDataSerializers.INT);
    public final SourceCartKind kind;
    protected SourceCartEntity(EntityType<?> type,Level level,SourceCartKind kind){super(type,level);this.kind=kind;}
    @Override protected void defineSynchedData(SynchedEntityData.Builder data){super.defineSynchedData(data);data.define(BASE,0);}
    public void setBase(SourceCartKind.Base base){entityData.set(BASE,base.ordinal());}
    public SourceCartKind.Base getBase(){return SourceCartKind.Base.safe(entityData.get(BASE));}
    @Override public Type getMinecartType(){return Type.RIDEABLE;}
    // Native enum has no source -1. Both semantic hooks are explicit: no automatic passenger or powered-cart branch.
    @Override public boolean canBeRidden(){return false;}
    @Override public boolean isPoweredCart(){return false;}
    @Override public boolean canBeCollidedWith(){return true;}
    @Override public boolean canCollideWith(Entity other){return !isPassengerOfSameVehicle(other);}
    public ItemStack getCartItem(){return SourceCartItem.stack(getBase(),kind);}
    @Override public ItemStack getPickResult(){return getCartItem();}
    @Override protected Item getDropItem(){return SourceCartContent.CART.get();}
    /** Official1.7.10 xl.a(DamageSource,float): named carts still drop when creative destroys them. */
    @Override public boolean hurt(DamageSource source,float amount){
        if(level().isClientSide||isRemoved())return true;if(isInvulnerableTo(source))return false;
        setHurtDir(-getHurtDir());setHurtTime(10);markHurt();setDamage(getDamage()+amount*10F);
        boolean creative=source.getEntity() instanceof net.minecraft.world.entity.player.Player player&&player.getAbilities().instabuild;
        if(creative||getDamage()>40F){ejectPassengers();if(creative&&!hasCustomName())discard();else destroy(source);}return true;
    }
    @Override protected void destroy(DamageSource source){discard();var item=getCartItem();if(hasCustomName())item.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,getCustomName());spawnAtLocation(item,0);}
    @Override protected void addAdditionalSaveData(CompoundTag tag){super.addAdditionalSaveData(tag);tag.putInt("base",entityData.get(BASE));}
    @Override protected void readAdditionalSaveData(CompoundTag tag){super.readAdditionalSaveData(tag);entityData.set(BASE,tag.getInt("base"));}
    public static boolean ready(Level level,AABB box){
        for(int cx=Mth.floor(box.minX)>>4;cx<=Mth.floor(box.maxX)>>4;cx++)for(int cz=Mth.floor(box.minZ)>>4;cz<=Mth.floor(box.maxZ)>>4;cz++){
            if(level instanceof ServerLevel server){if(server.getChunkSource().getChunkNow(cx,cz)==null)return false;}
            else if(!level.hasChunkAt(new BlockPos(cx<<4,0,cz<<4)))return false;
        }return true;
    }
    @Override public void tick(){if(!ready(level(),getBoundingBox().expandTowards(getDeltaMovement()).inflate(2,1,2)))return;super.tick();afterSourceTick();}
    protected void afterSourceTick(){}
    @Override public Vec3 getPos(double x,double y,double z){return ready(level(),new AABB(x-2,y-1,z-2,x+2,y+1,z+2))?super.getPos(x,y,z):null;}
    @Override public Vec3 getPosOffs(double x,double y,double z,double offset){return ready(level(),new AABB(x-2,y-1,z-2,x+2,y+1,z+2))?super.getPosOffs(x,y,z,offset):null;}
    @Override public int legacyTrackingRange(){return 250;}
}
