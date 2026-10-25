package com.hbm.client.weapon.forward;

import com.hbm.client.weapon.forward.mixin.ForwardCompositeStateAccess;
import com.hbm.client.weapon.forward.mixin.ForwardCompositeTypeAccess;
import com.hbm.client.weapon.forward.mixin.ForwardTextureAccess;
import com.hbm.client.weapon.forward.mixin.ForwardLightTextureAccess;
import com.hbm.client.render.WorldEffectsForward;
import com.hbm.client.render.WorldEffectsDepth;
import com.hbm.client.weapon.pepperbox.GunFirstPersonRenderer;
import com.hbm.client.weapon.pepperbox.GunShaderCompat;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.joml.Matrix4f;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;

/**
 * The source hand pass has its own cleared depth. Iris material GBuffers cannot
 * represent source SRC_ALPHA/ONE, so retain actual submitted hand geometry and
 * replay only its occlusion and additive radiance after Iris's final scene pass.
 * No pack attachment number, hand-depth scale, animation or model is inferred.
 */
@EventBusSubscriber(modid="hbm", value=Dist.CLIENT, bus=EventBusSubscriber.Bus.MOD)
public final class GunForwardEffects extends RenderType {
    private GunForwardEffects() { super("hbm_source_hand_effects", DefaultVertexFormat.NEW_ENTITY,
            VertexFormat.Mode.TRIANGLES, 4096, false, false, ()->{}, ()->{}); }

    private static final ResourceLocation WHITE=ResourceLocation.withDefaultNamespace("textures/misc/white.png");
    private record Additive(ResourceLocation texture,boolean depthWrite,boolean lit,boolean fog,boolean worldOccluder) {}
    private static final Map<RenderType,Additive> ADDITIVE=new IdentityHashMap<>();
    /** Registration belongs to each actual source material definition, never to a gun-name whitelist. */
    public static RenderType additive(RenderType type,ResourceLocation texture,boolean depthWrite,boolean lit,boolean fog) {
        ADDITIVE.put(type,new Additive(texture,depthWrite,lit,fog,false));return type;
    }
    /** Only actual world-volume materials opt in; hand materials never supply world depth. */
    public static RenderType worldOccluder(RenderType type) {
        var material=ADDITIVE.get(type);
        if(material==null)throw new IllegalArgumentException("World occluder must be a registered source additive material");
        ADDITIVE.put(type,new Additive(material.texture(),material.depthWrite(),material.lit(),material.fog(),true));
        if(!worldGeometryRegistered) {
            WorldEffectsDepth.registerWorldGeometry(GunForwardEffects::hasWorldOccluders,GunForwardEffects::drawWorldOccluders);
            worldGeometryRegistered=true;
        }
        return type;
    }
    private static boolean worldGeometryRegistered;
    private static final ArrayList<Batch> BATCHES=new ArrayList<>(),WORLD_BATCHES=new ArrayList<>();
    private static ShaderInstance shader;
    private static RenderTarget target;
    private static ByteBufferBuilder memory;
    private static boolean armed, collecting, effect;
    private static int count,worldCount;

    @SubscribeEvent public static void shaders(RegisterShadersEvent event) throws IOException {
        release();
        event.registerShader(new ShaderInstance(event.getResourceProvider(),
                ResourceLocation.parse("hbm:source_hand_additive"), DefaultVertexFormat.NEW_ENTITY), value->shader=value);
    }

    /** Called once per real native frame, including menu frames and aborted predecessors. */
    public static void beginFrame() {
        discardFrame();
        var client=Minecraft.getInstance();
        if(shader==null || client.player==null || client.level==null || client.options.hideGui
                || !client.options.getCameraType().isFirstPerson() || !GunShaderCompat.shaderPackInUse()) return;
        for(var hand:InteractionHand.values()) {
            var stack=client.player.getItemInHand(hand);
            if(stack.getItem() instanceof ItemGunBaseNT
                    && IClientItemExtensions.of(stack).getCustomRenderer() instanceof GunFirstPersonRenderer) {
                armed=true; break;
            }
        }
    }

