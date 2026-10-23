package com.hbm.bomb.conventional.grenade;

import com.hbm.bomb.conventional.ConventionalContent;
import com.hbm.entity.projectile.EntityThrowableInterp;
import com.hbm.registry.HbmSounds;
import net.minecraft.network.syncher.*;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

/** Source EntityGrenadeUniversal: shared true NT interpolation, source bounces, triggers, spin and triplet trail. */
public final class UniversalGrenade extends EntityThrowableInterp {
    private static final EntityDataAccessor<ItemStack> GRENADE=SynchedEntityData.defineId(UniversalGrenade.class,EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Integer> BOUNCES=SynchedEntityData.defineId(UniversalGrenade.class,EntityDataSerializers.INT),TRAIL=SynchedEntityData.defineId(UniversalGrenade.class,EntityDataSerializers.INT);
    public double previousSpin,spin;
    public UniversalGrenade(EntityType<? extends UniversalGrenade> type,Level level){super(type,level);}
    public UniversalGrenade(Level level,ItemStack grenade){this(GrenadeContent.UNIVERSAL_ENTITY.get(),level);var copy=grenade.copy();copy.setCount(1);entityData.set(GRENADE,copy);}
    public UniversalGrenade(Level level,Player player,ItemStack grenade){this(level,grenade);setThrower(player);var offset=GrenadeParts.rotateY(new Vec3(.25,-.25,0),-player.getYRot()+180);setPos(player.getX()+offset.x,player.getY()+player.getEyeHeight()+offset.y,player.getZ()+offset.z);var motion=player.getLookAngle().normalize();setThrowableHeading(motion.x,motion.y,motion.z,(float)shell().yeet,0);}
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder){super.defineSynchedData(builder);builder.define(GRENADE,ItemStack.EMPTY);builder.define(BOUNCES,0);builder.define(TRAIL,0);}
    public UniversalGrenade trail(int trail){entityData.set(TRAIL,trail);return this;}
    public ItemStack grenade(){return entityData.get(GRENADE);}
    public int bounces(){return entityData.get(BOUNCES);}
    public GrenadeParts.Shell shell(){return UniversalGrenadeItem.shell(grenade());}
    public GrenadeParts.Filling filling(){return UniversalGrenadeItem.filling(grenade());}
    public GrenadeParts.Fuze fuze(){return UniversalGrenadeItem.fuze(grenade());}
    public GrenadeParts.Extra extra(){return UniversalGrenadeItem.extra(grenade());}
    public int timer(){return ticksInAir+ticksInGround;}
    @Override public void tick(){
        super.tick();fuze().tick(this);var extra=extra();if(extra!=null)extra.tick(this);
        if(level().isClientSide){previousSpin=spin;if(bounces()<=0)spin+=15;else spin+=Math.min(15,new Vec3(xOld-getX(),0,zOld-getZ()).length()*50);if(spin>=360){previousSpin-=360;spin-=360;}if(entityData.get(TRAIL)==1)com.hbm.client.bomb.conventional.grenade.GrenadeParticleEffects.flame(level(),getX(),getY(),getZ());}
    }
    @Override protected void onImpact(HitResult hit){
        fuze().impact(this,hit);var extra=extra();if(extra!=null)extra.impact(this,hit);if(isRemoved())return;
        if(hit instanceof BlockHitResult block){var side=block.getDirection();var offset=Vec3.atLowerCornerOf(side.getNormal()).scale(.05);setPos(hit.getLocation().add(offset));var motion=getDeltaMovement();if(motion.length()>.2)level().playSound(null,getX(),getY(),getZ(),HbmSounds.get("weapon.grenadeBounce"),SoundSource.NEUTRAL,1,1);double bounce=shell().bounce;setDeltaMovement(motion.multiply(side.getStepX()!=0?-bounce:.8,side.getStepY()!=0?-bounce:.8,side.getStepZ()!=0?-bounce:.8));forceTeleport();entityData.set(BOUNCES,bounces()+1);}
    }
    public void explode(){discard();GrenadeEffects.explode(filling(),this);var extra=extra();if(extra!=null)extra.explode(this);if(ConventionalContent.EXTENDED_LOGGING.getAsBoolean())org.slf4j.LoggerFactory.getLogger("hbm").info("[GREN] Set off grenade at {} / {} / {} by {}!",(int)getX(),(int)getY(),(int)getZ(),getThrower() instanceof Player player?player.getDisplayName().getString():"null");}
    @Override protected int groundDespawn(){return 0;}
    @Override public boolean fullBlockCollisions(){return true;}
}
