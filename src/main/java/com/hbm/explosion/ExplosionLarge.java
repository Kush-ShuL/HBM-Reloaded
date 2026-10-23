package com.hbm.explosion;

import com.hbm.bomb.conventional.*;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

/** Source ExplosionLarge conventional effects and actual debris entities; original integer arithmetic is retained. */
public final class ExplosionLarge {
    private static final Random rand=new Random();
    private ExplosionLarge(){}
    public static void spawnParticles(Level level,double x,double y,double z,int count){SmokeBurstEffects.cloud(level,x,y,z,count);}
    public static void spawnParticlesRadial(Level level,double x,double y,double z,int count){SmokeBurstEffects.radial(level,x,y,z,count);}
    /** Original ParticleUtil gasfire ring: integer angles are deliberately radians, including 360/count truncation. */
    public static void spawnBurst(Level level,double x,double y,double z,int count,double strength){
        float angle=rand.nextInt(360),cos=net.minecraft.util.Mth.cos(angle),sin=net.minecraft.util.Mth.sin(angle);
        double vx=strength*cos,vz=-strength*sin;
        for(int i=0;i<count;i++){
            com.hbm.particle.LegacyFluidEffects.gasFlame(level,x,y,z,vx,0,vz);
            angle=360/count;cos=net.minecraft.util.Mth.cos(angle);sin=net.minecraft.util.Mth.sin(angle);
            double previousX=vx;vx=previousX*cos+vz*sin;vz=vz*cos-previousX*sin;
        }
    }
    public static void spawnFoam(Level level,double x,double y,double z,int count){FoamSplashEffects.send(level,x,y,z,count);}
    public static void spawnRubble(Level level,double x,double y,double z,int count){for(int i=0;i<count;i++){var rubble=new RubbleProjectile(level,x,y,z);double yv=.75*(1+(count+rand.nextInt(count*5))/25),xv=rand.nextGaussian()*.75*(1+count/50),zv=rand.nextGaussian()*.75*(1+count/50);rubble.setDeltaMovement(xv,yv,zv);rubble.setMaterial(Blocks.STONE.defaultBlockState());level.addFreshEntity(rubble);}}
    public static void spawnShrapnels(Level level,double x,double y,double z,int count){for(int i=0;i<count;i++){var shrapnel=new ShrapnelProjectile(level,x,y,z);double yv=(rand.nextFloat()*.5+.5)*(1+count/(15+rand.nextInt(21)))+rand.nextFloat()/50*count,xv=rand.nextGaussian()*(1+count/50),zv=rand.nextGaussian()*(1+count/50);shrapnel.setDeltaMovement(xv,yv,zv);shrapnel.setTrail(rand.nextInt(3)==0);level.addFreshEntity(shrapnel);}}
    public static void spawnTracers(Level level,double x,double y,double z,int count){for(int i=0;i<count;i++){var shrapnel=new ShrapnelProjectile(level,x,y,z);double yv=(rand.nextFloat()*.5+.5)*(1+count/(15+rand.nextInt(21)))+rand.nextFloat()/50*count*.25F,xv=rand.nextGaussian()*(1+count/50)*.25F,zv=rand.nextGaussian()*(1+count/50)*.25F;shrapnel.setDeltaMovement(xv,yv,zv);shrapnel.setTrail(true);level.addFreshEntity(shrapnel);}}
    public static void spawnShrapnelShower(Level level,double x,double y,double z,double vx,double vy,double vz,int count,double deviation){for(int i=0;i<count;i++){var shrapnel=new ShrapnelProjectile(level,x,y,z);shrapnel.setDeltaMovement(vx+rand.nextGaussian()*deviation,vy+rand.nextGaussian()*deviation,vz+rand.nextGaussian()*deviation);shrapnel.setTrail(rand.nextInt(3)==0);level.addFreshEntity(shrapnel);}}
    public static void explode(Level level,double x,double y,double z,float strength,boolean cloud,boolean rubble,boolean shrapnel){level.explode(null,x,y,z,strength,Level.ExplosionInteraction.TNT);effects(level,x,y,z,strength,cloud,rubble,shrapnel);}
    public static void explodeFire(Level level,double x,double y,double z,float strength,boolean cloud,boolean rubble,boolean shrapnel){level.explode(null,(float)x,(float)y,(float)z,strength,true,Level.ExplosionInteraction.TNT);effects(level,x,y,z,strength,cloud,rubble,shrapnel);}
    private static void effects(Level level,double x,double y,double z,float strength,boolean cloud,boolean rubble,boolean shrapnel){if(cloud)spawnParticles(level,x,y,z,cloudFunction((int)strength));if(rubble)spawnRubble(level,x,y,z,rubbleFunction((int)strength));if(shrapnel)spawnShrapnels(level,x,y,z,shrapnelFunction((int)strength));}
    public static int cloudFunction(int i){return(int)(850*(1-Math.pow(Math.E,-i/15))+15);}public static int rubbleFunction(int i){return i/10;}public static int shrapnelFunction(int i){return i/3;}
	public static void spawnMissileDebris(Level world, double x, double y, double z, double motionX, double motionY, double motionZ, double deviation, java.util.List<net.minecraft.world.item.ItemStack> debris, net.minecraft.world.item.ItemStack rareDrop) {

		if(debris != null) {
			for(int i = 0; i < debris.size(); i++) {
				if(debris.get(i) != null) {
					int k = rand.nextInt(debris.get(i).getCount() + 1);
					for(int j = 0; j < k; j++) {
						net.minecraft.world.entity.item.ItemEntity item = new net.minecraft.world.entity.item.ItemEntity(world, x, y, z, debris.get(i).copy());
						item.setDeltaMovement((motionX + rand.nextGaussian() * deviation) * 0.85, (motionY + rand.nextGaussian() * deviation) * 0.85, (motionZ + rand.nextGaussian() * deviation) * 0.85);
						item.setPos(item.position().add(item.getDeltaMovement().scale(2)));

						world.addFreshEntity(item);
					}
				}
			}
		}

		if(rareDrop != null && rand.nextInt(10) == 0) {
			net.minecraft.world.entity.item.ItemEntity item = new net.minecraft.world.entity.item.ItemEntity(world, x, y, z, rareDrop.copy());
			item.setDeltaMovement(motionX + rand.nextGaussian() * deviation * 0.1, motionY + rand.nextGaussian() * deviation * 0.1, motionZ + rand.nextGaussian() * deviation * 0.1);

			world.addFreshEntity(item);
		}
	}