    /** Covers the installed Iris solid hand method INCLUDING its deferred native endBatch. */
    public static void beginSolidHand() { collecting=armed; }
    public static void endSolidHand() { collecting=false; }
    public static void discardFrame() { armed=false; collecting=false; effect=false; count=0; worldCount=0; }

    /**
     * Invoked before native RenderType.draw consumes/closes MeshData. Opaque draws
     * still execute their original Iris shader. Only registered real source additive
     * materials are diverted; they must not write a pack's encoded material buffers.
     */
    public static boolean capture(RenderType type, MeshData mesh) {
        if(WorldEffectsForward.sourcePass()) return false;
        Additive material=ADDITIVE.get(type);
        if(!collecting) return captureWorld(material,mesh);
        ForwardCompositeStateAccess state=null;
        if(material==null) {
            if(!(type instanceof ForwardCompositeTypeAccess access)) return false;
            state=(ForwardCompositeStateAccess)(Object)access.hbm$forwardState();
            if(state.hbm$transparency()!=NO_TRANSPARENCY || state.hbm$depthTest()!=LEQUAL_DEPTH_TEST
                    || state.hbm$writeMask()!=COLOR_DEPTH_WRITE && state.hbm$writeMask()!=DEPTH_WRITE) return false;
        }
        var mode=mesh.drawState().mode();
        if(mode!=VertexFormat.Mode.QUADS && mode!=VertexFormat.Mode.TRIANGLES
                && mode!=VertexFormat.Mode.TRIANGLE_STRIP && mode!=VertexFormat.Mode.TRIANGLE_FAN) return false;
        Batch batch=count<BATCHES.size()?BATCHES.get(count):newBatch();
        if(!copy(batch,material,state,mesh))return false;
        count++;
        if(batch.effect) { effect=true; mesh.close(); return true; }
        return false;
    }

    private static boolean copy(Batch batch,Additive material,ForwardCompositeStateAccess state,MeshData mesh) {
        batch.effect=material!=null;
        batch.depthWrite=material==null || material.depthWrite();
        batch.worldOccluder=material!=null && material.worldOccluder();
        batch.writeColour=state==null || state.hbm$writeMask()==COLOR_DEPTH_WRITE;
        batch.cull=state!=null && state.hbm$cull()!=NO_CULL;
        batch.cutoff=material!=null || state!=null && (state.hbm$shader()==RENDERTYPE_ENTITY_SOLID_SHADER
                || state.hbm$shader()==RENDERTYPE_SOLID_SHADER)?0F:.1F;
        batch.texture=material==null?WHITE:material.texture();
        batch.shaderEffect=material==null?0F:material.lit()?1F:material.fog()?3F:4F;
        if(state!=null) batch.texture=((ForwardTextureAccess)(Object)state.hbm$texture()).hbm$cutoutTexture().orElse(WHITE);
        batch.mvp.set(RenderSystem.getProjectionMatrix()).mul(RenderSystem.getModelViewMatrix());
        System.arraycopy(RenderSystem.getShaderColor(),0,batch.modulator,0,4);
        batch.fogStart=RenderSystem.getShaderFogStart();batch.fogEnd=RenderSystem.getShaderFogEnd();
        batch.fogShape=RenderSystem.getShaderFogShape().getIndex();
        if(material!=null && material.lit()) {
            RenderSystem.setupShaderLights(shader);
            for(int i=0;i<3;i++) {
                batch.lights[i]=shader.LIGHT0_DIRECTION.getFloatBuffer().get(i);
                batch.lights[i+3]=shader.LIGHT1_DIRECTION.getFloatBuffer().get(i);
            }
        }
        batch.copy(mesh);
        return batch.vertices!=0;
    }

    private static boolean captureWorld(Additive material,MeshData mesh) {
        if(material==null || shader==null || !GunShaderCompat.shaderPackInUse()
                || GunShaderCompat.handState()!=GunShaderCompat.VANILLA)return false;
        Batch batch;
        if(worldCount<WORLD_BATCHES.size())batch=WORLD_BATCHES.get(worldCount);
        else {batch=new Batch();WORLD_BATCHES.add(batch);}
        if(!copy(batch,material,null,mesh))return false;
        Batch captured=batch;
        if(WorldEffectsForward.defer(()->drawWorld(captured)))worldCount++;
        // Additive radiance never belongs in Iris's shadow/material targets. Outside
        // its real world collection there is no scene effect to replay (e.g. shadows).
        mesh.close();return true;
    }

