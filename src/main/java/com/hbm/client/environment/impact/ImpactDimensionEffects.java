package com.hbm.client.environment.impact;

import com.hbm.environment.impact.ImpactState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/** A per-overworld wrapper, not a global replacement of every dimension's effects or weather renderer. */
public final class ImpactDimensionEffects extends DimensionSpecialEffects {
    private final ClientLevel world;private final DimensionSpecialEffects original;
    public ImpactDimensionEffects(ClientLevel world,DimensionSpecialEffects original){super(original.getCloudHeight(),original.hasGround(),original.skyType(),original.forceBrightLightmap(),original.constantAmbientLight());this.world=world;this.original=original;}
    public DimensionSpecialEffects delegate(){return original;}
    @Override public float[] getSunriseColor(float angle,float partial){
        if(!ImpactState.provider(world))return original.getSunriseColor(angle,partial);
        float f3=Mth.cos(angle*(float)Math.PI*2F),dust=ImpactState.get(world).dust();if(f3<-.4F||f3>.4F)return null;
        float f5=f3/.4F*.5F+.5F,f6=1F-(1F-Mth.sin(f5*(float)Math.PI))*.99F;f6*=f6;
        return new float[]{(f5*.3F+.7F)*(1-dust),(f5*f5*.7F+.2F)*(1-dust),.2F*(1-dust),f6*(1-dust)};
    }
    @Override public Vec3 getBrightnessDependentFogColor(Vec3 color,float sun){
        Vec3 fog=original.getBrightnessDependentFogColor(color,sun);if(!ImpactState.provider(world))return fog;var data=ImpactState.get(world);float dust=data.dust();double scale=data.fire()>0?Math.max(1-dust*2,0):1-dust;return new Vec3(fog.x*scale,fog.y*(1-dust*.5F)*scale,fog.z*(1-dust)*scale);
    }
    @Override public boolean isFoggyAt(int x,int z){return original.isFoggyAt(x,z);}
    @Override public boolean renderSky(ClientLevel level,int ticks,float partial,Matrix4f view,Camera camera,Matrix4f projection,boolean foggy,Runnable setupFog){
        if(ImpactState.skyboxes()&&level==world&&ImpactState.get(level).active()){ImpactSkyRenderer.render(level,partial,view,camera,projection,setupFog);return true;}
        return original.renderSky(level,ticks,partial,view,camera,projection,foggy,setupFog);
    }
    @Override public boolean renderClouds(ClientLevel level,int ticks,float partial,PoseStack pose,double x,double y,double z,Matrix4f view,Matrix4f projection){return original.renderClouds(level,ticks,partial,pose,x,y,z,view,projection);}
    @Override public boolean renderSnowAndRain(ClientLevel level,int ticks,float partial,LightTexture light,double x,double y,double z){return original.renderSnowAndRain(level,ticks,partial,light,x,y,z);}
    @Override public boolean tickRain(ClientLevel level,int ticks,Camera camera){return original.tickRain(level,ticks,camera);}
    @Override public void adjustLightmapColors(ClientLevel level,float partial,float skyDarken,float flicker,float sky,int x,int y,Vector3f color){original.adjustLightmapColors(level,partial,skyDarken,flicker,sky,x,y,color);}
}
