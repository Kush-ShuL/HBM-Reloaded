package com.hbm.client.bomb.conventional;

import com.hbm.bomb.conventional.TinyExplosionEffects;
import com.hbm.client.bomb.nuclear.LegacyExplosionEffects;
import net.minecraft.client.Minecraft;

/** Source ClientProxy vanillaExt largeexplode branch: one warm flash plus its one gray secondary. */
public final class TinyExplosionParticles {
    private TinyExplosionParticles(){}
    public static void receive(TinyExplosionEffects p){var mc=Minecraft.getInstance();var world=mc.level;if(world==null)return;
        var flash=LegacyExplosionEffects.create("largeexplode",world,p.x(),p.y(),p.z(),1.5,0,0);float r=1-world.random.nextFloat()*.2F;flash.setColor(r,.9F*r,.5F*r);
        var secondary=LegacyExplosionEffects.create("explode",world,p.x(),p.y(),p.z(),0,0,0);float gray=(1-world.random.nextFloat()*.5F)*.5F;secondary.setColor(gray,gray,gray);secondary.scale(1);mc.particleEngine.add(secondary);mc.particleEngine.add(flash);
    }
}