    /**
     * Iris may retain additive entity batches until after beginTranslucents.
     * Flush this actual source volume to our existing capture now, without
     * flushing its shared world buffers or collecting a second effect queue.
     */
    public static boolean captureWorldGeometry(RenderType type,Consumer<MultiBufferSource> emit) {
        var material=ADDITIVE.get(type);
        if(material==null || !material.worldOccluder() || shader==null || collecting
                || !WorldEffectsForward.acceptsSourceDraw()
                || GunShaderCompat.handState()!=GunShaderCompat.VANILLA)return false;
        try(var immediateMemory=new ByteBufferBuilder(type.bufferSize())) {
            var buffers=MultiBufferSource.immediate(immediateMemory);
            emit.accept(buffers);
            buffers.endBatch();
        }
        return true;
    }

    private static boolean hasWorldOccluders() {
        for(int i=0;i<worldCount;i++)if(WORLD_BATCHES.get(i).worldOccluder)return true;
        return false;
    }

    private static void drawWorldOccluders() {
        if(memory==null)memory=new ByteBufferBuilder(65536);
        try(var saved=new State()) {
            RenderSystem.enableDepthTest();GL11.glEnable(GL11.GL_DEPTH_TEST);
            RenderSystem.depthFunc(GL11.GL_LEQUAL);GL11.glDepthFunc(GL11.GL_LEQUAL);
            for(int i=0;i<worldCount;i++) {
                var batch=WORLD_BATCHES.get(i);
                if(batch.worldOccluder)draw(batch,true,false);
            }
        }
    }

    private static void drawWorld(Batch batch) {
        if(memory==null)memory=new ByteBufferBuilder(65536);
        try(var saved=new State();var colour=batch.worldOccluder?WorldEffectsDepth.colour():null) {
            RenderSystem.enableDepthTest();GL11.glEnable(GL11.GL_DEPTH_TEST);
            RenderSystem.depthFunc(GL11.GL_LEQUAL);GL11.glDepthFunc(GL11.GL_LEQUAL);
            GL20.glBlendEquationSeparate(GL14.GL_FUNC_ADD,GL14.GL_FUNC_ADD);
            // The common168 callback retains the actual world depth. No hand target/clear/blit.
            draw(batch,false,colour!=null && colour.active());
        }
    }

    private static Batch newBatch() { var batch=new Batch(); BATCHES.add(batch); return batch; }

