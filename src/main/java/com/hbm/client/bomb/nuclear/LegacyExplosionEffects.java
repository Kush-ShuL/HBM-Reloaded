package com.hbm.client.bomb.nuclear;

import com.hbm.explosion.ExplosionVisuals;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import java.util.Random;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import com.mojang.blaze3d.platform.GlStateManager;

/** Exact original VNT particle composition, tile coordinates, animation, and flash fan geometry. */
public final class LegacyExplosionEffects {
    private static final ResourceLocation SHEET=ResourceLocation.fromNamespaceAndPath("hbm","textures/particle/vanilla_1_7_10.png");
    private static final ResourceLocation EXPLOSION=ResourceLocation.fromNamespaceAndPath("hbm","textures/particle/vanilla_explosion_1_7_10.png");
    private static ParticleRenderType textured(ResourceLocation texture,boolean blend){return new ParticleRenderType(){
        @Override public BufferBuilder begin(Tesselator tess,TextureManager manager){RenderSystem.depthMask(true);if(blend){RenderSystem.enableBlend();RenderSystem.defaultBlendFunc();}else RenderSystem.disableBlend();RenderSystem.setShader(GameRenderer::getParticleShader);RenderSystem.setShaderTexture(0,texture);return tess.begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.PARTICLE);}
    };}
    private static final ParticleRenderType SMALL=textured(SHEET,true),LARGE=textured(EXPLOSION,false);
    private static final ParticleRenderType SMOKE=MukeRenderTypes.cloudTexture(SHEET,true);
    private static final ParticleRenderType FLASH=new ParticleRenderType(){
        @Override public BufferBuilder begin(Tesselator tess,TextureManager manager){RenderSystem.setShader(GameRenderer::getPositionColorShader);RenderSystem.enableBlend();RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE);RenderSystem.depthMask(false);return tess.begin(VertexFormat.Mode.TRIANGLES,DefaultVertexFormat.POSITION_COLOR);}
    };
    private LegacyExplosionEffects(){}
    public static Particle create(String mode,ClientLevel world,double x,double y,double z,double vx,double vy,double vz){
        return switch(mode){case "hugeexplosion"->new Huge(world,x,y,z);case "largeexplode"->new Large(world,x,y,z,vx);case "explode"->new Small(world,x,y,z,vx,vy,vz,false);case "smoke"->new Small(world,x,y,z,vx,vy,vz,true);case "largesmoke"->new Small(world,x,y,z,vx,vy,vz,true).largeSmoke();default->throw new IllegalArgumentException(mode);};
    }
    public static void particle(String mode,double x,double y,double z,double vx,double vy,double vz){
        var mc=Minecraft.getInstance();if(mc.level!=null)mc.particleEngine.add(create(mode,mc.level,x,y,z,vx,vy,vz));
    }
    public static void receive(ExplosionVisuals packet){
        Minecraft mc=Minecraft.getInstance();ClientLevel world=mc.level;if(world==null)return;
        double x=packet.x(),y=packet.y(),z=packet.z();float size=packet.size();
        if(packet.effect()==ExplosionVisuals.AMAT){mc.particleEngine.add(new AmatFlash(world,x,y,z,size));return;}
        if(size>=2)mc.particleEngine.add(new Huge(world,x,y,z));else mc.particleEngine.add(new Large(world,x,y,z,1));
        for(var pos:packet.blocks()){
            double ox=(double)((float)pos.getX()+world.random.nextFloat()),oy=(double)((float)pos.getY()+world.random.nextFloat()),oz=(double)((float)pos.getZ()+world.random.nextFloat());
            double dx=ox-x,dy=oy-y,dz=oz-z,delta=(double)(float)Math.sqrt(dx*dx+dy*dy+dz*dz);dx/=delta;dy/=delta;dz/=delta;
            double mod=.5D/(delta/(double)size+.1D);mod*=(double)(world.random.nextFloat()*world.random.nextFloat()+.3F);dx*=mod;dy*=mod;dz*=mod;
            mc.particleEngine.add(new Small(world,(ox+x)/2,(oy+y)/2,(oz+z)/2,dx,dy,dz,false));
            mc.particleEngine.add(new Small(world,ox,oy,oz,dx,dy,dz,true));
        }
    }
    private abstract static class LegacyQuad extends SingleQuadParticle {
        LegacyQuad(ClientLevel world,double x,double y,double z){super(world,x,y,z);random.nextFloat();random.nextFloat();quadSize=(random.nextFloat()*.5F+.5F)*2F*.1F;}
        void legacyRandomMotion(double vx,double vy,double vz){
            xd=vx+(double)((float)(Math.random()*2D-1D)*.4F);yd=vy+(double)((float)(Math.random()*2D-1D)*.4F);zd=vz+(double)((float)(Math.random()*2D-1D)*.4F);
            float speed=(float)(Math.random()+Math.random()+1D)*.15F,norm=(float)Math.sqrt(xd*xd+yd*yd+zd*zd);xd=xd/(double)norm*(double)speed*(double).4F;yd=yd/(double)norm*(double)speed*(double).4F+(double).1F;zd=zd/(double)norm*(double)speed*(double).4F;
        }
    }
    private static final class Small extends LegacyQuad {
        private final boolean smoke;
        private float originalScale;
        private int texture;
        Small(ClientLevel world,double x,double y,double z,double vx,double vy,double vz,boolean smoke){
            super(world,x,y,z);this.smoke=smoke;
            if(smoke){legacyRandomMotion(0,0,0);xd=xd*(double).1F+vx;yd=yd*(double).1F+vy;zd=zd*(double).1F+vz;rCol=gCol=bCol=(float)(Math.random()*(double).3F);quadSize*=.75F;originalScale=quadSize;lifetime=(int)(8D/(Math.random()*.8D+.2D));hasPhysics=false;}
            else{legacyRandomMotion(vx,vy,vz);xd=vx+(double)((float)(Math.random()*2D-1D)*.05F);yd=vy+(double)((float)(Math.random()*2D-1D)*.05F);zd=vz+(double)((float)(Math.random()*2D-1D)*.05F);rCol=gCol=bCol=random.nextFloat()*.3F+.7F;quadSize=(random.nextFloat()*random.nextFloat()*6F+1F)*.1F;lifetime=(int)(16D/((double)random.nextFloat()*.8D+.2D))+2;}
        }
        private Small largeSmoke(){quadSize*=2.5F;originalScale=quadSize;lifetime=(int)(lifetime*2.5F);return this;}
        @Override public void tick(){xo=x;yo=y;zo=z;if(age++>=lifetime)remove();texture=7-age*8/lifetime;yd+=.004D;move(xd,yd,zd);if(smoke&&y==yo){xd*=1.1D;zd*=1.1D;}double drag=smoke?(double).96F:(double).9F;xd*=drag;yd*=drag;zd*=drag;if(onGround){xd*=(double).7F;zd*=(double).7F;}}
        @Override public float getQuadSize(float partial){return smoke?originalScale*Mth.clamp(((float)age+partial)/(float)lifetime*32F,0,1):quadSize;}
        @Override protected float getU0(){return (float)(texture%16)/16F;}
        @Override protected float getU1(){return getU0()+.0624375F;}
        @Override protected float getV0(){return (float)(texture/16)/16F;}
        @Override protected float getV1(){return getV0()+.0624375F;}
        @Override public void render(VertexConsumer vertices,Camera camera,float partial){if(smoke){if(MukeRenderTypes.defer(this,partial))return;vertices=MukeRenderTypes.begin(SMOKE);}super.render(vertices,camera,partial);}
        @Override public ParticleRenderType getRenderType(){return smoke?MukeRenderTypes.ORDERED:SMALL;}
    }
    private static final class Large extends LegacyQuad {
        private int frame;
        Large(ClientLevel world,double x,double y,double z,double vx){super(world,x,y,z);legacyRandomMotion(0,0,0);lifetime=6+random.nextInt(4);rCol=gCol=bCol=random.nextFloat()*.6F+.4F;quadSize=2F*(1F-(float)vx*.5F);}
        @Override public void tick(){xo=x;yo=y;zo=z;if(++age==lifetime)remove();}
        @Override public void render(VertexConsumer vertices,Camera camera,float partial){frame=(int)(((float)age+partial)*15F/(float)lifetime);if(frame<=15)super.render(vertices,camera,partial);}
        @Override protected float getU0(){return (float)(frame%4)/4F;}
        @Override protected float getU1(){return getU0()+.24975F;}
        @Override protected float getV0(){return (float)(frame/4)/4F;}
        @Override protected float getV1(){return getV0()+.24975F;}
        @Override protected int getLightColor(float partial){return 0xF000F0;}
        @Override public ParticleRenderType getRenderType(){return LARGE;}
    }
    private static final class Huge extends Particle {
        Huge(ClientLevel world,double x,double y,double z){super(world,x,y,z);lifetime=8;}
        @Override public void tick(){for(int i=0;i<6;i++){double px=x+(random.nextDouble()-random.nextDouble())*4D,py=y+(random.nextDouble()-random.nextDouble())*4D,pz=z+(random.nextDouble()-random.nextDouble())*4D;Minecraft.getInstance().particleEngine.add(new Large(level,px,py,pz,(float)age/(float)lifetime));}if(++age==lifetime)remove();}
        @Override public void render(VertexConsumer vertices,Camera camera,float partial){}
        @Override public ParticleRenderType getRenderType(){return ParticleRenderType.NO_RENDER;}
    }
    private static final class AmatFlash extends Particle {
        private final float scale;
        AmatFlash(ClientLevel world,double x,double y,double z,float scale){super(world,x,y,z);this.scale=scale;lifetime=10;}
        @Override public void tick(){xo=x;yo=y;zo=z;if(age++>=lifetime)remove();}
        @Override public AABB getRenderBoundingBox(float partial){return AABB.INFINITE;}
        @Override public void render(VertexConsumer vertices,Camera camera,float partial){
            if(com.hbm.client.render.WorldEffectParticles.draw(com.hbm.client.render.WorldEffectParticles.amatFlash(),out->sourceRender(out,camera,partial)))return;
            sourceRender(vertices,camera,partial);
        }
        private void sourceRender(VertexConsumer vertices,Camera camera,float partial){
            var player=Minecraft.getInstance().player;if(player==null)return;
            double px=Mth.lerp(partial,player.xOld,player.getX()),py=Mth.lerp(partial,player.yOld,player.getY())+player.getEyeHeight(),pz=Mth.lerp(partial,player.zOld,player.getZ());
            PoseStack pose=new PoseStack();pose.translate((float)(Mth.lerp(partial,xo,x)-px),(float)(Mth.lerp(partial,yo,y)-py),(float)(Mth.lerp(partial,zo,z)-pz));pose.scale(.2F*scale,.2F*scale,.2F*scale);
            double intensity=(double)((float)age+partial)/(double)lifetime,inverse=1D-intensity;Random random=new Random(432L);
            for(int i=0;i<100;i++){
                pose.mulPose(Axis.XP.rotationDegrees(random.nextFloat()*360F));pose.mulPose(Axis.YP.rotationDegrees(random.nextFloat()*360F));pose.mulPose(Axis.ZP.rotationDegrees(random.nextFloat()*360F));pose.mulPose(Axis.XP.rotationDegrees(random.nextFloat()*360F));pose.mulPose(Axis.YP.rotationDegrees(random.nextFloat()*360F));
                float length=(random.nextFloat()*20F+5F+10F)*(float)(intensity*.5F),width=(random.nextFloat()*2F+1F+2F)*(float)(intensity*.5F);
                float[][] ring={{(float)(-.866D*width),length,-.5F*width},{(float)(.866D*width),length,-.5F*width},{0,length,width}};
                for(int v=0;v<3;v++){vertices.addVertex(pose.last().pose(),0,0,0).setColor(1F,1F,1F,(float)inverse);var a=ring[v];vertices.addVertex(pose.last().pose(),a[0],a[1],a[2]).setColor(1F,1F,1F,0F);var b=ring[(v+1)%3];vertices.addVertex(pose.last().pose(),b[0],b[1],b[2]).setColor(1F,1F,1F,0F);}
            }
        }
        @Override public ParticleRenderType getRenderType(){return FLASH;}
    }
}
