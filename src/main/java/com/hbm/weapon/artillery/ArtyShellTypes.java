package com.hbm.weapon.artillery;
import com.hbm.bomb.conventional.LargeConventionalEffects;
import com.hbm.bomb.conventional.grenade.GrenadeParticleEffects;
import com.hbm.bomb.nuclear.*;
import com.hbm.entity.effect.EntityMist;
import com.hbm.explosion.*;
import com.hbm.explosion.vanillant.*;
import com.hbm.explosion.vanillant.standard.*;
import com.hbm.fluid.HbmFluids;
import com.hbm.item.medicine.MedicineEffects;
import com.hbm.pollution.*;
import com.hbm.registry.HbmSounds;
import com.hbm.weapon.sedna.gun.particle.SpentCasing;
import com.hbm.weapon.sedna.gun.particle.SpentCasing.CasingType;
import com.hbm.world.structure.dependencies.materials.StructureMaterialsContent;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.network.PacketDistributor;
/** All twelve actual ItemAmmoArty effect definitions, including the original cluster, gas and cargo branches. */
public final class ArtyShellTypes {
    public static final int NORMAL=0,CLASSIC=1,EXPLOSIVE=2,MINI_NUKE=3,NUKE=4,PHOSPHORUS=5,MINI_NUKE_MULTI=6,PHOSPHORUS_MULTI=7,CARGO=8,CHLORINE=9,PHOSGENE=10,MUSTARD=11;
    public static final String[] NAMES={"ammo_arty","ammo_arty_classic","ammo_arty_he","ammo_arty_mini_nuke","ammo_arty_nuke","ammo_arty_phosphorus","ammo_arty_mini_nuke_multi","ammo_arty_phosphorus_multi","ammo_arty_cargo","ammo_arty_chlorine","ammo_arty_phosgene","ammo_arty_mustard_gas"};
    public static final Random RANDOM=new Random();private static final SpentCasing[] CASINGS=new SpentCasing[12];
    public static int safeType(int value){return value==Integer.MIN_VALUE?0:Math.abs(value)%12;}
    public static synchronized SpentCasing casing(int type){type=safeType(type);if(CASINGS[type]==null){int color=switch(type){case PHOSPHORUS,PHOSPHORUS_MULTI->SpentCasing.COLOR_CASE_16INCH_PHOS;case MINI_NUKE,NUKE,MINI_NUKE_MULTI,PHOSGENE,MUSTARD->SpentCasing.COLOR_CASE_16INCH_NUKE;default->SpentCasing.COLOR_CASE_16INCH;};CASINGS[type]=new SpentCasing(CasingType.STRAIGHT).setScale(15F,15F,10F).setupSmoke(1F,1D,200,60).setMaxAge(300).setBounceMotion(1F,.5F).register(NAMES[type]).setColor(color);}return CASINGS[type];}
    private static Vec3 behind(ArtilleryShellEntity shell,HitResult hit){var motion=shell.getDeltaMovement();var normal=LegacyVector.createVectorHelper(motion.x,motion.y,motion.z).normalize();return hit.getLocation().add(-normal.xCoord,-normal.yCoord,-normal.zCoord);}
    public static void standardExplosion(ArtilleryShellEntity shell,HitResult hit,float size,float range,boolean breakBlocks){var pos=behind(shell,hit);var explosion=new ExplosionVNT(shell.level(),pos.x,pos.y,pos.z,size);if(breakBlocks){explosion.setBlockAllocator(new BlockAllocatorStandard(48));explosion.setBlockProcessor(new BlockProcessorStandard().setNoDrop().withBlockEffect(new ArtyDebrisMutator(StructureMaterialsContent.SLAG.get().fromLegacy(1))));}explosion.setEntityProcessor(new EntityProcessorCross(7.5D).withRangeMod(range));explosion.setPlayerProcessor(new PlayerProcessorStandard());explosion.explode();shell.killAndClear();}
    private static void visual(Level level,Vec3 pos,boolean small,boolean debris){var packet=small?new LargeConventionalEffects(pos.x,pos.y,pos.z,10,2,.5F,25,5,0,20,.75F,1,-2,150):new LargeConventionalEffects(pos.x,pos.y,pos.z,15,5,1,45,10,debris?16:0,50,1,3,-2,200);if(level instanceof ServerLevel server)for(var player:server.players())if(player.distanceToSqr(pos.x,pos.y,pos.z)<=90000)PacketDistributor.sendToPlayer(player,packet);}
    public static void impact(int type,ArtilleryShellEntity shell,HitResult hit){var level=shell.level();var pos=hit.getLocation();switch(type){
        case NORMAL->{standardExplosion(shell,hit,10,3,false);visual(level,pos,true,false);}
        case CLASSIC->{standardExplosion(shell,hit,15,5,false);visual(level,pos,false,false);}
        case EXPLOSIVE->{standardExplosion(shell,hit,15,3,true);visual(level,pos,false,true);}
        case MINI_NUKE,MINI_NUKE_MULTI->{shell.killAndClear();var point=behind(shell,hit);ExplosionNukeSmall.explode(level,point.x,point.y,point.z,ExplosionNukeSmall.PARAMS_MEDIUM);}
        case NUKE->{level.addFreshEntity(NuclearExplosionMK5.statFac(level,NuclearConfig.radius("missile"),pos.x,pos.y,pos.z));NuclearTorex.statFacStandard(level,pos.x,pos.y,pos.z,NuclearConfig.radius("missile"));shell.setDead();}
        case PHOSPHORUS,PHOSPHORUS_MULTI->{level.playSound(null,shell.getX(),shell.getY(),shell.getZ(),HbmSounds.get("weapon.explosionMedium"),SoundSource.NEUTRAL,20,.9F+RANDOM.nextFloat()*.2F);standardExplosion(shell,hit,10,3,false);ExplosionLarge.spawnShrapnels(level,(int)pos.x,(int)pos.y,(int)pos.z,15);ExplosionChaos.igniteAllBlocks(level,(int)pos.x,(int)pos.y,(int)pos.z,12);for(var entity:level.getEntities(shell,new AABB(shell.getX()-15,shell.getY()-15,shell.getZ()-15,shell.getX()+15,shell.getY()+15,shell.getZ()+15))){entity.igniteForSeconds(5);if(entity instanceof LivingEntity living){var effect=new net.minecraft.world.effect.MobEffectInstance(MedicineEffects.PHOSPHORUS,600,0,true,true);effect.getCures().clear();living.addEffect(effect);}}for(int i=0;i<5;i++)GrenadeParticleEffects.sendHaze(level,pos.x+level.random.nextGaussian()*10,pos.y,pos.z+level.random.nextGaussian()*10,shell.getX(),shell.getY(),shell.getZ());RbmkMushEffects.sendAtOrigin(level,pos.x,pos.y,pos.z,10,shell.getX(),shell.getY(),shell.getZ());}
        case CARGO->{if(hit instanceof BlockHitResult block){shell.setPos(pos);var bp=block.getBlockPos();shell.getStuck(bp.getX(),bp.getY(),bp.getZ(),block.getDirection().get3DDataValue());}}
        case CHLORINE,PHOSGENE,MUSTARD->{shell.killAndClear();var point=behind(shell,hit);level.explode(shell,point.x,point.y,point.z,5,false,Level.ExplosionInteraction.NONE);int count=type==CHLORINE?1:type==PHOSGENE?3:5;for(int i=0;i<count;i++){double x=point.x,z=point.z;if(i>0){x+=RANDOM.nextGaussian()*(type==MUSTARD?25:15);z+=RANDOM.nextGaussian()*(type==MUSTARD?25:15);}var mist=new EntityMist(level).setType(type==CHLORINE?HbmFluids.CHLORINE:type==PHOSGENE?HbmFluids.PHOSGENE:HbmFluids.MUSTARDGAS).setArea(type==MUSTARD?20:15,type==CHLORINE?7.5F:10);mist.setPos(x,point.y-(type==CHLORINE?3:5),z);level.addFreshEntity(mist);}if(level instanceof ServerLevel server){var bp=hit instanceof BlockHitResult block?block.getBlockPos():BlockPos.ZERO;PollutionSystem.increment(server,bp,PollutionType.HEAVYMETAL,type==CHLORINE?5:type==PHOSGENE?10:15);if(type!=CHLORINE)PollutionSystem.increment(server,bp,PollutionType.POISON,type==PHOSGENE?15:30);}}
        default->throw new IllegalArgumentException("Unknown real artillery shell "+type);
    }}
    public static void update(int type,ArtilleryShellEntity shell){if(type==PHOSPHORUS_MULTI)cluster(shell,PHOSPHORUS,10,300,5);if(type==MINI_NUKE_MULTI)cluster(shell,MINI_NUKE,5,300,5);}
    private static void cluster(ArtilleryShellEntity shell,int type,int amount,double splitHeight,double deviation){if(!shell.shouldWhistle||shell.getDeltaMovement().y>0||shell.targetY+splitHeight<shell.getY())return;shell.killAndClear();NuclearPlasmaBlastEffects.send(shell.level(),shell.getX(),shell.getY(),shell.getZ(),1,1,1,50,0,0,500);var velocity=shell.getDeltaMovement();for(int i=0;i<amount;i++){var child=new ArtilleryShellEntity(shell.level()).shellType(type);child.setDeltaMovement(i==0?velocity.x:velocity.x+RANDOM.nextGaussian()*deviation,velocity.y,i==0?velocity.z:velocity.z+RANDOM.nextGaussian()*deviation);child.moveTo(shell.getX(),shell.getY(),shell.getZ(),shell.getYRot(),shell.getXRot());child.targetX=shell.targetX;child.targetY=shell.targetY;child.targetZ=shell.targetZ;child.shouldWhistle=shell.shouldWhistle&&!shell.didWhistle;shell.level().addFreshEntity(child);}}
    private ArtyShellTypes(){}
}