    /**
     * Installed Iris finalizeGameRendering HEAD: scene composite/final completed,
     * main target returned, but color-space conversion and vanilla GUI not begun.
     */
    public static void finishScene() {
        collecting=false;
        if(!effect || count==0) return;
        var client=Minecraft.getInstance();
        var destination=client.getMainRenderTarget();
        if(destination.width<=0 || destination.height<=0) { discardFrame(); return; }
        try(var saved=new State()) {
            RenderSystem.activeTexture(GL13.GL_TEXTURE0);
            if(target==null || target.width!=destination.width || target.height!=destination.height) {
                if(target!=null) target.destroyBuffers();
                target=new TextureTarget(destination.width,destination.height,true,Minecraft.ON_OSX);
                target.setClearColor(0,0,0,0);
            }
            if(memory==null) memory=new ByteBufferBuilder(65536);
            RenderSystem.disableScissor();
            GL11.glDisable(GL11.GL_STENCIL_TEST);
            GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
            GL11.glDisable(GL30.GL_FRAMEBUFFER_SRGB);
            GL11.glDepthRange(0,1);
            RenderSystem.depthMask(true);GL11.glDepthMask(true);
            colourMask(true,true,true,true);
            target.clear(Minecraft.ON_OSX);
            target.bindWrite(true);
            RenderSystem.enableDepthTest();GL11.glEnable(GL11.GL_DEPTH_TEST);
            RenderSystem.depthFunc(GL11.GL_LEQUAL);GL11.glDepthFunc(GL11.GL_LEQUAL);
            GL20.glBlendEquationSeparate(GL14.GL_FUNC_ADD,GL14.GL_FUNC_ADD);
            for(int i=0;i<count;i++) draw(BATCHES.get(i));

            // The hand-depth test has already happened in the private target.
            // Add its RGB once; multiplying alpha again would dim the source.
            destination.bindWrite(true);
            RenderSystem.disableDepthTest();GL11.glDisable(GL11.GL_DEPTH_TEST);
            RenderSystem.depthMask(false);GL11.glDepthMask(false);
            RenderSystem.disableCull();GL11.glDisable(GL11.GL_CULL_FACE);
            colourMask(true,true,true,false);
            blend(true,GL11.GL_ONE,GL11.GL_ONE,GL11.GL_ZERO,GL11.GL_ONE);
            shader.safeGetUniform("CapturedMVP").set(new Matrix4f());
            shader.safeGetUniform("CapturedColour").set(1F,1F,1F,1F);
            shader.safeGetUniform("AlphaCutoff").set(0F);
            shader.safeGetUniform("Effect").set(2F);
            shader.setSampler("Sampler0",target.getColorTextureId());
            var vertices=new BufferBuilder(memory,VertexFormat.Mode.QUADS,DefaultVertexFormat.NEW_ENTITY);
            quadVertex(vertices,-1,-1,0,0);
            quadVertex(vertices,1,-1,1,0);
            quadVertex(vertices,1,1,1,1);
            quadVertex(vertices,-1,1,0,1);
            submit(vertices);
        } finally { discardFrame(); }
    }

    private static void quadVertex(BufferBuilder vertices,float x,float y,float u,float v) {
        vertices.addVertex(x,y,0).setColor(-1).setUv(u,v).setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(0).setNormal(0,1,0);
    }

    private static void draw(Batch batch) {
        draw(batch,false,false);
    }

    private static void draw(Batch batch,boolean depthOnly,boolean resolvedColour) {
        boolean depthWrite=depthOnly || batch.depthWrite&&!resolvedColour;
        RenderSystem.depthMask(depthWrite);GL11.glDepthMask(depthWrite);
        if(batch.cull) { RenderSystem.enableCull();GL11.glEnable(GL11.GL_CULL_FACE); }
        else { RenderSystem.disableCull();GL11.glDisable(GL11.GL_CULL_FACE); }
        boolean writeColour=batch.writeColour&&!depthOnly;
        colourMask(writeColour,writeColour,writeColour,writeColour);
        blend(batch.effect&&!depthOnly,GL11.GL_SRC_ALPHA,GL11.GL_ONE,GL11.GL_SRC_ALPHA,GL11.GL_ONE);
        WorldEffectsDepth.shader(shader);
        // This owner calls ShaderInstance.apply directly, rather than the native
        // drawWithShader sampler bridge. Bind the helper's independent copies explicitly.
        if(resolvedColour)for(int i=6;i<=8;i++)shader.setSampler("Sampler"+i,RenderSystem.getShaderTexture(i));
        shader.safeGetUniform("CapturedMVP").set(batch.mvp);
        shader.safeGetUniform("CapturedColour").set(batch.modulator[0],batch.modulator[1],batch.modulator[2],batch.modulator[3]);
        shader.safeGetUniform("AlphaCutoff").set(batch.cutoff);
        shader.safeGetUniform("Effect").set(batch.shaderEffect);
        shader.safeGetUniform("CapturedFogStart").set(batch.fogStart);
        shader.safeGetUniform("CapturedFogEnd").set(batch.fogEnd);
        shader.safeGetUniform("CapturedFogShape").set(batch.fogShape);
        if(batch.shaderEffect==1F) {
            shader.safeGetUniform("Light0_Direction").set(batch.lights[0],batch.lights[1],batch.lights[2]);
            shader.safeGetUniform("Light1_Direction").set(batch.lights[3],batch.lights[4],batch.lights[5]);
            shader.setSampler("SourceLightmap",((ForwardLightTextureAccess)(Object)Minecraft.getInstance()
                    .gameRenderer.lightTexture()).hbm$nativeLightTexture());
        }
        shader.setSampler("Sampler0",Minecraft.getInstance().getTextureManager().getTexture(batch.texture));
        var vertices=new BufferBuilder(memory,VertexFormat.Mode.TRIANGLES,DefaultVertexFormat.NEW_ENTITY);
        for(int i=0;i<batch.vertices;i++) {
            int p=i*14;
            vertices.addVertex(batch.data[p],batch.data[p+1],batch.data[p+2])
                    .setColor(batch.data[p+5],batch.data[p+6],batch.data[p+7],batch.data[p+8])
                    .setUv(batch.data[p+3],batch.data[p+4]).setOverlay(OverlayTexture.NO_OVERLAY)
                    .setUv2((int)batch.data[p+12],(int)batch.data[p+13])
                    .setNormal(batch.data[p+9],batch.data[p+10],batch.data[p+11]);
        }
        submit(vertices);
    }

