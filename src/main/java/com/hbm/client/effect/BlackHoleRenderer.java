package com.hbm.client.effect;

import com.hbm.client.model.LegacyObjModels;
import com.hbm.client.render.WorldEffectsForward;
import com.hbm.entity.effect.EntityBlackHole;
import com.hbm.entity.effect.EntityVortex;
import com.hbm.entity.effect.EntityRagingVortex;
import com.hbm.entity.effect.EntityQuasar;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

/** The original Sphere.obj, 16-sector swirl, 15 accretion rings, two glow passes and jets. */
public class BlackHoleRenderer<T extends EntityBlackHole> extends EntityRenderer<T> {
    private static final ResourceLocation SPHERE=ResourceLocation.parse("hbm:models/sphere.obj"),HOLE=ResourceLocation.parse("hbm:textures/models/blackhole.png"),SWIRL=ResourceLocation.parse("hbm:textures/entity/bhole.png"),DISC=ResourceLocation.parse("hbm:textures/entity/bholedisc.png"),QUASAR=ResourceLocation.parse("hbm:textures/entity/bholed.png");
    private static final RenderType CORE=Layers.textured(HOLE,false,false),SWIRL_ALPHA=Layers.textured(SWIRL,true,false),SWIRL_GLOW=Layers.textured(SWIRL,true,true),DISC_ALPHA=Layers.textured(DISC,true,false),DISC_GLOW=Layers.textured(DISC,true,true),QUASAR_ALPHA=Layers.textured(QUASAR,true,false),QUASAR_GLOW=Layers.textured(QUASAR,true,true),JETS=Layers.jets(false),FLARE=Layers.jets(true);
    private static final MultiBufferSource.BufferSource BUFFERS=MultiBufferSource.immediate(new ByteBufferBuilder(262144));
    public BlackHoleRenderer(EntityRendererProvider.Context context){super(context);shadowRadius=0;}
    @Override public ResourceLocation getTextureLocation(T entity){return HOLE;}
    @Override public void render(T entity,float yaw,float partial,PoseStack pose,MultiBufferSource buffers,int light){
        pose.pushPose();float size=entity.size();pose.scale(size,size,size);
        var corePose=BlackHolePasses.copyPose(pose);
        if(!WorldEffectsForward.defer(()->sphere(corePose)))sphere(pose);
        if(!BlackHolePasses.defer(entity,partial,effectPose->effects(entity,partial,effectPose)))BlackHolePasses.drawOrDefer(false,pose,effectPose->effects(entity,partial,effectPose));
        pose.popPose();
    }
    private void effects(T entity,float partial,PoseStack pose){
        if(entity instanceof EntityVortex)swirl(entity,partial,pose);else if(entity instanceof EntityRagingVortex){swirl(entity,partial,pose);jets(entity,pose);}else{disc(entity,partial,pose);jets(entity,pose);}
    }
    private static void sphere(PoseStack pose){
        var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),SPHERE);var out=BUFFERS.getBuffer(CORE);
        for(var face:mesh.faces()){
            float au=0,av=0;for(var corner:face.corners())if(corner.texture()>=0){var uv=mesh.textureCoordinates().get(corner.texture());au+=uv.u();av+=1-uv.v();}au/=face.corners().size();av/=face.corners().size();
            if(face.corners().size()==4){for(int i=0;i<4;i++)sphereVertex(mesh,face,i,au,av,pose,out);}
            else if(face.corners().size()==3){for(int i:new int[]{0,1,2,2})sphereVertex(mesh,face,i,au,av,pose,out);}
            else for(var triangle:mesh.triangles())if(mesh.faces().get(triangle.face())==face)for(int i:new int[]{triangle.first(),triangle.second(),triangle.third(),triangle.third()})sphereVertex(mesh,face,i,au,av,pose,out);
        }
        BUFFERS.endBatch(CORE);
    }
    private static void sphereVertex(com.hbm.client.model.WavefrontMesh mesh,com.hbm.client.model.WavefrontMesh.Face face,int index,float au,float av,PoseStack pose,VertexConsumer out){
        var corner=face.corners().get(index);var pos=mesh.positions().get(corner.position());float u=0,v=0;if(corner.texture()>=0){var texture=mesh.textureCoordinates().get(corner.texture());u=texture.u();v=1-texture.v();u+=u>au?-.0005F:.0005F;v+=v>av?-.0005F:.0005F;}
        out.addVertex(pose.last().pose(),pos.x(),pos.y(),pos.z()).setUv(u,v).setColor(255,255,255,255);
    }
    private static void orient(EntityBlackHole entity,PoseStack pose){pose.mulPose(Axis.XP.rotationDegrees(entity.getId()%90-45));pose.mulPose(Axis.YP.rotationDegrees(entity.getId()%360));}
    protected void swirl(T entity,float partial,PoseStack pose){
        pose.pushPose();orient(entity,pose);pose.mulPose(Axis.YP.rotationDegrees((entity.tickCount+partial%360)*-5));Vec3 vector=new Vec3(1,0,0);double scale=3;float glow=entity instanceof EntityRagingVortex?.25F:.75F;
        for(int pass=0;pass<2;pass++){
            RenderType type=pass==0?SWIRL_ALPHA:SWIRL_GLOW;var out=BUFFERS.getBuffer(type);
            for(int i=0;i<16;i++){
                vertex(out,pose,vector,.9,0,.5+vector.x*.25/scale*.9,.5+vector.z*.25/scale*.9,0,0,0,1);
                tint(out,pose,vector,scale,0,.5+vector.x*.25,.5+vector.z*.25,entity,pass==0?1:glow,pass!=0);
                vector=vector.yRot((float)(Math.PI*2/16));tint(out,pose,vector,scale,0,.5+vector.x*.25,.5+vector.z*.25,entity,pass==0?1:glow,pass!=0);
                vertex(out,pose,vector,.9,0,.5+vector.x*.25/scale*.9,.5+vector.z*.25/scale*.9,0,0,0,1);
            }
            BUFFERS.endBatch(type);
        }
        for(int pass=0;pass<2;pass++){
            RenderType type=pass==0?SWIRL_ALPHA:SWIRL_GLOW;var out=BUFFERS.getBuffer(type);
            for(int i=0;i<16;i++){
                tint(out,pose,vector,scale,0,.5+vector.x*.25,.5+vector.z*.25,entity,pass==0?1:glow,pass!=0);
                tint(out,pose,vector,scale*2,0,.5+vector.x*.5,.5+vector.z*.5,entity,0,false);
                vector=vector.yRot((float)(Math.PI*2/16));tint(out,pose,vector,scale*2,0,.5+vector.x*.5,.5+vector.z*.5,entity,0,false);
                tint(out,pose,vector,scale,0,.5+vector.x*.25,.5+vector.z*.25,entity,pass==0?1:glow,pass!=0);
            }
            BUFFERS.endBatch(type);
        }
        pose.popPose();
    }
    protected void disc(T entity,float partial,PoseStack pose){
        pose.pushPose();orient(entity,pose);Vec3 vector=new Vec3(1,0,0);
        for(int k=0;k<15;k++){
            pose.pushPose();pose.mulPose(Axis.YP.rotationDegrees((entity.tickCount+partial%360)*-(float)Math.pow(k+1,1.25)));double scale=3-k*.175D;float[] tint=entity instanceof EntityQuasar?new float[]{1,(float)Math.pow(k/15F,2),(float)Math.pow(k/15F,2)}:iteration(k);
            for(int pass=0;pass<2;pass++){
                RenderType type=entity instanceof EntityQuasar?(pass==0?QUASAR_ALPHA:QUASAR_GLOW):(pass==0?DISC_ALPHA:DISC_GLOW);var out=BUFFERS.getBuffer(type);
                for(int i=0;i<16;i++){
                    iterationVertex(out,pose,vector,scale,tint,pass==0?1:.75F,pass!=0,.25);
                    iterationVertex(out,pose,vector,scale*2,tint,0,false,.5);
                    vector=vector.yRot((float)(Math.PI*2/16));iterationVertex(out,pose,vector,scale*2,tint,0,false,.5);
                    iterationVertex(out,pose,vector,scale,tint,pass==0?1:.75F,pass!=0,.25);
                }
                BUFFERS.endBatch(type);
            }
            pose.popPose();
        }
        pose.popPose();
    }
    private static float[] iteration(int k){if(k<5)return new float[]{1,.125F+k*.1F,0};if(k==5)return new float[]{1,1,0};int i=k-6;return new float[]{1-i/9F,1-i/9F,i/5F};}
    private static void iterationVertex(VertexConsumer out,PoseStack pose,Vec3 v,double size,float[] tint,float alpha,boolean white,double uv){vertex(out,pose,v,size,0,.5+v.x*uv,.5+v.z*uv,white?1:tint[0],white?1:tint[1],white?1:tint[2],alpha);}
    protected void jets(T entity,PoseStack pose){
        pose.pushPose();orient(entity,pose);var out=BUFFERS.getBuffer(JETS);
        for(int sign=-1;sign<=1;sign+=2){Vec3 vector=new Vec3(.5,0,0);for(int i=0;i<12;i++){
            out.addVertex(pose.last().pose(),0,0,0).setColor(1F,1F,1F,.35F);
            out.addVertex(pose.last().pose(),(float)vector.x,10*sign,(float)vector.z).setColor(1F,1F,1F,0F);
            vector=vector.yRot((float)(Math.PI/6*-sign));out.addVertex(pose.last().pose(),(float)vector.x,10*sign,(float)vector.z).setColor(1F,1F,1F,0F);
            out.addVertex(pose.last().pose(),(float)vector.x,10*sign,(float)vector.z).setColor(1F,1F,1F,0F);
        }}
        BUFFERS.endBatch(JETS);pose.popPose();
    }
    /** Preserved source helper, available to source renderer subclasses even though these four do not call it. */
    protected void renderFlare(T entity,PoseStack pose){
        pose.pushPose();pose.scale(.2F,.2F,.2F);int j=75;float f1=(j+2F)/200F,f2=0;int count=j;if(f1>.8F)f2=(f1-.8F)/.2F;
        var random=new java.util.Random(432L);var out=BUFFERS.getBuffer(FLARE);int rgb=entity instanceof EntityVortex?0x3898b3:entity instanceof EntityRagingVortex?0xe8390d:0xffb900;
        for(int i=0;i<count;i++){
            pose.mulPose(Axis.XP.rotationDegrees(random.nextFloat()*360));pose.mulPose(Axis.YP.rotationDegrees(random.nextFloat()*360));pose.mulPose(Axis.ZP.rotationDegrees(random.nextFloat()*360));
            pose.mulPose(Axis.XP.rotationDegrees(random.nextFloat()*360));pose.mulPose(Axis.YP.rotationDegrees(random.nextFloat()*360));pose.mulPose(Axis.ZP.rotationDegrees(random.nextFloat()*360+f1*90));
            float f3=random.nextFloat()*20+5+f2*10,f4=random.nextFloat()*2+1+f2*2;
            float[][] points={{(float)(-.866D*f4),f3,-.5F*f4},{(float)(.866D*f4),f3,-.5F*f4},{0,f3,f4}};
            for(int k=0;k<3;k++){
                out.addVertex(pose.last().pose(),0,0,0).setColor((rgb>>16)&255,(rgb>>8)&255,rgb&255,255);
                var a=points[k];var b=points[(k+1)%3];out.addVertex(pose.last().pose(),a[0],a[1],a[2]).setColor((rgb>>16)&255,(rgb>>8)&255,rgb&255,0);
                out.addVertex(pose.last().pose(),b[0],b[1],b[2]).setColor((rgb>>16)&255,(rgb>>8)&255,rgb&255,0);out.addVertex(pose.last().pose(),b[0],b[1],b[2]).setColor((rgb>>16)&255,(rgb>>8)&255,rgb&255,0);
            }
        }
        BUFFERS.endBatch(FLARE);pose.popPose();
    }
    private static void tint(VertexConsumer out,PoseStack pose,Vec3 v,double scale,double y,double u,double w,EntityBlackHole entity,float alpha,boolean white){int rgb=entity instanceof EntityVortex?0x3898b3:entity instanceof EntityRagingVortex?0xe8390d:0xffb900;vertex(out,pose,v,scale,y,u,w,white?1:((rgb>>16)&255)/255F,white?1:((rgb>>8)&255)/255F,white?1:(rgb&255)/255F,alpha);}
    private static void vertex(VertexConsumer out,PoseStack pose,Vec3 v,double scale,double y,double u,double w,float r,float g,float b,float a){out.addVertex(pose.last().pose(),(float)(v.x*scale),(float)y,(float)(v.z*scale)).setUv((float)u,(float)w).setColor(Mth.clamp(r,0,1),Mth.clamp(g,0,1),Mth.clamp(b,0,1),Mth.clamp(a,0,1));}
    private static final class Layers extends RenderType {
        private Layers(String name,VertexFormat format,VertexFormat.Mode mode,int size,boolean crumble,boolean sort,Runnable setup,Runnable clear){super(name,format,mode,size,crumble,sort,setup,clear);}
        private static final TransparencyStateShard SOURCE_ALPHA=new TransparencyStateShard("hbm_black_hole_source_alpha",()->{RenderSystem.enableBlend();RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,GlStateManager.SourceFactor.ONE,com.hbm.client.bomb.nuclear.NuclearTransparencyLayers.active()?GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA:GlStateManager.DestFactor.ZERO);},()->{RenderSystem.disableBlend();RenderSystem.defaultBlendFunc();});
        private static final TransparencyStateShard SOURCE_ADD=new TransparencyStateShard("hbm_black_hole_source_add",()->{RenderSystem.enableBlend();if(com.hbm.client.bomb.nuclear.NuclearTransparencyLayers.active())RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE,GlStateManager.SourceFactor.ZERO,GlStateManager.DestFactor.ONE);else RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE);},()->{RenderSystem.disableBlend();RenderSystem.defaultBlendFunc();});
        // Native COLOR_DEPTH_WRITE is a no-op for true/true; the core must not inherit a previous renderer's disabled depth writes.
        private static final WriteMaskStateShard CORE_WRITE=new WriteMaskStateShard(true,true){@Override public void setupRenderState(){RenderSystem.depthMask(true);RenderSystem.colorMask(true,true,true,true);}};
        private static final WriteMaskStateShard EFFECT_WRITE=new WriteMaskStateShard(true,true){@Override public void setupRenderState(){boolean depth=BlackHolePasses.depthOnly();RenderSystem.depthMask(depth);RenderSystem.colorMask(!depth,!depth,!depth,!depth);}@Override public void clearRenderState(){RenderSystem.depthMask(true);RenderSystem.colorMask(true,true,true,true);}};
        static RenderType textured(ResourceLocation texture,boolean transparent,boolean additive){return create("hbm_black_hole_"+texture.getPath()+"_"+transparent+"_"+additive,DefaultVertexFormat.POSITION_TEX_COLOR,VertexFormat.Mode.QUADS,262144,false,false,CompositeState.builder().setShaderState(new ShaderStateShard(transparent?BlackHoleClient::ringShader:()->BlackHoleClient.sphereShader)).setTextureState(new TextureStateShard(texture,false,false)).setTransparencyState(transparent?(additive?SOURCE_ADD:SOURCE_ALPHA):NO_TRANSPARENCY).setCullState(NO_CULL).setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(transparent?EFFECT_WRITE:CORE_WRITE).createCompositeState(false));}
        static RenderType jets(boolean flare){return create(flare?"hbm_black_hole_flare":"hbm_black_hole_jets",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,4096,false,false,CompositeState.builder().setShaderState(new ShaderStateShard(BlackHoleClient::jetsShader)).setTransparencyState(SOURCE_ADD).setCullState(flare?CULL:NO_CULL).setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(EFFECT_WRITE).createCompositeState(false));}
    }
}
