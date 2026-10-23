package com.hbm.machine.turret;
import com.hbm.bomb.nuclear.LegacyVector;
import com.hbm.registry.HbmSounds;
import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.pistols.XFactory9mm;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
/** Original Brown dual barrels, ten aligned-tick firing cadence, casing ejector and physical pivot. */
public class SentryEntity extends TurretBaseEntity {
    public double barrelLeftPos,lastBarrelLeftPos,barrelRightPos,lastBarrelRightPos;
    protected boolean firedLeft,firedRight,retractingLeft,retractingRight,shotSide;protected int firingTimer;
    public SentryEntity(BlockPos pos,BlockState state){this(TurretContent.SENTRY_ENTITY.get(),pos,state);}
    protected SentryEntity(BlockEntityType<?> type,BlockPos pos,BlockState state){super(type,pos,state);}
    public boolean damaged(){return false;}
    @Override protected String title(){return "container.turretSentry";}
    @Override protected List<BulletConfig> ammoConfigs(){return List.of(XFactory9mm.p9_sp,XFactory9mm.p9_fmj,XFactory9mm.p9_jhp,XFactory9mm.p9_ap);}
    public List<net.minecraft.world.item.ItemStack> displayedAmmo(){return ammoConfigs().stream().filter(c->c.ammo!=null).map(c->c.ammo.toStack()).toList();}
    @Override public long getMaxPower(){return 1000;}@Override public long consumption(){return 5;}@Override public double depression(){return 20;}@Override public double elevation(){return 20;}@Override public double detectorRange(){return 24;}@Override public double detectorGrace(){return 2;}@Override public double acceptableInaccuracy(){return 15;}@Override public double barrelLength(){return 1.25;}@Override public boolean thermalVision(){return false;}@Override public Vec3 horizontalOffset(){return new Vec3(.5,0,.5);}@Override protected float baseDamage(){return TurretSettings.brownDamage();}
    @Override public void tickClient(){lastBarrelLeftPos=barrelLeftPos;lastBarrelRightPos=barrelRightPos;if(retractingLeft){barrelLeftPos+=.5F;if(barrelLeftPos>=1)retractingLeft=false;}else barrelLeftPos=Math.max(0,barrelLeftPos-.25F);if(retractingRight){barrelRightPos+=.5F;if(barrelRightPos>=1)retractingRight=false;}else barrelRightPos=Math.max(0,barrelRightPos-.25F);super.tickClient();}
    @Override protected void updateFiringTick(){firingTimer++;if(firingTimer%10!=0)return;var config=firstLoaded();if(config==null)return;cachedCasing=config.casing;spawnBullet(config);consumeAmmo(config);fireSound(1);var muzzle=muzzleOffset();var side=LegacyVector.createVectorHelper(.125*(shotSide?1:-1),0,0);side.rotateAroundY((float)-rotationYaw);smoke(muzzle.xCoord+side.xCoord,muzzle.yCoord,muzzle.zCoord+side.zCoord);markFired();}
    protected void fireSound(float pitch){level.playSound(null,worldPosition.getX(),worldPosition.getY(),worldPosition.getZ(),HbmSounds.get("turret.sentry_fire"),SoundSource.BLOCKS,2,pitch);}
    protected void smoke(double x,double y,double z){if(level instanceof ServerLevel server){var pos=turretPosition();var tag=new CompoundTag();tag.putString("type","smoke");TurretEffects.send(server,worldPosition,pos.x+x,pos.y+y,pos.z+z,tag);}}
    protected void markFired(){if(shotSide)firedLeft=true;else firedRight=true;shotSide=!shotSide;}
    @Override protected Vec3 casingPosition(){var pos=turretPosition();var vector=LegacyVector.createVectorHelper(0,.25,-.125);vector.rotateAroundZ((float)-rotationPitch);vector.rotateAroundY((float)-(rotationYaw+Math.PI*.5));return pos.add(vector.xCoord,vector.yCoord,vector.zCoord);}
    @Override protected void seekNewTarget(){var previous=target;super.seekNewTarget();if(previous!=target&&target!=null)level.playSound(null,target.getX(),target.getY(),target.getZ(),HbmSounds.get("turret.sentry_lockon"),SoundSource.BLOCKS,2,1.5F);}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=super.getUpdateTag(lookup);tag.putBoolean("hbm_fired_left",firedLeft);tag.putBoolean("hbm_fired_right",firedRight);return tag;}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){super.handleUpdateTag(tag,lookup);retractingLeft=tag.getBoolean("hbm_fired_left");retractingRight=tag.getBoolean("hbm_fired_right");}
    @Override protected void afterSync(){firedLeft=false;firedRight=false;}
}