    private static void colourMask(boolean r,boolean g,boolean b,boolean a) {
        RenderSystem.colorMask(r,g,b,a);
        GL30.glColorMaski(0,r,g,b,a);
    }
    private static void blend(boolean on,int src,int dst,int srcAlpha,int dstAlpha) {
        if(on) { RenderSystem.enableBlend();GL30.glEnablei(GL11.GL_BLEND,0); }
        else { RenderSystem.disableBlend();GL30.glDisablei(GL11.GL_BLEND,0); }
        RenderSystem.blendFuncSeparate(src,dst,srcAlpha,dstAlpha);
        if(GL.getCapabilities().OpenGL40) GL40.glBlendFuncSeparatei(0,src,dst,srcAlpha,dstAlpha);
        else if(GL.getCapabilities().GL_ARB_draw_buffers_blend) ARBDrawBuffersBlend.glBlendFuncSeparateiARB(0,src,dst,srcAlpha,dstAlpha);
        else GL14.glBlendFuncSeparate(src,dst,srcAlpha,dstAlpha);
    }

    private static void submit(BufferBuilder vertices) {
        // Do not select an Iris GBuffer shader or call the world's effect queue.
        // This registered shader runs after Iris world rendering has finished.
        shader.apply();
        try { BufferUploader.draw(vertices.buildOrThrow()); }
        finally { shader.clear(); }
    }

    private static void release() {
        discardFrame();
        if(target!=null) { target.destroyBuffers(); target=null; }
        if(memory!=null) { memory.close(); memory=null; }
        BATCHES.clear();WORLD_BATCHES.clear();
    }
    @EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
    public static final class Lifecycle {
        @SubscribeEvent public static void logout(ClientPlayerNetworkEvent.LoggingOut event) {
            if(RenderSystem.isOnRenderThread()) release(); else RenderSystem.recordRenderCall(GunForwardEffects::release);
        }
    }

