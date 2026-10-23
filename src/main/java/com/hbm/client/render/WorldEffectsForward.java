package com.hbm.client.render;

import com.hbm.client.weapon.pepperbox.GunShaderCompat;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import org.joml.Matrix4f;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;

/**
 * Source world effects are colour, not an arbitrary shader pack's encoded MRT material.
 * Keep their real shader/geometry/blend and draw after Iris's final world composite,
 * before colour-space conversion and GUI. The main WORLD depth is retained, never
 * cleared or replaced with the independent first-person hand depth used by141.
 */
public final class WorldEffectsForward {
    private static final ArrayList<Draw> PENDING=new ArrayList<>();
    private static boolean collecting,replaying,shadows;
    private WorldEffectsForward(){}

    /** Native frame entry also drops references left by an interrupted preceding render. */
    public static void beginFrame(){try{clearPending(null);}finally{collecting=false;replaying=false;shadows=false;}}
    public static void beginWorld(){collecting=GunShaderCompat.shaderPackInUse();}
    public static void shadows(boolean value){shadows=value;}
    public static boolean sourcePass(){return replaying;}
    public static boolean pack(){return GunShaderCompat.shaderPackInUse();}
    /** Return true only when the original draw+flush was retained; otherwise draw normally. */
    public static boolean acceptsSourceDraw(){return collecting&&!replaying&&!shadows&&pack();}
    public static boolean defer(Runnable draw){return defer(draw,()->{});}
    /** Optional owned-resource cleanup also runs for interrupted frames; there is still one world queue. */
    public static boolean defer(Runnable draw,Runnable cleanup){
        if(!acceptsSourceDraw())return false;
        PENDING.add(new Draw(draw,new Context(),cleanup));return true;
    }

