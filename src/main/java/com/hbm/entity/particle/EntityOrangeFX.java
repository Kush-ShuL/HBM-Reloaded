package com.hbm.entity.particle;
import com.hbm.block.fluid.LegacyMaterials;
import com.hbm.explosion.ExplosionChaos;
import com.hbm.explosion.ExplosionNukeGeneric;
import com.hbm.world.structure.geyser.GeyserContent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
/** Actual source Agent Orange: persistent poisoning, four substeps and its source3x3x3 vegetation corruption on every collision substep. */
public final class EntityOrangeFX extends EntityModFX {
 public EntityOrangeFX(EntityType<? extends EntityOrangeFX> type,Level level){super(type,level,0,0,0);}
 public EntityOrangeFX(Level level,double x,double y,double z,double mx,double my,double mz){this(level,x,y,z,mx,my,mz,1);}
 public EntityOrangeFX(Level level,double x,double y,double z,double mx,double my,double mz,float scale){super(GeyserContent.ORANGE.get(),level,x,y,z,0,0,0);setDeltaMovement(getDeltaMovement().scale(.1).add(mx,my,mz));particleRed=particleGreen=particleBlue=(float)(Math.random()*.3);particleScale*=.75F;particleScale*=scale;smokeParticleScale=particleScale;noPhysics=false;}
 @Override public void tick(){xo=getX();yo=getY();zo=getZ();if(maxAge<900)maxAge=random.nextInt(301)+900;if(!level().isClientSide&&random.nextInt(50)==0)ExplosionChaos.poison(level(),(int)getX(),(int)getY(),(int)getZ(),2);particleAge++;if(particleAge>=maxAge)discard();setDeltaMovement(getDeltaMovement().scale(.86).add(0,-.1,0));for(int i=0;i<4;i++){setPos(position().add(getDeltaMovement().scale(.25)));var pos=new BlockPos((int)getX(),(int)getY(),(int)getZ());if(!LegacyMaterials.of(level().getBlockState(pos)).name().equals("air")){discard();for(int a=-1;a<2;a++)for(int b=-1;b<2;b++)for(int c=-1;c<2;c++){var p=pos.offset(a,b,c);if(level().getBlockState(p).is(Blocks.GRASS_BLOCK))level().setBlock(p,Blocks.COARSE_DIRT.defaultBlockState(),3);else ExplosionNukeGeneric.solinium(level(),p.getX(),p.getY(),p.getZ());}}}}
 @Override public boolean shouldBeSaved(){return false;}@Override protected void readAdditionalSaveData(CompoundTag tag){super.readAdditionalSaveData(tag);discard();}
}