    /** Pooled, copied primitives; no mutable MeshData/ByteBufferBuilder survives native upload. */
    private static final class Batch {
        final Matrix4f mvp=new Matrix4f();
        final float[] modulator=new float[4],lights=new float[6];
        float[] data=new float[0];
        ResourceLocation texture;
        boolean effect,depthWrite,writeColour,cull,worldOccluder;
        float cutoff,shaderEffect,fogStart,fogEnd;
        int fogShape;
        int vertices;
        void copy(MeshData mesh) {
            var state=mesh.drawState();
            var source=mesh.vertexBuffer().duplicate().order(ByteOrder.nativeOrder());
            var indices=mesh.indexBuffer();
            if(indices!=null) indices=indices.duplicate().order(ByteOrder.nativeOrder());
            int stride=state.format().getVertexSize();
            int position=state.format().getOffset(VertexFormatElement.POSITION);
            int uv=state.format().getOffset(VertexFormatElement.UV0);
            int colour=state.format().getOffset(VertexFormatElement.COLOR);
            int normal=state.format().getOffset(VertexFormatElement.NORMAL);
            int light=state.format().getOffset(VertexFormatElement.UV2);
            vertices=0;
            if(position<0) return;
            int n=indices==null?state.vertexCount():state.indexCount();
            int total=switch(state.mode()) {
                case QUADS -> indices==null?n/4*6:n;
                case TRIANGLES -> n;
                case TRIANGLE_STRIP,TRIANGLE_FAN -> Math.max(0,n-2)*3;
                default -> 0;
            };
            if(data.length<total*14) data=Arrays.copyOf(data,Math.max(total*14,data.length*2));
            for(int i=0;i<total;i++) {
                int k=switch(state.mode()) {
                    case QUADS -> indices==null?(i/6*4+switch(i%6){case 0->0;case 1->1;case 2,3->2;case 4->3;default->0;}):i;
                    case TRIANGLE_STRIP -> {int triangle=i/3,corner=i%3;yield triangle+(triangle%2==1&&corner<2?1-corner:corner);}
                    case TRIANGLE_FAN -> i%3==0?0:i/3+i%3;
                    default -> i;
                };
                if(indices!=null) k=state.indexType()==VertexFormat.IndexType.SHORT?Short.toUnsignedInt(indices.getShort(k*2)):indices.getInt(k*4);
                int at=k*stride,p=vertices++*14;
                data[p]=source.getFloat(at+position);data[p+1]=source.getFloat(at+position+4);data[p+2]=source.getFloat(at+position+8);
                data[p+3]=uv<0?0:source.getFloat(at+uv);data[p+4]=uv<0?0:source.getFloat(at+uv+4);
                for(int c=0;c<4;c++) data[p+5+c]=colour<0?1F:Byte.toUnsignedInt(source.get(at+colour+c))/255F;
                for(int c=0;c<3;c++) data[p+9+c]=normal<0?(c==1?1F:0F):source.get(at+normal+c)/127F;
                data[p+12]=light<0?0:Short.toUnsignedInt(source.getShort(at+light));
                data[p+13]=light<0?0:Short.toUnsignedInt(source.getShort(at+light+2));
            }
        }
    }

