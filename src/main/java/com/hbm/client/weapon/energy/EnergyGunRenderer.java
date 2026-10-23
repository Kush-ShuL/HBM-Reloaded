package com.hbm.client.weapon.energy;
import com.hbm.client.weapon.legendary.*;
/** Source weapon-base laser muzzle primitive, keeping its original elapsed-time expansion, color, UV and additive color-only depth behavior. */
public abstract class EnergyGunRenderer extends LegacyGunRenderer {
    protected static void renderLaserFlash(long lastShot,int duration,double scale,int color){
        long elapsed=System.currentTimeMillis()-lastShot;if(elapsed>=duration)return;
        double size=4*(elapsed/(double)duration)*scale;var context=LegacyGunGL.frame();var out=context.buffers().getBuffer(EnergyRenderTypes.FLASH);
        double[][] vertices={{0,-size,-size,1,1},{0,size,-size,0,1},{0,size,size,0,0},{0,-size,size,1,0}};
        for(var vertex:vertices)out.addVertex(context.pose().last(),(float)vertex[0],(float)vertex[1],(float)vertex[2]).setColor(0xff000000|color).setUv((float)vertex[3],(float)vertex[4]).setOverlay(net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY).setLight(240).setNormal(context.pose().last(),0,1,0);
    }
}