    /** Iris finalizeLevelRendering RETURN: isRenderingWorld=false and finalPass has returned main. */
    public static void finishWorld(){
        collecting=false;
        if(PENDING.isEmpty())return;
        var mc=Minecraft.getInstance();
        if(mc.level==null||!pack()){beginFrame();return;}
        replaying=true;Throwable failure=null;
        try(var saved=new State()){
            mc.getMainRenderTarget().bindWrite(true);
            RenderSystem.disableScissor();GL11.glDisable(GL11.GL_STENCIL_TEST);
            GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);GL11.glDisable(GL30.GL_FRAMEBUFFER_SRGB);
            GL11.glDepthRange(0,1);
            RenderSystem.enableDepthTest();GL11.glEnable(GL11.GL_DEPTH_TEST);
            RenderSystem.depthFunc(GL11.GL_LEQUAL);GL11.glDepthFunc(GL11.GL_LEQUAL);
            RenderSystem.depthMask(false);GL11.glDepthMask(false);
            RenderSystem.disableBlend();GL30.glDisablei(GL11.GL_BLEND,0);
            RenderSystem.colorMask(true,true,true,true);
            GL30.glColorMaski(0,true,true,true,true);
            GL20.glBlendEquationSeparate(GL14.GL_FUNC_ADD,GL14.GL_FUNC_ADD);
            mc.gameRenderer.lightTexture().turnOnLightLayer();
            for(var draw:PENDING){draw.context.apply();draw.action.run();}
        }catch(RuntimeException|Error thrown){failure=thrown;throw thrown;}
        finally{
            // All retained resources close, including later callbacks skipped by a real draw failure.
            try{clearPending(failure);}finally{replaying=false;shadows=false;}
        }
    }

    private static void clearPending(Throwable original){
        Throwable cleanupFailure=null;
        try{for(var draw:PENDING)try{draw.cleanup.run();}catch(RuntimeException|Error thrown){
            if(original!=null)original.addSuppressed(thrown);
            else if(cleanupFailure==null)cleanupFailure=thrown;else cleanupFailure.addSuppressed(thrown);
        }}finally{PENDING.clear();}
        if(cleanupFailure instanceof RuntimeException thrown)throw thrown;
        if(cleanupFailure instanceof Error thrown)throw thrown;
    }
    private record Draw(Runnable action,Context context,Runnable cleanup){}
    /** Capture at submission, not after Iris has installed hand/GUI projection or fog. */
    private static final class Context {
        final Matrix4f projection=new Matrix4f(RenderSystem.getProjectionMatrix());
        final Matrix4f modelView=new Matrix4f(RenderSystem.getModelViewMatrix());
        final VertexSorting sorting=RenderSystem.getVertexSorting();
        final float fogStart=RenderSystem.getShaderFogStart(),fogEnd=RenderSystem.getShaderFogEnd();
        final float[] fog=RenderSystem.getShaderFogColor().clone(),colour=RenderSystem.getShaderColor().clone();
        final FogShape shape=RenderSystem.getShaderFogShape();
        final int[] textures=new int[12];
        Context(){for(int i=0;i<textures.length;i++)textures[i]=RenderSystem.getShaderTexture(i);}
        void apply(){
            RenderSystem.setProjectionMatrix(new Matrix4f(projection),sorting);
            RenderSystem.getModelViewStack().set(modelView);RenderSystem.applyModelViewMatrix();
            RenderSystem.setShaderFogStart(fogStart);RenderSystem.setShaderFogEnd(fogEnd);
            RenderSystem.setShaderFogColor(fog[0],fog[1],fog[2],fog[3]);RenderSystem.setShaderFogShape(shape);
            RenderSystem.setShaderColor(colour[0],colour[1],colour[2],colour[3]);
            for(int i=0;i<textures.length;i++)RenderSystem.setShaderTexture(i,textures[i]);
        }
    }

    /** Restore actual GL state and RenderSystem's cached values, including indexed MRT state. */
    private static final class State implements AutoCloseable {
        final Context context=new Context();
        final ShaderInstance shader=RenderSystem.getShader();
        final int draw=GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING),read=GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);
        final int program=GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM),vao=GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING),array=GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);
        final int active=GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        final int[] textures=new int[12],viewport=new int[4],scissorBox=new int[4];
        final double[] depthRange=new double[2];
        final boolean depth=GL11.glIsEnabled(GL11.GL_DEPTH_TEST),mask=GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK),cull=GL11.glIsEnabled(GL11.GL_CULL_FACE);
        final boolean scissor=GL11.glIsEnabled(GL11.GL_SCISSOR_TEST),stencil=GL11.glIsEnabled(GL11.GL_STENCIL_TEST),offset=GL11.glIsEnabled(GL11.GL_POLYGON_OFFSET_FILL),srgb=GL11.glIsEnabled(GL30.GL_FRAMEBUFFER_SRGB);
        final int depthFunction=GL11.glGetInteger(GL11.GL_DEPTH_FUNC);
        final boolean indexed=GL.getCapabilities().OpenGL40||GL.getCapabilities().GL_ARB_draw_buffers_blend;
        final int[] src,dst,srcAlpha,dstAlpha,equation,equationAlpha;
        final boolean[] blend;final boolean[][] colour;
        State(){
            GL11.glGetIntegerv(GL11.GL_VIEWPORT,viewport);GL11.glGetIntegerv(GL11.GL_SCISSOR_BOX,scissorBox);
            GL11.glGetDoublev(GL11.GL_DEPTH_RANGE,depthRange);
            for(int i=0;i<textures.length;i++){RenderSystem.activeTexture(GL13.GL_TEXTURE0+i);textures[i]=GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);}
            RenderSystem.activeTexture(active);
            int n=GL11.glGetInteger(GL20.GL_MAX_DRAW_BUFFERS);
            src=new int[n];dst=new int[n];srcAlpha=new int[n];dstAlpha=new int[n];equation=new int[n];equationAlpha=new int[n];blend=new boolean[n];colour=new boolean[n][4];
            try(var stack=MemoryStack.stackPush()){
                var bytes=stack.malloc(4);
                for(int i=0;i<n;i++){
                    blend[i]=GL30.glIsEnabledi(GL11.GL_BLEND,i);
                    src[i]=factor(GL14.GL_BLEND_SRC_RGB,i);dst[i]=factor(GL14.GL_BLEND_DST_RGB,i);
                    srcAlpha[i]=factor(GL14.GL_BLEND_SRC_ALPHA,i);dstAlpha[i]=factor(GL14.GL_BLEND_DST_ALPHA,i);
                    equation[i]=factor(GL20.GL_BLEND_EQUATION_RGB,i);equationAlpha[i]=factor(GL20.GL_BLEND_EQUATION_ALPHA,i);
                    GL30.glGetBooleani_v(GL11.GL_COLOR_WRITEMASK,i,bytes);
                    for(int c=0;c<4;c++)colour[i][c]=bytes.get(c)!=0;
                }
            }
        }
        int factor(int key,int index){return indexed?GL30.glGetIntegeri(key,index):GL11.glGetInteger(key);}
        @Override public void close(){
            context.apply();RenderSystem.setShader(()->shader);
            BufferUploader.invalidate();GlStateManager._glBindVertexArray(vao);GlStateManager._glBindBuffer(GL15.GL_ARRAY_BUFFER,array);
            GlStateManager._glUseProgram(program);
            GlStateManager._glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER,draw);GlStateManager._glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER,read);
            RenderSystem.viewport(viewport[0],viewport[1],viewport[2],viewport[3]);
            if(scissor)RenderSystem.enableScissor(scissorBox[0],scissorBox[1],scissorBox[2],scissorBox[3]);else RenderSystem.disableScissor();
            if(depth)RenderSystem.enableDepthTest();else RenderSystem.disableDepthTest();
            RenderSystem.depthFunc(depthFunction);RenderSystem.depthMask(mask);GL11.glDepthRange(depthRange[0],depthRange[1]);
            if(cull)RenderSystem.enableCull();else RenderSystem.disableCull();
            toggle(GL11.GL_STENCIL_TEST,stencil);toggle(GL11.GL_POLYGON_OFFSET_FILL,offset);toggle(GL30.GL_FRAMEBUFFER_SRGB,srgb);
            if(blend[0])RenderSystem.enableBlend();else RenderSystem.disableBlend();
            RenderSystem.blendFuncSeparate(src[0],dst[0],srcAlpha[0],dstAlpha[0]);GL20.glBlendEquationSeparate(equation[0],equationAlpha[0]);
            RenderSystem.colorMask(colour[0][0],colour[0][1],colour[0][2],colour[0][3]);
            for(int i=0;i<blend.length;i++){
                if(blend[i])GL30.glEnablei(GL11.GL_BLEND,i);else GL30.glDisablei(GL11.GL_BLEND,i);
                if(indexed){
                    if(GL.getCapabilities().OpenGL40){GL40.glBlendFuncSeparatei(i,src[i],dst[i],srcAlpha[i],dstAlpha[i]);GL40.glBlendEquationSeparatei(i,equation[i],equationAlpha[i]);}
                    else{ARBDrawBuffersBlend.glBlendFuncSeparateiARB(i,src[i],dst[i],srcAlpha[i],dstAlpha[i]);ARBDrawBuffersBlend.glBlendEquationSeparateiARB(i,equation[i],equationAlpha[i]);}
                }
                GL30.glColorMaski(i,colour[i][0],colour[i][1],colour[i][2],colour[i][3]);
            }
            for(int i=0;i<textures.length;i++){RenderSystem.activeTexture(GL13.GL_TEXTURE0+i);RenderSystem.bindTexture(textures[i]);}
            RenderSystem.activeTexture(active);
        }
        static void toggle(int cap,boolean on){if(on)GL11.glEnable(cap);else GL11.glDisable(cap);}
    }
    @EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
    public static final class Lifecycle {
        @SubscribeEvent public static void logout(ClientPlayerNetworkEvent.LoggingOut event){beginFrame();}
    }
}
