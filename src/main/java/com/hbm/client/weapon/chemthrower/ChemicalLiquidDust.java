package com.hbm.client.weapon.chemthrower;

import com.hbm.client.bomb.conventional.SmallExplosionEffects;
import com.hbm.entity.projectile.EntityChemical;
import net.minecraft.client.Minecraft;

/** Original client-only LIQUID producer, using the shared real 1.7 white-wool BlockDust kernel. */
final class ChemicalLiquidDust {
    static void spawn(EntityChemical chemical){
        var minecraft=Minecraft.getInstance();if(minecraft.level==null)return;
        var random=minecraft.level.random;var motion=chemical.getDeltaMovement();
        double vx=motion.x+random.nextGaussian()*.05,vy=motion.y-.2+random.nextGaussian()*.05,vz=motion.z+random.nextGaussian()*.05;
        int color=chemical.getFluidType().getColor();
        SmallExplosionEffects.sourceColorDust(chemical.getX(),chemical.getY(),chemical.getZ(),vx,vy,vz,
                (color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F);
    }
    private ChemicalLiquidDust(){}
}
