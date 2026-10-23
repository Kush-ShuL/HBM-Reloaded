package com.hbm.entity.mob.crab;

import com.hbm.damage.HbmDamageSources;
import com.hbm.damage.mixin.LivingDamageAccess;
import com.hbm.weapon.sedna.ShotSensitiveBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;

/** Sole actual old EntityBullet kernel shared by canonical legacy bullet, CyberCrab and HunterChopper.
 * Source constructors/flags/hit callbacks use this same flight path; source unsaved fields stay unsaved. */
public class CyberTauBullet extends Entity {
    private static final EntityDataAccessor<Boolean> CRITICAL=SynchedEntityData.defineId(CyberTauBullet.class,EntityDataSerializers.BOOLEAN),TAU=SynchedEntityData.defineId(CyberTauBullet.class,EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> CHOPPER=SynchedEntityData.defineId(CyberTauBullet.class,EntityDataSerializers.BOOLEAN);
    public Entity shootingEntity;private BlockPos tile;private BlockState tileState;private boolean inGround,largeSourceBody;private int groundTicks,airTicks,shake,pickup;
    public double damage,gravity;
    public boolean antidote,pip,fire;
    private boolean sourceInstakill,sourceRadiation;private int sourceKnockback,sourceInData;
    public CyberTauBullet(EntityType<? extends CyberTauBullet> type,Level world){super(type,world);}
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder){builder.define(CRITICAL,false);builder.define(TAU,false);builder.define(CHOPPER,false);}
    public boolean chopper(){return entityData.get(CHOPPER);}
    public boolean critical(){return entityData.get(CRITICAL);}public boolean tau(){return entityData.get(TAU);}
    @Override public EntityDimensions getDimensions(Pose pose){return largeSourceBody?EntityDimensions.scalable(.6F,1.8F):super.getDimensions(pose);}
    protected final void heading(double x,double y,double z,float velocity,float spread){sourceHeading(x,y,z,velocity,spread,.007499999832361937D);}
    protected final void sourceHeading(double x,double y,double z,float velocity,float spread,double variance){float norm=(float)Math.sqrt(x*x+y*y+z*z);x/=norm;y/=norm;z/=norm;x+=random.nextGaussian()*(random.nextBoolean()?-1:1)*variance*spread;y+=random.nextGaussian()*(random.nextBoolean()?-1:1)*variance*spread;z+=random.nextGaussian()*(random.nextBoolean()?-1:1)*variance*spread;setDeltaMovement(x*velocity,y*velocity,z*velocity);rotationFromMotion();groundTicks=0;}
    private void rotationFromMotion(){var motion=getDeltaMovement();yRotO=(float)(Math.atan2(motion.x,motion.z)*180/Math.PI);xRotO=(float)(Math.atan2(motion.y,(float)Math.sqrt(motion.x*motion.x+motion.z*motion.z))*180/Math.PI);setYRot(yRotO);setXRot(xRotO);}
    @Override public void lerpMotion(double x,double y,double z){setDeltaMovement(x,y,z);if(xRotO==0&&yRotO==0){rotationFromMotion();groundTicks=0;}}
    @Override public void tick(){if(!sourceAreaReady())return;super.tick();var world=level();var motion=getDeltaMovement();if(xRotO==0&&yRotO==0){yRotO=(float)(Math.atan2(motion.x,motion.z)*180/Math.PI);setYRot(yRotO);}if(tile!=null){var state=world.getBlockState(tile);if(!state.isAir()){if(!critical()&&state.getCollisionShape(world,tile).toAabbs().stream().anyMatch(box->box.move(tile).contains(position())))inGround=true;if(state.getBlock() instanceof ShotSensitiveBlock sensitive)sensitive.onShot(world,tile);if(glass(state.getBlock())){world.setBlockAndUpdate(tile,Blocks.AIR.defaultBlockState());world.playLocalSound(tile.getX(),tile.getY(),tile.getZ(),SoundEvents.GLASS_BREAK,SoundSource.BLOCKS,1,1,true);}}}if(shake>0)shake--;if(inGround&&!critical()){discard();return;}airTicks++;
        Vec3 start=position(),end=start.add(motion);var block=world.clip(new ClipContext(start,end,ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,this));HitResult impact=block.getType()==HitResult.Type.MISS?null:block;Vec3 collisionEnd=impact==null?end:impact.getLocation();Entity target=null;double best=0;
        for(var entity:world.getEntities(this,getBoundingBox().expandTowards(motion).inflate(1))){if(!entity.isPickable()||(entity==shootingEntity&&airTicks<5))continue;var hit=sourceIntercept(entity.getBoundingBox().inflate((double).3F),start,collisionEnd);if(hit!=null){double distance=start.distanceTo(hit);if(best==0||distance<best){target=entity;best=distance;}}}
        if(target!=null)impact=new EntityHitResult(target);if(target instanceof Player player&&(player.getAbilities().invulnerable||shootingEntity instanceof Player owner&&!owner.canHarmPlayer(player)))impact=null;
        if(impact instanceof EntityHitResult hit)hitEntity(hit.getEntity());else if(impact instanceof BlockHitResult hit&&!critical()){tile=hit.getBlockPos();tileState=world.getBlockState(tile);var meta=tileState.getBlock().getStateDefinition().getProperty("legacy_meta");sourceInData=meta instanceof net.minecraft.world.level.block.state.properties.IntegerProperty integer?tileState.getValue(integer):0;var delta=hit.getLocation().subtract(position());setDeltaMovement((float)delta.x,(float)delta.y,(float)delta.z);motion=getDeltaMovement();float length=(float)motion.length();setPos(position().subtract(motion.scale(.05000000074505806D/length)));inGround=true;shake=7;if(!tileState.isAir())tileState.entityInside(world,tile,this);}
        motion=getDeltaMovement();if(critical()&&world.isClientSide)for(int i=0;i<8;i++){var pos=position().add(motion.scale(i/8D));com.hbm.client.entity.crab.CrabParticles.trail(this,pos,tau());}
        setPos(position().add(motion));setYRot((float)(Math.atan2(motion.x,motion.z)*180/Math.PI));float drag=.99F;if(isInWater()){for(int i=0;i<4;i++)world.addParticle(ParticleTypes.BUBBLE,getX()-motion.x*.25,getY()-motion.y*.25,getZ()-motion.z*.25,motion.x,motion.y,motion.z);drag=.8F;}if(isInWaterOrRain())clearFire();var damped=motion.scale(drag);setDeltaMovement(damped.x,damped.y-gravity,damped.z);checkInsideBlocks();if(tickCount>250)discard();}
    /** Old AxisAlignedBB.calculateIntercept tests both planes, including exits when the ray starts inside. */
    public static Vec3 sourceIntercept(AABB box,Vec3 start,Vec3 end){Vec3 best=null;double distance=Double.MAX_VALUE;double[] min={box.minX,box.minY,box.minZ},max={box.maxX,box.maxY,box.maxZ},from={start.x,start.y,start.z},delta={end.x-start.x,end.y-start.y,end.z-start.z};for(int axis=0;axis<3;axis++){if(delta[axis]*delta[axis]<1.0000000116860974E-7D)continue;for(int side=0;side<2;side++){double t=((side==0?min[axis]:max[axis])-from[axis])/delta[axis];if(t<0||t>1)continue;var point=start.add(end.subtract(start).scale(t));double[] values={point.x,point.y,point.z};int a=(axis+1)%3,b=(axis+2)%3;if(values[a]<min[a]||values[a]>max[a]||values[b]<min[b]||values[b]>max[b])continue;double next=start.distanceToSqr(point);if(next<distance){distance=next;best=point;}}}return best;}
    @Override public boolean shouldRenderAtSqrDistance(double distance){double span=getBoundingBox().getSize()*4;if(Double.isNaN(span))span=4;span*=64*10;return distance<span*span;}
    private static boolean glass(Block block){return block==Blocks.GLASS||block==Blocks.GLASS_PANE||block instanceof StainedGlassBlock||block instanceof StainedGlassPaneBlock;}
    private void hitEntity(Entity target){if(target instanceof ItemFrame frame&&!frame.getItem().isEmpty()&&BuiltInRegistries.ITEM.getKey(frame.getItem().getItem()).equals(ResourceLocation.parse("hbm:flame_pony"))){discard();return;}int criticalDamage=Mth.ceil((float)getDeltaMovement().length()*damage);if(critical())criticalDamage+=random.nextInt(criticalDamage/2+2);var source=chopper()&&!critical()?HbmDamageSources.causeDisplacementDamage(this,shootingEntity==null?this:shootingEntity):critical()&&!chopper()?HbmDamageSources.causeTauDamage(this,shootingEntity==null?this:shootingEntity):!critical()&&!chopper()?HbmDamageSources.causeBulletDamage(this,shootingEntity==null?this:shootingEntity):null;if(fire||isOnFire()&&!(target instanceof EnderMan))target.igniteForSeconds(5);if(target.hurt(source,(float)damage)){sourceHitSucceeded(target,source);}else if(target instanceof LivingEntity living&&living instanceof LivingDamageAccess access)living.hurt(source,(float)damage+access.hbm$getLastHurt());}
    @Override protected void addAdditionalSaveData(CompoundTag tag){tag.putShort("xTile",(short)(tile==null?-1:tile.getX()));tag.putShort("yTile",(short)(tile==null?-1:tile.getY()));tag.putShort("zTile",(short)(tile==null?-1:tile.getZ()));tag.putBoolean("hasSourceTile",tile!=null);if(tileState!=null)tag.put("sourceTileState",net.minecraft.nbt.NbtUtils.writeBlockState(tileState));tag.putByte("inData",(byte)sourceInData);tag.putShort("life",(short)groundTicks);tag.putByte("shake",(byte)shake);tag.putBoolean("inGround",inGround);tag.putByte("pickup",(byte)pickup);tag.putDouble("damage",damage);}
    @Override protected void readAdditionalSaveData(CompoundTag tag){if(tag.getBoolean("hasSourceTile"))tile=new BlockPos(tag.getShort("xTile"),tag.getShort("yTile"),tag.getShort("zTile"));tileState=tag.contains("sourceTileState",10)?net.minecraft.nbt.NbtUtils.readBlockState(level().holderLookup(net.minecraft.core.registries.Registries.BLOCK),tag.getCompound("sourceTileState")):Blocks.AIR.defaultBlockState();sourceInData=tag.getByte("inData")&255;groundTicks=tag.getShort("life");shake=tag.getByte("shake")&255;inGround=tag.getBoolean("inGround");pickup=tag.getByte("pickup");if(tag.contains("damage"))damage=tag.getDouble("damage");}
    @Override public void playerTouch(Player player){if(!level().isClientSide&&inGround&&shake<=0&&(pickup==1||pickup==2&&player.getAbilities().instabuild)){player.take(this,1);discard();}}
    /** Actual source constructors and both real mob producers use the canonical bullet's one kernel. */
    protected final void initializeSourceTarget(LivingEntity owner,LivingEntity target,float velocity,float spread){
        shootingEntity=owner;largeSourceBody=true;refreshDimensions();if(owner instanceof Player)pickup=1;
        double y=owner.getY()+owner.getEyeHeight()-.10000000149011612D,dx=target.getX()-owner.getX(),dy=target.getBoundingBox().minY+target.getBbHeight()/3F-y,dz=target.getZ()-owner.getZ();
        float horizontal=(float)Math.sqrt(dx*dx+dz*dz);setPos(0,y,0);
        if(horizontal>=1.0E-7D){setPos(owner.getX()+dx/horizontal,y,owner.getZ()+dz/horizontal);setYRot((float)(Math.atan2(dz,dx)*180/Math.PI)-90);setXRot((float)-(Math.atan2(dy,horizontal)*180/Math.PI));heading(dx,dy,dz,velocity,spread);}
    }
    protected final void initializeSourceOwner(LivingEntity owner,float velocity,boolean wide){
        shootingEntity=owner;if(owner instanceof Player)pickup=1;
        if(owner!=null){setPos(owner.getX(),owner.getY()+owner.getEyeHeight(),owner.getZ());setYRot(owner.getYRot());setXRot(owner.getXRot());}
        setPos(getX()-Mth.cos(getYRot()/(float)180*(float)Math.PI)*.16F,getY()-.10000000149011612D,getZ()-Mth.sin(getYRot()/(float)180*(float)Math.PI)*.16F);
        double x=-Mth.sin(getYRot()/(float)180*(float)Math.PI)*Mth.cos(getXRot()/(float)180*(float)Math.PI),z=Mth.cos(getYRot()/(float)180*(float)Math.PI)*Mth.cos(getXRot()/(float)180*(float)Math.PI),y=-Mth.sin(getXRot()/(float)180*(float)Math.PI);
        sourceHeading(x,y,z,velocity*1.5F,1F,wide?.042499999832361937D:.007499999832361937D);
    }
    protected final void sourceLargeBody(){largeSourceBody=true;refreshDimensions();}
    protected final void sourceHitFlags(boolean instant,boolean radiation){sourceInstakill=instant;sourceRadiation=radiation;}
    public void setIsCritical(boolean value){entityData.set(CRITICAL,value);}
    public void setTau(boolean value){entityData.set(TAU,value);}
    public void setChopper(boolean value){entityData.set(CHOPPER,value);}
    public boolean getIsCritical(){return critical();}public boolean getIsTau(){return tau();}public boolean getIsChopper(){return chopper();}
    public void setDamage(double value){damage=value;}public double getDamage(){return damage;}
    public void setKnockbackStrength(int value){sourceKnockback=value;}
    public int getArrowShake(){return shake;}public void setArrowShake(int value){shake=value;}
    public int getCanBePickedUp(){return pickup;}public void setCanBePickedUp(int value){pickup=value;}
    public void setThrowableHeading(double x,double y,double z,float velocity,float spread){heading(x,y,z,velocity,spread);}
    public void setThrowableHeading2(double x,double y,double z,float velocity,float spread){sourceHeading(x,y,z,velocity,spread,.042499999832361937D);}

