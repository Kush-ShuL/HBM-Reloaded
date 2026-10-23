package com.hbm.client.weapon.flaregun;
import com.hbm.client.bomb.nuclear.MukeClient;
import com.hbm.client.bomb.nuclear.MukeRenderTypes;
import com.hbm.client.bomb.nuclear.NuclearTransparencyLayers;
import com.hbm.client.weapon.pepperbox.GunBulletRenderContext;
import com.hbm.client.weapon.pepperbox.GunShaderCompat;
import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

/** Source LegoClient.renderFlare: two unlit additive billboards, original UV/age/random scale.
 * The shared world-effects queue supplies real water/cloud/OBJ occlusion and Fabulous depth. */
public final class FlareSignalRenderer {
    private static final MultiBufferSource.BufferSource BUFFERS=MultiBufferSource.immediate(new ByteBufferBuilder(2048));
    private FlareSignalRenderer(){}
    static void render(EntityBulletBaseMK4 bullet,float partial,float r,float g,float b){
        if(bullet.tickCount<2)return;
        double size=Math.min(5,(bullet.tickCount+partial-2)*.5)*(.8+bullet.level().random.nextDouble()*.4);
        sprite(GunBulletRenderContext.current().poses(),size,r,g,b,.5F,.75F);
    }
    /** The one original renderFlareSprite primitive, also used by the real Folly beam. */
    public static void sprite(PoseStack poses,double size,float r,float g,float b,float outerAlpha,float innerAlpha){
        var pose=new Matrix4f(poses.last().pose());
        var camera=Minecraft.getInstance().gameRenderer.getMainCamera();
        float yaw=camera.getYRot()*Mth.DEG_TO_RAD,pitch=camera.getXRot()*Mth.DEG_TO_RAD;
        float x=Mth.cos(yaw),z=Mth.sin(yaw),tx=-z*Mth.sin(pitch),tz=x*Mth.sin(pitch),vertical=Mth.cos(pitch);
        MukeRenderTypes.deferDraw(()->{
            boolean shaderPack=GunShaderCompat.shaderPackInUse()&&!com.hbm.client.render.WorldEffectsForward.sourcePass();
            var type=shaderPack?Layers.IRIS_TYPE:Layers.TYPE;
            var out=BUFFERS.getBuffer(type);
            billboard(out,pose,x,z,tx,tz,vertical,size,r,g,b,outerAlpha,shaderPack);
            billboard(out,pose,x,z,tx,tz,vertical,size*.5,1F,1F,1F,innerAlpha,shaderPack);
            BUFFERS.endBatch(type);
        });
    }
    private static void billboard(VertexConsumer out,Matrix4f pose,float x,float z,float tx,float tz,float vertical,double size,float r,float g,float b,float alpha,boolean shaderPack){
        vertex(out,pose,(-x-tx)*size,-vertical*size,(-z-tz)*size,1,1,r,g,b,alpha,shaderPack);
        vertex(out,pose,(-x+tx)*size, vertical*size,(-z+tz)*size,1,0,r,g,b,alpha,shaderPack);
        vertex(out,pose,( x+tx)*size, vertical*size,( z+tz)*size,0,0,r,g,b,alpha,shaderPack);
        vertex(out,pose,( x-tx)*size,-vertical*size,( z-tz)*size,0,1,r,g,b,alpha,shaderPack);
    }
    private static void vertex(VertexConsumer out,Matrix4f pose,double x,double y,double z,float u,float v,float r,float g,float b,float a,boolean shaderPack){var vertex=out.addVertex(pose,(float)x,(float)y,(float)z).setUv(u,v).setColor(r,g,b,a);if(shaderPack)vertex.setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0,1,0);}
    private static final class Layers extends RenderType {
        private Layers(){super("hbm_signal_flare",DefaultVertexFormat.POSITION_TEX_COLOR,VertexFormat.Mode.QUADS,2048,false,false,()->{},()->{});}
        private static final TransparencyStateShard SOURCE_ADD=new TransparencyStateShard("hbm_signal_flare_add",()->{
            RenderSystem.enableBlend();
            if(NuclearTransparencyLayers.active())RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE,GlStateManager.SourceFactor.ZERO,GlStateManager.DestFactor.ONE);
            else RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE);
        },()->{RenderSystem.disableBlend();RenderSystem.defaultBlendFunc();});
        private static final WriteMaskStateShard DEPTH=new WriteMaskStateShard(true,true){@Override public void setupRenderState(){RenderSystem.depthMask(MukeRenderTypes.writesDepth());}@Override public void clearRenderState(){RenderSystem.depthMask(false);}};
        static final RenderType TYPE=create("hbm_signal_flare",DefaultVertexFormat.POSITION_TEX_COLOR,VertexFormat.Mode.QUADS,2048,false,false,
            CompositeState.builder().setShaderState(new ShaderStateShard(MukeClient::additiveShader))
                .setTextureState(new TextureStateShard(ResourceLocation.parse("hbm:textures/particle/flare.png"),false,false))
                .setTransparencyState(SOURCE_ADD).setCullState(NO_CULL).setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(DEPTH).createCompositeState(false));
        // A plain custom ShaderInstance is intentionally skipped by Iris when unknown shaders are disabled.
        // Native Eyes supplies the recognized unlit SRC_ALPHA/ONE program; its full entity attributes are explicit.
        static final RenderType IRIS_TYPE=create("hbm_signal_flare_iris",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,2048,false,false,
            CompositeState.builder().setShaderState(new ShaderStateShard(GameRenderer::getRendertypeEyesShader))
                .setTextureState(new TextureStateShard(ResourceLocation.parse("hbm:textures/particle/flare.png"),false,false))
                .setTransparencyState(SOURCE_ADD).setCullState(NO_CULL).setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(DEPTH)
                .setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false));
    }
}
