package com.hbm.entity.projectile;
import com.hbm.entity.LegacyTrackingRange;
import com.hbm.entity.mob.glyphid.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
/** Real source acid projectile, retaining the sole EntityThrowableInterp flight/thrower and friendly-Glyphid passthrough. */
public final class EntityAcidBomb extends EntityThrowableInterp implements LegacyTrackingRange,ItemSupplier {
    public float damage=1.5F;
    public EntityAcidBomb(EntityType<? extends EntityAcidBomb> type,Level level){super(type,level);}
    public EntityAcidBomb(Level level,double x,double y,double z){this(GlyphidContent.ACID.get(),level);setPos(x,y,z);}
    @Override public int legacyTrackingRange(){return 1000;}
    @Override public ItemStack getItem(){return new ItemStack(Items.SLIME_BALL);}
    @Override protected void onImpact(HitResult hit){
        if(level().isClientSide)return;
        if(hit instanceof EntityHitResult entity&&!(entity.getEntity() instanceof EntityGlyphid)){
            var type=level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE,ResourceLocation.parse("hbm:acidplayer")));
            entity.getEntity().hurt(new DamageSource(type,this,getThrower()),damage);discard();
        }
        if(hit instanceof BlockHitResult)discard();
    }
    @Override public double getGravityVelocity(){return .04D;}
    @Override protected float getAirDrag(){return 1;}
    @Override protected void addAdditionalSaveData(CompoundTag tag){super.addAdditionalSaveData(tag);tag.putFloat("damage",damage);}
    @Override protected void readAdditionalSaveData(CompoundTag tag){super.readAdditionalSaveData(tag);damage=tag.getFloat("damage");}
}
