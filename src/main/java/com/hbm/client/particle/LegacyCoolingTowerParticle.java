package com.hbm.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;

/** Complete source ParticleCoolingTower, including optional modifiers used by gas clouds. */
public class LegacyCoolingTowerParticle extends LegacyFluidBillboardParticle {
    /** Dedicated source consumers may reuse the real tower material without duplicating physics or shaders. */
    protected static net.minecraft.client.renderer.ShaderInstance sourceShader(){return LegacyTowerClient.shader();}
    private float baseScale=1,maxScale=1,lift=.3F,strafe=.075F,alphaMod=.25F;
    private boolean windDir=true;
    public LegacyCoolingTowerParticle(ClientLevel level,double x,double y,double z){super(level,x,y,z);rCol=gCol=bCol=.9F+level.random.nextFloat()*.05F;visualState();}
    public void setBaseScale(float value){baseScale=value;visualState();}
    public void setMaxScale(float value){maxScale=value;visualState();}
    public void setLift(float value){lift=value;}
    public void setLife(int value){lifetime=value;visualState();}
    public void setStrafe(float value){strafe=value;}
    public void noWind(){windDir=false;}
    public void alphaMod(float value){alphaMod=value;visualState();}
    /** ParticleEngine admits new particles after ticking; their first frame must already use source age0 values. */
    private void visualState(){float ageScale=(float)age/lifetime;alpha=alphaMod-ageScale*alphaMod;particleScale=baseScale+(float)Math.pow(maxScale*ageScale-baseScale,2);}
    @Override public void tick(){xo=x;yo=y;zo=z;float ageScale=(float)age/lifetime;visualState();age++;if(lift>0&&yd<lift)yd+=.01F;if(lift<0&&yd>lift)yd-=.01F;xd+=random.nextGaussian()*strafe*ageScale;zd+=random.nextGaussian()*strafe*ageScale;if(windDir){xd+=.02*ageScale;zd-=.01*ageScale;}if(age==lifetime)remove();move(xd,yd,zd);xd*=.925;yd*=.925;zd*=.925;}
    @Override public void render(VertexConsumer buffer,Camera camera,float partial){if(LegacyTowerRenderTypes.defer(this,partial))return;billboard(LegacyTowerRenderTypes.vertices(),camera,partial,particleScale,0,1,0,1);}
    @Override public ParticleRenderType getRenderType(){return LegacyTowerRenderTypes.ORDERED;}
    @Override public AABB getRenderBoundingBox(float partial){double px=Mth.lerp(partial,xo,x),py=Mth.lerp(partial,yo,y),pz=Mth.lerp(partial,zo,z),radius=Math.abs(particleScale)*Math.sqrt(2);return new AABB(px-radius,py-radius,pz-radius,px+radius,py+radius,pz+radius);}
}
