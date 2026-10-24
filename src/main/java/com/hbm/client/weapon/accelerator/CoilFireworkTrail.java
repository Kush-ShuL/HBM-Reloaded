package com.hbm.client.weapon.accelerator;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
/** Official1.7.10 bkd spark shared by the source coil trail and colored letter-firework burst. */
public final class CoilFireworkTrail extends SingleQuadParticle {
    private static final ParticleRenderType TYPE=new ParticleRenderType(){
        @Override public BufferBuilder begin(Tesselator tess,TextureManager textures){RenderSystem.depthMask(true);RenderSystem.enableBlend();RenderSystem.defaultBlendFunc();RenderSystem.setShader(GameRenderer::getParticleShader);RenderSystem.setShaderTexture(0,ResourceLocation.parse("hbm:textures/particle/vanilla_1_7_10.png"));return tess.begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.PARTICLE);}
    };
    private int tile;
    private CoilFireworkTrail(ClientLevel level,double x,double y,double z){
        this(level,x,y,z,0,0,0);
    }
    private CoilFireworkTrail(ClientLevel level,double x,double y,double z,double vx,double vy,double vz){
        super(level,x,y,z);setBoundingBox(getBoundingBox().move(0,-.1,0));
        // Modern quad constructor consumes the first random float; complete
        // original jitter/scale/base-lifetime draws before the spark lifetime.
        random.nextFloat();quadSize=(random.nextFloat()*.5F+.5F)*2F*.1F*.75F;random.nextFloat();
        lifetime=48+random.nextInt(12);xd=vx;yd=vy;zd=vz;rCol=gCol=bCol=alpha=1F;hasPhysics=true;
    }
    public static void spawn(double x,double y,double z){var minecraft=Minecraft.getInstance();if(minecraft.level!=null)minecraft.particleEngine.add(new CoilFireworkTrail(minecraft.level,x,y,z));}
    public static void spawn(double x,double y,double z,double vx,double vy,double vz,int color){
        var minecraft=Minecraft.getInstance();if(minecraft.level==null)return;
        var spark=new CoilFireworkTrail(minecraft.level,x,y,z,vx,vy,vz);
        spark.setColor(((color>>16)&255)/255F,((color>>8)&255)/255F,(color&255)/255F);
        minecraft.particleEngine.add(spark);
    }
    @Override public void tick(){xo=x;yo=y;zo=z;if(age++>=lifetime)remove();if(age>lifetime/2)alpha=1F-((float)age-(float)(lifetime/2))/(float)lifetime;tile=160+7-age*8/lifetime;yd-=.004D;move(xd,yd,zd);xd*=(double).91F;yd*=(double).91F;zd*=(double).91F;if(onGround){xd*=(double).7F;zd*=(double).7F;}}
    @Override public void move(double x,double y,double z){var motion=Entity.collideBoundingBox(null,new Vec3(x,y,z),getBoundingBox(),level,List.of());setBoundingBox(getBoundingBox().move(motion));setLocationFromBoundingbox();onGround=y!=motion.y&&y<0;if(x!=motion.x)xd=0;if(y!=motion.y)yd=0;if(z!=motion.z)zd=0;}
    @Override protected void setLocationFromBoundingbox(){super.setLocationFromBoundingbox();y+=.1D;}
    @Override protected int getLightColor(float partial){return 15728880;}
    @Override protected float getU0(){return (float)(tile%16)/16F;}
    @Override protected float getU1(){return getU0()+.0624375F;}
    @Override protected float getV0(){return (float)(tile/16)/16F;}
    @Override protected float getV1(){return getV0()+.0624375F;}
    @Override public ParticleRenderType getRenderType(){return TYPE;}
}
