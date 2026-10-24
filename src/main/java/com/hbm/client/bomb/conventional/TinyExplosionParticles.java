package com.hbm.client.bomb.conventional;

import com.hbm.bomb.conventional.TinyExplosionEffects;
import com.hbm.client.bomb.nuclear.LegacyExplosionEffects;
import net.minecraft.client.Minecraft;

/** Source ClientProxy vanillaExt largeexplode branch: one warm flash plus its one gray secondary. */
public final class TinyExplosionParticles {
    private TinyExplosionParticles(){}
    public static void receive(TinyExplosionEffects p){warmFlash(p.x(),p.y(),p.z(),1.5F,1);}
    /** The source adds every secondary before its main flash, with a separate color draw and scale for each. */
    public static void warmFlash(double x,double y,double z,float size,int count){var mc=Minecraft.getInstance();var world=mc.level;if(world==null)return;
        var flash=LegacyExplosionEffects.create("largeexplode",world,x,y,z,size,0,0);float r=1-world.random.nextFloat()*.2F;flash.setColor(r,.9F*r,.5F*r);
        for(int i=0;i<count;i++){
            var secondary=LegacyExplosionEffects.create("explode",world,x,y,z,0,0,0);float gray=(1-world.random.nextFloat()*.5F)*.5F;secondary.setColor(gray,gray,gray);secondary.scale(i+1);mc.particleEngine.add(secondary);
        }
        mc.particleEngine.add(flash);
    }
}