    private boolean sourceAreaReady(){
        if(!sourceFull(getBoundingBox().expandTowards(getDeltaMovement()).inflate(1)))return false;
        return tile==null||sourceFull(new AABB(tile).inflate(1));
    }
    private boolean sourceFull(AABB box){
        for(int x=Mth.floor(box.minX)>>4;x<=Mth.floor(box.maxX)>>4;x++)for(int z=Mth.floor(box.minZ)>>4;z<=Mth.floor(box.maxZ)>>4;z++){
            if(level() instanceof ServerLevel server){if(server.getChunkSource().getChunkNow(x,z)==null)return false;}
            else if(!level().hasChunkAt(new BlockPos(x<<4,0,z<<4)))return false;
        }
        return true;
    }

    private void sourceHitSucceeded(Entity target,net.minecraft.world.damagesource.DamageSource source){
        if(target instanceof LivingEntity living){
            if(sourceRadiation&&!com.hbm.exposure.HbmExposure.spectatorRadiationExempt(living)){
                if(living instanceof Player&&com.hbm.bomb.conventional.MistExposure.checkForHazmat(living)){
                    // Actual old ArmorUtil suit+mutation predicate, already shared by source fluids/mist.
                }else if(living instanceof net.minecraft.world.entity.monster.Creeper){
                    if(level() instanceof ServerLevel server&&!living.isRemoved()){
                        var replacement=com.hbm.entity.mob.sourcecreepers.SourceCreeperContent.NUCLEAR.get().create(server);
                        if(replacement==null)throw new IllegalStateException("Actual nuclear creeper type unavailable");
                        replacement.moveTo(living.getX(),living.getY(),living.getZ(),living.getYRot(),living.getXRot());server.addFreshEntity(replacement);
                    }
                    living.discard();
                }else if(living instanceof net.minecraft.world.entity.npc.Villager){
                    if(level() instanceof ServerLevel server){
                        var replacement=EntityType.ZOMBIE.create(server);
                        if(replacement==null)throw new IllegalStateException("Native source zombie type unavailable");
                        replacement.moveTo(living.getX(),living.getY(),living.getZ(),living.getYRot(),living.getXRot());
                        living.discard();server.addFreshEntity(replacement);
                    }else living.discard();
                }else if(!(living instanceof com.hbm.entity.mob.EntityCreeperNuclear)&&!(living instanceof net.minecraft.world.entity.animal.MushroomCow)&&!(living instanceof net.minecraft.world.entity.monster.Zombie)){
                    living.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.POISON,2400,2));
                    living.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.WITHER,20,4));
                    living.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN,1200,1));
                }
            }
            if(antidote)living.removeAllEffects();
            if(sourceKnockback>0){var motion=getDeltaMovement();float horizontal=(float)Math.sqrt(motion.x*motion.x+motion.z*motion.z);if(horizontal>0)living.push(motion.x*sourceKnockback*.6000000238418579D/horizontal,.1D,motion.z*sourceKnockback*.6000000238418579D/horizontal);}
            if(shootingEntity instanceof LivingEntity&&level() instanceof ServerLevel server)EnchantmentHelper.doPostAttackEffects(server,target,source);
            if(shootingEntity!=null&&target!=shootingEntity&&target instanceof Player&&shootingEntity instanceof net.minecraft.server.level.ServerPlayer serverPlayer)serverPlayer.connection.send(new net.minecraft.network.protocol.game.ClientboundGameEventPacket(net.minecraft.network.protocol.game.ClientboundGameEventPacket.ARROW_HIT_PLAYER,0));
        }
        if(!(target instanceof EnderMan)&&!level().isClientSide&&sourceInstakill&&!(target instanceof Player)&&target instanceof LivingEntity living)living.setHealth(0);
        // Source's successful normal-hit discard is commented out; never add it here.
    }

    @Override public boolean isAttackable(){return false;}
}
