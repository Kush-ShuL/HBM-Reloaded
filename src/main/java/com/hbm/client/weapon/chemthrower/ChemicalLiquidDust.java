package com.hbm.client.weapon.chemthrower;
import com.hbm.entity.projectile.EntityChemical;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.world.level.block.Blocks;
/** ClientProxy colordust = real white-wool BlockDust, supplied velocity and source10..29life, directly coloured by fluid. */
final class ChemicalLiquidDust extends TerrainParticle {
 private ChemicalLiquidDust(ClientLevel world,EntityChemical chemical,double vx,double vy,double vz){super(world,chemical.getX(),chemical.getY(),chemical.getZ(),vx,vy+.2,vz,Blocks.WHITE_WOOL.defaultBlockState());xd=vx;yd=vy+.2;zd=vz;int color=chemical.getFluidType().getColor();setColor((color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F);lifetime=10+world.random.nextInt(20);}
 static void spawn(EntityChemical chemical){var mc=Minecraft.getInstance();if(mc.level==null)return;var random=mc.level.random;var motion=chemical.getDeltaMovement();double vx=motion.x+random.nextGaussian()*.05,vy=motion.y-.2+random.nextGaussian()*.05,vz=motion.z+random.nextGaussian()*.05;mc.particleEngine.add(new ChemicalLiquidDust(mc.level,chemical,vx,vy,vz));}
}
