package com.hbm.client.bomb.nuclear;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
/** Exact1.7 EntitySmokeFX + vanillaExt(noClip=true,overrideAge=50), using the sole shared ordered particle renderer. */
public final class FlareSmokeParticle extends Particle {
    private final float shade,sourceScale;
    public FlareSmokeParticle(ClientLevel level,double x,double y,double z){super(level,x,y,z);double mx=(float)(Math.random()*2-1)*.4F,my=(float)(Math.random()*2-1)*.4F,mz=(float)(Math.random()*2-1)*.4F;float magnitude=(float)(Math.random()+Math.random()+1)*.15F,length=(float)Math.sqrt(mx*mx+my*my+mz*mz);xd=mx/length*magnitude*.4000000059604645*.10000000149011612;yd=(my/length*magnitude*.4000000059604645+.10000000149011612)*.10000000149011612;zd=mz/length*magnitude*.4000000059604645*.10000000149011612;shade=(float)(Math.random()*.30000001192092896);sourceScale=(random.nextFloat()*.5F+.5F)*2*.75F;lifetime=50;hasPhysics=false;}
    @Override public void tick(){xo=x;yo=y;zo=z;if(age++>=lifetime)remove();yd+=.004;move(xd,yd,zd);if(y==yo){xd*=1.1;zd*=1.1;}xd*=.9599999785423279;yd*=.9599999785423279;zd*=.9599999785423279;}
    @Override public ParticleRenderType getRenderType(){return MukeRenderTypes.ORDERED;}
    @Override public void render(VertexConsumer ignored,Camera camera,float partial){if(MukeRenderTypes.defer(this,partial))return;var out=MukeRenderTypes.begin(MukeRenderTypes.VANILLA_SMOKE);var eye=camera.getPosition();int frame=Math.max(0,7-age*8/lifetime);var sprite=((TextureAtlas)Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_PARTICLES)).getSprite(ResourceLocation.withDefaultNamespace("generic_"+frame));float scale=.1F*sourceScale*Mth.clamp((age+partial)/lifetime*32F,0,1);float px=(float)(Mth.lerp(partial,xo,x)-eye.x),py=(float)(Mth.lerp(partial,yo,y)-eye.y),pz=(float)(Mth.lerp(partial,zo,z)-eye.z);var corners=new org.joml.Vector3f[]{new org.joml.Vector3f(-1,-1,0),new org.joml.Vector3f(-1,1,0),new org.joml.Vector3f(1,1,0),new org.joml.Vector3f(1,-1,0)};float[] us={sprite.getU1(),sprite.getU1(),sprite.getU0(),sprite.getU0()},vs={sprite.getV1(),sprite.getV0(),sprite.getV0(),sprite.getV1()};for(int i=0;i<4;i++){var vertex=corners[i].rotate(camera.rotation()).mul(scale).add(px,py,pz);out.addVertex(vertex.x,vertex.y,vertex.z).setUv(us[i],vs[i]).setColor(shade,shade,shade,1).setLight(getLightColor(partial));}}
}