    /** Only allocated on real flash frames. Restore global AND indexed blend/masks after the private pass. */
    private static final class State implements AutoCloseable {
        final int draw=GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING),read=GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);
        final int program=GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM),vao=GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING),array=GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);
        final int active=GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        final int[] textures=new int[9];
        final int[] viewport=new int[4],scissorBox=new int[4];
        final float[] clearColour=new float[4];
        final double clearDepth=GL11.glGetDouble(GL11.GL_DEPTH_CLEAR_VALUE);
        final double[] depthRange=new double[2];
        final boolean depth=GL11.glIsEnabled(GL11.GL_DEPTH_TEST),mask=GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK),cull=GL11.glIsEnabled(GL11.GL_CULL_FACE);
        final boolean scissor=GL11.glIsEnabled(GL11.GL_SCISSOR_TEST),stencil=GL11.glIsEnabled(GL11.GL_STENCIL_TEST),offset=GL11.glIsEnabled(GL11.GL_POLYGON_OFFSET_FILL),srgb=GL11.glIsEnabled(GL30.GL_FRAMEBUFFER_SRGB);
        final int depthFunction=GL11.glGetInteger(GL11.GL_DEPTH_FUNC);
        final boolean indexed=GL.getCapabilities().OpenGL40 || GL.getCapabilities().GL_ARB_draw_buffers_blend;
        final int[] src,dst,srcAlpha,dstAlpha,equation,equationAlpha;
        final boolean[] blend;
        final boolean[][] colour;
        State() {
            GL11.glGetIntegerv(GL11.GL_VIEWPORT,viewport);GL11.glGetIntegerv(GL11.GL_SCISSOR_BOX,scissorBox);
            GL11.glGetFloatv(GL11.GL_COLOR_CLEAR_VALUE,clearColour);GL11.glGetDoublev(GL11.GL_DEPTH_RANGE,depthRange);
            for(int i=0;i<textures.length;i++) {
                RenderSystem.activeTexture(GL13.GL_TEXTURE0+i);textures[i]=GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
            }
            RenderSystem.activeTexture(active);
            int n=GL11.glGetInteger(GL20.GL_MAX_DRAW_BUFFERS);
            src=new int[n];dst=new int[n];srcAlpha=new int[n];dstAlpha=new int[n];equation=new int[n];equationAlpha=new int[n];blend=new boolean[n];colour=new boolean[n][4];
            try(var stack=MemoryStack.stackPush()) {
                var bytes=stack.malloc(4);
                for(int i=0;i<n;i++) {
                    blend[i]=GL30.glIsEnabledi(GL11.GL_BLEND,i);
                    src[i]=factor(GL14.GL_BLEND_SRC_RGB,i);dst[i]=factor(GL14.GL_BLEND_DST_RGB,i);
                    srcAlpha[i]=factor(GL14.GL_BLEND_SRC_ALPHA,i);dstAlpha[i]=factor(GL14.GL_BLEND_DST_ALPHA,i);
                    equation[i]=factor(GL20.GL_BLEND_EQUATION_RGB,i);equationAlpha[i]=factor(GL20.GL_BLEND_EQUATION_ALPHA,i);
                    GL30.glGetBooleani_v(GL11.GL_COLOR_WRITEMASK,i,bytes);
                    for(int c=0;c<4;c++) colour[i][c]=bytes.get(c)!=0;
                }
            }
        }
        int factor(int key,int index) { return indexed?GL30.glGetIntegeri(key,index):GL11.glGetInteger(key); }
        @Override public void close() {
            BufferUploader.invalidate();
            GlStateManager._glBindVertexArray(vao);GlStateManager._glBindBuffer(GL15.GL_ARRAY_BUFFER,array);
            GlStateManager._glUseProgram(program);
            GlStateManager._glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER,draw);GlStateManager._glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER,read);
            RenderSystem.viewport(viewport[0],viewport[1],viewport[2],viewport[3]);
            if(scissor) RenderSystem.enableScissor(scissorBox[0],scissorBox[1],scissorBox[2],scissorBox[3]);else RenderSystem.disableScissor();
            if(depth) RenderSystem.enableDepthTest();else RenderSystem.disableDepthTest();
            RenderSystem.depthFunc(depthFunction);RenderSystem.depthMask(mask);GL11.glDepthRange(depthRange[0],depthRange[1]);
            if(cull) RenderSystem.enableCull();else RenderSystem.disableCull();
            toggle(GL11.GL_STENCIL_TEST,stencil);toggle(GL11.GL_POLYGON_OFFSET_FILL,offset);toggle(GL30.GL_FRAMEBUFFER_SRGB,srgb);
            if(blend[0]) RenderSystem.enableBlend();else RenderSystem.disableBlend();
            RenderSystem.blendFuncSeparate(src[0],dst[0],srcAlpha[0],dstAlpha[0]);
            GL20.glBlendEquationSeparate(equation[0],equationAlpha[0]);
            RenderSystem.colorMask(colour[0][0],colour[0][1],colour[0][2],colour[0][3]);
            for(int i=0;i<blend.length;i++) {
                if(blend[i]) GL30.glEnablei(GL11.GL_BLEND,i);else GL30.glDisablei(GL11.GL_BLEND,i);
                if(indexed) {
                    if(GL.getCapabilities().OpenGL40) {
                        GL40.glBlendFuncSeparatei(i,src[i],dst[i],srcAlpha[i],dstAlpha[i]);GL40.glBlendEquationSeparatei(i,equation[i],equationAlpha[i]);
                    } else {
                        ARBDrawBuffersBlend.glBlendFuncSeparateiARB(i,src[i],dst[i],srcAlpha[i],dstAlpha[i]);ARBDrawBuffersBlend.glBlendEquationSeparateiARB(i,equation[i],equationAlpha[i]);
                    }
                }
                GL30.glColorMaski(i,colour[i][0],colour[i][1],colour[i][2],colour[i][3]);
            }
            RenderSystem.clearColor(clearColour[0],clearColour[1],clearColour[2],clearColour[3]);RenderSystem.clearDepth(clearDepth);
            for(int i=0;i<textures.length;i++) {
                RenderSystem.activeTexture(GL13.GL_TEXTURE0+i);RenderSystem.bindTexture(textures[i]);
            }
            RenderSystem.activeTexture(active);
        }
        static void toggle(int cap,boolean on) { if(on) GL11.glEnable(cap);else GL11.glDisable(cap); }
    }
}