    /** Original jolt excavates the first weak solid along each random spoke into a real moving rubble entity. */
    public static void jolt(Level world,double posX,double posY,double posZ,double strength,int count,double vel){
        for(int j=0;j<count;j++){
            double phi=rand.nextDouble()*(Math.PI*2),costheta=rand.nextDouble()*2-1,theta=Math.acos(costheta);
            double x=Math.sin(theta)*Math.cos(phi),y=Math.sin(theta)*Math.sin(phi),z=Math.cos(theta);
            for(int i=0;i<strength;i++){
                double x0=posX+x*i,y0=posY+y*i,z0=posZ+z*i;
                if(!world.isClientSide){
                    var pos=new net.minecraft.core.BlockPos((int)x0,(int)y0,(int)z0);
                    if(world.getBlockState(pos).liquid())world.setBlock(pos,Blocks.AIR.defaultBlockState(),3);
                    var state=world.getBlockState(pos);
                    if(!state.isAir()){
                        if(state.getBlock().getExplosionResistance()>70)continue;
                        var rubble=new RubbleProjectile(world,x0+.5F,y0+.5F,z0+.5F);rubble.setMetaBasedOnBlock(state);
                        // Source Vec3.normalize() returns a new vector but its return was discarded.
                        rubble.setDeltaMovement((posX-rubble.getX())*vel,(posY-rubble.getY())*vel,(posZ-rubble.getZ())*vel);
                        world.addFreshEntity(rubble);world.setBlock(pos,Blocks.AIR.defaultBlockState(),3);break;
                    }
                }
            }
        }
    }

}
