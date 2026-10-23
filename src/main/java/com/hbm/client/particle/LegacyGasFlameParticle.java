package com.hbm.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.hbm.client.bomb.nuclear.MukeRenderTypes;
import java.awt.Color;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/** Source ParticleGasFlame and the original1.7.10 EntitySmokeFX parent path. */
public final class LegacyGasFlameParticle extends LegacyFluidBillboardParticle {
    private static final ParticleRenderType TYPE=MukeRenderTypes.cloudTexture(ResourceLocation.parse("hbm:textures/particle/legacy_particles.png"),false);
    private final float smokeScale;
    private float colorMod=1;
    private int textureIndex;
    public LegacyGasFlameParticle(ClientLevel level,double x,double y,double z,double mx,double my,double mz,float scale){
        super(level,x,y,z);double vx=(float)(Math.random()*2-1)*.4F,vy=(float)(Math.random()*2-1)*.4F,vz=(float)(Math.random()*2-1)*.4F;float speed=(float)(Math.random()+Math.random()+1)*.15F,length=(float)Math.sqrt(vx*vx+vy*vy+vz*vz);
        xd=(vx/length*speed*.4000000059604645D)*.10000000149011612D+mx;yd=(vy/length*speed*.4000000059604645D+.10000000149011612D)*.10000000149011612D+my*1.5;zd=(vz/length*speed*.4000000059604645D)*.10000000149011612D+mz;
        rCol=gCol=bCol=(float)(Math.random()*.30000001192092896D);particleScale*=.75F;particleScale*=scale;smokeScale=particleScale;lifetime=(int)(8D/(Math.random()*.8D+.2D));lifetime=(int)(lifetime*scale);
        updateColor();particleScale=scale;colorMod=.8F+random.nextFloat()*.2F;lifetime=30+random.nextInt(13);
    }
    @Override public void tick(){
        double previousY=yd;xo=x;yo=y;zo=z;if(age++>=lifetime)remove();textureIndex=7-age*8/lifetime;yd+=.004;move(xd,yd,zd);if(y==yo){xd*=1.1;zd*=1.1;}xd*=.9599999785423279D;yd*=.9599999785423279D;zd*=.9599999785423279D;if(onGround){xd*=.699999988079071D;zd*=.699999988079071D;}updateColor();yd=previousY;xd*=.75D;yd+=.005D;zd*=.75D;
    }
    private void updateColor(){float time=(float)age/lifetime;var color=Color.getHSBColor(Math.max((60-time*100)/360F,0),1-time*.25F,1-time*.5F);rCol=color.getRed()/255F*colorMod;gCol=color.getGreen()/255F*colorMod;bCol=color.getBlue()/255F*colorMod;}
    @Override protected int getLightColor(float partial){return 15728880;}
    @Override public void render(VertexConsumer buffer,Camera camera,float partial){if(MukeRenderTypes.defer(this,partial))return;buffer=MukeRenderTypes.begin(TYPE);particleScale=smokeScale*Mth.clamp((age+partial)/lifetime*32,0,1);float minU=(textureIndex%16)/16F,minV=(textureIndex/16)/16F;billboard(buffer,camera,partial,.1F*particleScale,minU,minU+.0624375F,minV,minV+.0624375F);}
    @Override public ParticleRenderType getRenderType(){return MukeRenderTypes.ORDERED;}
}
