package com.hbm.bomb.conventional.grenade;

import com.hbm.LegacyStartup;
import com.hbm.bomb.conventional.ConventionalRadiation;
import com.hbm.bomb.nuclear.*;
import com.hbm.damage.DamageResistanceHandler.DamageClass;
import com.hbm.entity.projectile.*;
import com.hbm.explosion.ExplosionEmp;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.*;
import com.hbm.registry.HbmSounds;
import com.hbm.weapon.sedna.BulletConfig;
import java.util.Collections;
import net.minecraft.core.Direction;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.*;

/** Original thirteen ItemGrenadeFilling callbacks, including real SEDNA pellets/beams and nuclear engines. */
public final class GrenadeEffects {
    private static BulletConfig fragmentation,pellets,heavy,laser;
    private GrenadeEffects(){}
    public static void init(){if(fragmentation!=null)return;
        fragmentation=new BulletConfig("hbm:grenade_fragmentation").setLife(3).setThresholdNegation(5).setRicochetAngle(90).setRicochetCount(2);
        pellets=new BulletConfig("hbm:grenade_pellets").setLife(100).setGrav(.04).setVel(1.5F).setOnImpact((bullet,hit)->{if(bullet.tickCount<2)return;pelletExplode(bullet,hit,1.5F,true);bullet.setDead();});
        heavy=new BulletConfig("hbm:grenade_heavy_pellets").setLife(100).setGrav(.04).setVel(1.5F).setOnImpact((bullet,hit)->{if(bullet.tickCount<2)return;pelletExplode(bullet,hit,5,false);bullet.setDead();});
        laser=new BulletConfig("hbm:grenade_laser").setBeam().setupDamageClass(DamageClass.LASER).setLife(3).setRenderRotations(false).setThresholdNegation(10).setOnBeamImpact(BulletConfig.LAMBDA_STANDARD_BEAM_HIT);
    }
    public static void explode(GrenadeParts.Filling filling,UniversalGrenade grenade){switch(filling){
        case POWDER->standard(grenade,5,10,5,0);case HE->standard(grenade,7.5F,25,10,.1F);
        case DEMO->base(grenade,5).setBlockAllocator(new BlockAllocatorStandard()).setBlockProcessor(new BlockProcessorStandard()).setEntityProcessor(new EntityProcessorCrossSmooth(1,10)).setPlayerProcessor(new PlayerProcessorStandard()).setSFX(new ExplosionEffectWeapon(10,2.5F,1)).explode();
        case INC,WP->{standard(grenade,3,10,0,0);var fire=new LingeringFire(grenade.level()).area(6,2).duration(filling==GrenadeParts.Filling.INC?200:600).type(filling==GrenadeParts.Filling.INC?0:2);fire.setPos(grenade.position());grenade.level().addFreshEntity(fire);ignite(grenade,filling==GrenadeParts.Filling.INC?2:3);if(filling==GrenadeParts.Filling.WP)for(int i=0;i<3;i++)GrenadeParticleEffects.sendHaze(grenade.level(),grenade.getX()+grenade.level().random.nextGaussian()*4,grenade.getY(),grenade.getZ()+grenade.level().random.nextGaussian()*4,grenade.getX(),grenade.getY(),grenade.getZ());}
        case CLUSTER,CLUSTER_HEAVY->{standard(grenade,7.5F,15,10,.1F);boolean big=filling==GrenadeParts.Filling.CLUSTER_HEAVY;int count=big?15:30;if(!big&&grenade.shell()==GrenadeParts.Shell.FRAG)count*=1.25;for(int i=0;i<count;i++){var bullet=new EntityBulletBaseMK4(grenade.level(),big?heavy:pellets,big?30:15,0,grenade.level().random.nextFloat()*2F*(float)Math.PI,(grenade.level().random.nextFloat()*.5F+.5F)*(float)Math.PI);bullet.setPos(grenade.getX(),grenade.getY()+.05,grenade.getZ());bullet.setDeltaMovement(bullet.getDeltaMovement().multiply(.5,big?1.25:.75,.5));grenade.level().addFreshEntity(bullet);}}
        case EMP->{energy(grenade,15,3,DamageClass.ELECTRIC,.5F,.5F,1,3);var p=grenade.blockPosition();ExplosionEmp.empBlast(grenade.level(),p.getX(),p.getY(),p.getZ(),5);}
        case PLASMA->energy(grenade,50,5,DamageClass.PLASMA,.5F,1,.5F,4);
        case LASER->{tiny(grenade,2,5);double x=grenade.getX(),y=grenade.getY()+.125,z=grenade.getZ();var targets=grenade.level().getEntitiesOfClass(LivingEntity.class,new AABB(x,y,z,x,y,z).inflate(15));Collections.shuffle(targets);for(var target:targets){if(target==grenade.getThrower())continue;var delta=new Vec3(target.getX()-x,target.getY()+target.getBbHeight()/2-y,target.getZ()-z);double distance=delta.length();if(distance>15)continue;var beam=new EntityBulletBeamBase(grenade.level(),laser,30);beam.thrower=grenade.getThrower();beam.setPos(x,y,z);beam.setRotationsFromVector(delta);beam.performHitscanExternal(distance);grenade.level().addFreshEntity(beam);}}
        case NUCLEAR,NUCLEAR_DEMO->{boolean demo=filling==GrenadeParts.Filling.NUCLEAR_DEMO;var blast=new ExplosionVNT(grenade.level(),grenade.getX(),grenade.getY(),grenade.getZ(),10).setEntityProcessor(new EntityProcessorCrossSmooth(2,demo?50:100).withRangeMod(1.5F)).setPlayerProcessor(new PlayerProcessorStandard());if(demo)blast.setBlockAllocator(new BlockAllocatorStandard(64)).setBlockProcessor(new BlockProcessorStandard().withBlockEffect(new BlockMutatorFire()));blast.explode();ConventionalRadiation.incrementRad(grenade.level(),grenade.getX(),grenade.getY(),grenade.getZ(),demo?1.5F:1);mushroom(grenade);}
        case SCHRAB->{var ex=NuclearExplosionMK3.statFacFleija(grenade.level(),grenade.getX(),grenade.getY(),grenade.getZ(),20);if(!ex.isRemoved()){grenade.level().playSound(null,grenade.getX(),grenade.getY(),grenade.getZ(),SoundEvents.GENERIC_EXPLODE,SoundSource.BLOCKS,100,grenade.level().random.nextFloat()*.1F+.9F);grenade.level().addFreshEntity(ex);var cloud=new NuclearFleijaCloud(NuclearContent.FLEIJA_CLOUD.get(),grenade.level(),20);cloud.setPos(grenade.position());grenade.level().addFreshEntity(cloud);}}
    }}
    private static ExplosionVNT base(UniversalGrenade grenade,float range){return new ExplosionVNT(grenade.level(),grenade.getX(),grenade.getY(),grenade.getZ(),range,grenade.getThrower());}
    private static void standard(UniversalGrenade grenade,float range,float damage,float dt,float dr){base(grenade,range).setEntityProcessor(new EntityProcessorCrossSmooth(1,damage).setupPiercing(dt,dr)).setPlayerProcessor(new PlayerProcessorStandard()).setSFX(new ExplosionEffectWeapon(10,2.5F,1)).explode();}
    private static void tiny(UniversalGrenade grenade,float range,float damage){base(grenade,range).setEntityProcessor(new EntityProcessorCrossSmooth(.5,damage).setupPiercing(0,0).setKnockback(.25)).setPlayerProcessor(new PlayerProcessorStandard()).setSFX(new ExplosionEffectTiny()).explode();}
    private static void ignite(UniversalGrenade grenade,int radius){var world=grenade.level();var center=grenade.blockPosition();for(int x=-radius;x<=radius;x++)for(int y=-radius;y<=radius;y++)for(int z=-radius;z<=radius;z++){var position=center.offset(x,y,z);if(world.isEmptyBlock(position))for(var side:Direction.values()){var neighbor=position.relative(side);if(world.getBlockState(neighbor).isFlammable(world,neighbor,side.getOpposite())){world.setBlockAndUpdate(position,Blocks.FIRE.defaultBlockState());break;}}}}
    private static void energy(UniversalGrenade grenade,float damage,float range,DamageClass type,float r,float g,float b,float scale){base(grenade,range).setEntityProcessor(new EntityProcessorCrossSmooth(1,damage).setDamageClass(type)).setPlayerProcessor(new PlayerProcessorStandard()).explode();var world=grenade.level();double x=grenade.getX(),y=grenade.getY(),z=grenade.getZ();world.playSound(null,x,y,z,HbmSounds.get("entity.ufoBlast"),SoundSource.BLOCKS,5,.9F+world.random.nextFloat()*.2F);world.playSound(null,x,y,z,SoundEvents.FIREWORK_ROCKET_BLAST,SoundSource.BLOCKS,5,.5F);float yaw=world.random.nextFloat()*180;for(int i=0;i<3;i++)NuclearPlasmaBlastEffects.sendAtOrigin(world,x,y+.125,z,r,g,b,scale,-60+60*i,yaw,100,x,y,z);}
    private static void mushroom(UniversalGrenade grenade){var world=grenade.level();double x=grenade.getX(),y=grenade.getY(),z=grenade.getZ();SatelliteBursts.reportEvent(world,SatelliteBursts.DURATION_LOW,SatelliteBursts.BurstIntensity.LOW,x,z);world.playSound(null,x,y,z,HbmSounds.get("weapon.mukeExplosion"),SoundSource.BLOCKS,15,1);NuclearMukeEffects.sendAtOrigin(world,x,y+.5,z,"muke",LegacyStartup.polaroidId==11||world.random.nextInt(100)==0,x,y,z);}
    public static void standardFragmentation(UniversalGrenade grenade,float count){if(grenade.shell()==GrenadeParts.Shell.FRAG)count*=1.5;for(int i=0;i<count;i++){var bullet=new EntityBulletBaseMK4(grenade.level(),fragmentation,10,0,grenade.level().random.nextFloat()*2*(float)Math.PI,(grenade.level().random.nextFloat()-.5F)*2*(float)Math.PI);bullet.setPos(grenade.getX(),grenade.getY()+.05,grenade.getZ());grenade.level().addFreshEntity(bullet);}}
    private static void pelletExplode(EntityBulletBaseMK4 bullet,HitResult hit,float range,boolean tiny){var pos=hit.getLocation();if(tiny&&hit instanceof BlockHitResult block)pos=pos.add(Vec3.atLowerCornerOf(block.getDirection().getNormal()).scale(.25));var processor=new EntityProcessorCrossSmooth(tiny?.5:1,bullet.damage).setupPiercing(bullet.config.armorThresholdNegation,bullet.config.armorPiercingPercent);if(tiny)processor.setKnockback(.25);var blast=new ExplosionVNT(bullet.level(),pos.x,pos.y,pos.z,range,bullet.getThrower()).setEntityProcessor(processor).setPlayerProcessor(new PlayerProcessorStandard());blast.setSFX(tiny?new ExplosionEffectTiny():new ExplosionEffectWeapon(10,2.5F,1)).explode();}
}
