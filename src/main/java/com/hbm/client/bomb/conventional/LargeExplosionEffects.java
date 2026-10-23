package com.hbm.client.bomb.conventional;

import com.hbm.client.bomb.nuclear.MukeRenderTypes;

import com.hbm.bomb.conventional.LargeConventionalEffects;
import com.hbm.registry.HbmSounds;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.phys.Vec3;

/** Original ExplosionCreator composition and RocketFlame/MukeWave/Debris particle algorithms. */
public final class LargeExplosionEffects {
    private static final AtomicInteger IDS=new AtomicInteger();
    private static final ResourceLocation BASE=ResourceLocation.parse("hbm:textures/block/particle/particle_base.png"),WAVE_TEXTURE=ResourceLocation.parse("hbm:textures/particle/shockwave.png");
    private static final ParticleRenderType CLOUD=MukeRenderTypes.cloudTexture(BASE,false);
    private static final ParticleRenderType WAVE=new ParticleRenderType(){@Override public BufferBuilder begin(Tesselator tess,TextureManager manager){RenderSystem.setShader(GameRenderer::getPositionTexColorShader);RenderSystem.setShaderTexture(0,WAVE_TEXTURE);RenderSystem.enableBlend();RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE);RenderSystem.depthMask(false);RenderSystem.disableCull();return tess.begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.POSITION_TEX_COLOR);}};
    private static final ParticleRenderType DEBRIS=new ParticleRenderType(){@Override public boolean isTranslucent(){return false;}@Override public BufferBuilder begin(Tesselator tess,TextureManager manager){RenderSystem.setShader(GameRenderer::getRendertypeSolidShader);RenderSystem.setShaderTexture(0,TextureAtlas.LOCATION_BLOCKS);RenderSystem.disableBlend();RenderSystem.depthMask(true);RenderSystem.disableCull();return tess.begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.BLOCK);}};
    private LargeExplosionEffects(){}
    public static void receive(LargeConventionalEffects data){
        var mc=Minecraft.getInstance();var level=mc.level;var player=mc.player;if(level==null||player==null)return;var random=level.random;
        float distance=(float)Math.sqrt(player.distanceToSqr(data.x(),data.y(),data.z()));
        if(distance<=data.soundRange()){var sound=HbmSounds.get(distance<=data.soundRange()*.4?"weapon.explosionLargeNear":"weapon.explosionLargeFar");mc.getSoundManager().playDelayed(new SimpleSoundInstance(sound,SoundSource.BLOCKS,1000F,.9F+random.nextFloat()*.2F,RandomSource.create(),data.x(),data.y(),data.z()),(int)(distance/8.575D));}
        var wave=new com.hbm.client.bomb.nuclear.MukeWave(level,data.x(),data.y()+2,data.z());wave.setup(data.wave(),(int)(25F*data.wave()/45));mc.particleEngine.add(wave);
        for(int i=0;i<data.clouds();i++){var flame=new Flame(level,data.x(),data.y(),data.z(),data.scale(),300);flame.xdValue=random.nextGaussian()*.5*data.speed();flame.ydValue=random.nextDouble()*3*data.speed();flame.zdValue=random.nextGaussian()*.5*data.speed();flame.lifespan(70+random.nextInt(20));mc.particleEngine.add(flame);}
        for(int c=0;c<data.debris();c++){
            double ox=random.nextGaussian()*data.deviation(),oy=data.vertical(),oz=random.nextGaussian()*data.deviation();int cx=Mth.floor(data.x()+ox+.5),cy=Mth.floor(data.y()+oy+.5),cz=Mth.floor(data.z()+oz+.5);
            float zAngle=(float)-Math.toRadians(45+random.nextFloat()*25),yAngle=(float)(random.nextDouble()*Math.PI*2);
            double dx=data.velocity()*Mth.cos(zAngle),dy=-data.velocity()*Mth.sin(zAngle),dz=0,rx=dx*Mth.cos(yAngle)+dz*Mth.sin(yAngle),rz=dz*Mth.cos(yAngle)-dx*Mth.sin(yAngle);
            var snapshot=new FragmentWorld(level,data.debrisSize());int middle=data.debrisSize()/2-1;
            if(data.debrisSize()>0){
                for(int i=0;i<2;i++)for(int j=0;j<2;j++)for(int k=0;k<2;k++)snapshot.put(middle+i,middle+j,middle+k,level.getBlockState(new BlockPos(cx+i,cy+j,cz+k)));
                for(int layer=2;layer<=data.debrisSize()/2;layer++)for(int i=0;i<data.retries();i++){
                    int jx=-layer+random.nextInt(layer*2+1),jy=-layer+random.nextInt(layer*2+1),jz=-layer+random.nextInt(layer*2+1);var center=new BlockPos(middle+jx,middle+jy,middle+jz);boolean neighbor=false;
                    for(Direction side:Direction.values())if(!snapshot.getBlockState(center.relative(side)).is(Blocks.AIR))neighbor=true;
                    if(neighbor)snapshot.put(center.getX(),center.getY(),center.getZ(),level.getBlockState(new BlockPos(cx+jx,cy+jy,cz+jz)));
                }
            }
            mc.particleEngine.add(new Debris(level,data.x(),data.y(),data.z(),rx,dy,rz,snapshot));
        }
    }
    private static final class Flame extends SingleQuadParticle {
        private final int id=IDS.getAndIncrement();private final float scale;double xdValue,ydValue,zdValue;
        Flame(ClientLevel level,double x,double y,double z,float scale,int lifetime){super(level,x,y,z);this.scale=scale;this.lifetime=lifetime;hasPhysics=false;}
        @Override public void tick(){xo=x;yo=y;zo=z;if(++age==lifetime)remove();xdValue*=.91D;ydValue*=.91D;zdValue*=.91D;move(xdValue,ydValue,zdValue);}
        @Override public void render(VertexConsumer out,Camera camera,float partial){if(MukeRenderTypes.defer(this,partial))return;out=MukeRenderTypes.begin(CLOUD);var random=new Random(id);var cam=camera.getPosition();for(int i=0;i<10;i++){
            float add=random.nextFloat()*.3F,dark=1-Math.min((float)age/(lifetime*.25F),1);rCol=Math.min(dark+add,1);gCol=Math.min(.6F*dark+add,1);bCol=add;alpha=(float)Math.pow(1-Math.min((float)age/lifetime,1),.5)*.75F;
            float spread=((float)Math.pow((float)age/lifetime*4F,1.5)+1)*scale,offset=.25F*scale;quadSize=(random.nextFloat()*.5F+.1F+(float)age/lifetime*2F)*scale;
            float px=(float)((xo+offset+(x-xo)*partial-cam.x)+(random.nextGaussian()-1D)*.2F*spread),py=(float)((yo+(y-yo)*partial-cam.y)+(random.nextGaussian()-1D)*.5F*spread),pz=(float)((zo+offset+(z-zo)*partial-cam.z)+(random.nextGaussian()-1D)*.2F*spread);
            renderRotatedQuad(out,camera.rotation(),px,py,pz,partial);
        }}
        void lifespan(int ticks){lifetime=ticks;}
        @Override public net.minecraft.world.phys.AABB getRenderBoundingBox(float partial){return net.minecraft.world.phys.AABB.INFINITE;}
        @Override protected int getLightColor(float partial){return 240;}@Override protected float getU0(){return 0;}@Override protected float getU1(){return 1;}@Override protected float getV0(){return 0;}@Override protected float getV1(){return 1;}@Override public ParticleRenderType getRenderType(){return MukeRenderTypes.ORDERED;}
    }
    private static final class Debris extends Particle {
        private final int id=IDS.getAndIncrement();private final FragmentWorld snapshot;private float pitch,yaw,previousPitch,previousYaw;private FragmentVertex[] mesh;private int meshLight=Integer.MIN_VALUE;
        Debris(ClientLevel level,double x,double y,double z,double vx,double vy,double vz,FragmentWorld snapshot){super(level,x,y,z);this.snapshot=snapshot;xd=vx*3;yd=vy*3;zd=vz*3;gravity=.15F;lifetime=100;hasPhysics=false;setBoundingBox(getBoundingBox().move(0,-.1,0));}
        @Override public void tick(){xo=x;yo=y;zo=z;if(age>5)hasPhysics=true;var random=new Random(id);previousPitch=pitch;previousYaw=yaw;pitch+=random.nextFloat()*10;yaw+=random.nextFloat()*10;
            if(id%3==0)Minecraft.getInstance().particleEngine.add(new Flame(level,x,y,z,Math.max(snapshot.size,6)/16F,50));yd-=gravity;move(xd,yd,zd);age++;if(onGround||level.getBlockStates(getBoundingBox()).anyMatch(state->state.is(Blocks.COBWEB)))remove();}
        @Override public void move(double dx,double dy,double dz){var delta=hasPhysics?Entity.collideBoundingBox(null,new Vec3(dx,dy,dz),getBoundingBox(),level,List.of()):new Vec3(dx,dy,dz);setBoundingBox(getBoundingBox().move(delta));setLocationFromBoundingbox();onGround=dy!=delta.y&&dy<0;if(dx!=delta.x)xd=0;if(dy!=delta.y)yd=0;if(dz!=delta.z)zd=0;}
        @Override protected void setLocationFromBoundingbox(){super.setLocationFromBoundingbox();y+=.1D;}
        @Override public void render(VertexConsumer out,Camera camera,float partial){
            int light=LevelRenderer.getLightColor(level,BlockPos.containing(x,y,z));
            if(mesh==null||meshLight!=light)bake(light);
            var cam=camera.getPosition();var poses=new PoseStack();
            poses.translate((float)(Mth.lerp(partial,xo,x)-cam.x),(float)(Mth.lerp(partial,yo,y)-cam.y),(float)(Mth.lerp(partial,zo,z)-cam.z));
            poses.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partial,previousPitch,pitch)));poses.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partial,previousYaw,yaw)));
            var pose=poses.last();for(var vertex:mesh)vertex.emit(out,pose);
        }
        // The captured fragment never changes. Preserve AO, fluid geometry and emissive
        // light, but only tessellate again when its actual packed lighting changes.
        private void bake(int light){
            snapshot.light=light;var mc=Minecraft.getInstance();var poses=new PoseStack();
            poses.translate(-snapshot.size/2D,-snapshot.size/2D,-snapshot.size/2D);
            var capture=new FragmentCapture();var random=RandomSource.create();
            for(int i=0;i<snapshot.size;i++)for(int j=0;j<snapshot.size;j++)for(int k=0;k<snapshot.size;k++){
                var pos=new BlockPos(i,j,k);var state=snapshot.getBlockState(pos);if(state.isAir())continue;
                if(state.getRenderShape()==RenderShape.MODEL){poses.pushPose();poses.translate(i,j,k);mc.getBlockRenderer().getModelRenderer().tesselateWithAO(snapshot,mc.getBlockRenderer().getBlockModel(state),state,pos,poses,capture,true,random,state.getSeed(pos),0);poses.popPose();}
                if(!state.getFluidState().isEmpty())mc.getBlockRenderer().renderLiquid(pos,snapshot,new TransformedVertices(capture,poses.last()),state,state.getFluidState());
            }
            mesh=capture.finish();meshLight=light;
        }
        @Override public net.minecraft.world.phys.AABB getRenderBoundingBox(float partial){return getBoundingBox().inflate(snapshot.size);}
        @Override public ParticleRenderType getRenderType(){return DEBRIS;}
    }
    private record FragmentVertex(float x,float y,float z,int color,float u,float v,int light,float nx,float ny,float nz){
        void emit(VertexConsumer out,PoseStack.Pose pose){out.addVertex(pose,x,y,z).setColor(color).setUv(u,v).setLight(light).setNormal(pose,nx,ny,nz);}
    }
    private static final class FragmentCapture implements VertexConsumer {
        private final ArrayList<FragmentVertex> result=new ArrayList<>();
        private float x,y,z,u,v,nx,ny,nz;private int color=-1,light;private boolean pending;
        private void end(){if(pending)result.add(new FragmentVertex(x,y,z,color,u,v,light,nx,ny,nz));}
        FragmentVertex[] finish(){end();pending=false;return result.toArray(FragmentVertex[]::new);}
        @Override public VertexConsumer addVertex(float x,float y,float z){end();this.x=x;this.y=y;this.z=z;pending=true;return this;}
        @Override public VertexConsumer setColor(int r,int g,int b,int a){color=a<<24|r<<16|g<<8|b;return this;}
        @Override public VertexConsumer setUv(float u,float v){this.u=u;this.v=v;return this;}
        @Override public VertexConsumer setUv1(int u,int v){return this;}
        @Override public VertexConsumer setUv2(int u,int v){light=u&65535|(v&65535)<<16;return this;}
        @Override public VertexConsumer setNormal(float x,float y,float z){nx=x;ny=y;nz=z;return this;}
    }
    private record TransformedVertices(VertexConsumer target,PoseStack.Pose pose) implements VertexConsumer {
        @Override public VertexConsumer addVertex(float x,float y,float z){target.addVertex(pose,x,y,z);return this;}
        @Override public VertexConsumer setColor(int r,int g,int b,int a){target.setColor(r,g,b,a);return this;}
        @Override public VertexConsumer setUv(float u,float v){target.setUv(u,v);return this;}
        @Override public VertexConsumer setUv1(int u,int v){target.setUv1(u,v);return this;}
        @Override public VertexConsumer setUv2(int u,int v){target.setUv2(u,v);return this;}
        @Override public VertexConsumer setNormal(float x,float y,float z){target.setNormal(pose,x,y,z);return this;}
    }
}
