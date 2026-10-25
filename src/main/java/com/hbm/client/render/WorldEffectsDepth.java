package com.hbm.client.render;

import com.hbm.client.bomb.nuclear.MukeRenderTypes;
import com.hbm.client.bomb.nuclear.NuclearTorexRenderer;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.function.BooleanSupplier;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.culling.Frustum;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

/** Same-frame source coverage before Iris copies depthtex1; source colour keeps its sole late queue. */
@EventBusSubscriber(modid="hbm", value=Dist.CLIENT)
public final class WorldEffectsDepth {
    private static final ArrayList<Contributor> CONTRIBUTORS=new ArrayList<>();
    private static final ArrayList<ShaderInstance> COLOUR_SHADERS=new ArrayList<>();
    private static DepthSnapshot before,nearest,current;
    private static Camera camera;
    private static Frustum frustum;
    private static Matrix4f pose;
    private static WorldEffectsForward.Context context;
    private static Object level;
    private static float partial;
    private static int capturedDepth;
    private static boolean attempted,ready,depthPass,colourPass;
    private WorldEffectsDepth(){}

    /** Fixed actual-owner registrations, not another per-frame effect or mesh queue. */
    public static void registerWorldGeometry(BooleanSupplier hasGeometry,Runnable drawDepth){
        CONTRIBUTORS.add(new Contributor(hasGeometry,drawDepth));
    }
    public static boolean earlyDepthPass(){return depthPass;}

    @SubscribeEvent public static void frameContext(RenderLevelStageEvent event){
        if(event.getStage()!=RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES||!WorldEffectsForward.acceptsSourceDraw())return;
        camera=event.getCamera();frustum=event.getFrustum();pose=new Matrix4f(event.getPoseStack().last().pose());
        partial=event.getPartialTick().getGameTimeDeltaPartialTick(false);
        context=new WorldEffectsForward.Context();level=Minecraft.getInstance().level;
    }

    /** Called at Iris beginTranslucents HEAD, before its pre-translucent depth copy and deferred passes. */
    public static void capture(){
        if(attempted||context==null||!WorldEffectsForward.acceptsSourceDraw())return;
        attempted=true;var mc=Minecraft.getInstance();
        if(mc.level==null||mc.level!=level)return;
        boolean smoke=MukeRenderTypes.hasDepthSmoke(),torex=NuclearTorexRenderer.hasDepthClouds(),meshes=WorldEffectsForward.hasDepthDraws();
        boolean extra=false;for(var contributor:CONTRIBUTORS)extra|=contributor.hasGeometry.getAsBoolean();
        if(!smoke&&!torex&&!meshes&&!extra)return;
        try(var saved=new WorldEffectsForward.State()){
            var main=mc.getMainRenderTarget();targets(main,saved);before.copyDepthFrom(main);
            main.bindWrite(true);context.apply();
            RenderSystem.disableScissor();GL11.glDisable(GL11.GL_STENCIL_TEST);
            GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);GL11.glDisable(GL30.GL_FRAMEBUFFER_SRGB);
            GL11.glDepthRange(0,1);RenderSystem.enableDepthTest();GL11.glEnable(GL11.GL_DEPTH_TEST);
            RenderSystem.depthFunc(GL11.GL_LEQUAL);GL11.glDepthFunc(GL11.GL_LEQUAL);
            RenderSystem.depthMask(true);GL11.glDepthMask(true);
            RenderSystem.colorMask(false,false,false,false);GL30.glColorMaski(0,false,false,false,false);
            depthPass=true;
            try{
                if(smoke)MukeRenderTypes.renderEarlyDepth(camera,frustum,partial);
                if(torex)NuclearTorexRenderer.renderEarlyDepth(camera,partial,pose);
                if(meshes)WorldEffectsForward.renderDepth();
                for(var contributor:CONTRIBUTORS)if(contributor.hasGeometry.getAsBoolean()){
                    // Every actual owner starts from the same main depth-only state.
                    main.bindWrite(true);context.apply();
                    RenderSystem.depthFunc(GL11.GL_LEQUAL);GL11.glDepthFunc(GL11.GL_LEQUAL);
                    RenderSystem.enableDepthTest();GL11.glEnable(GL11.GL_DEPTH_TEST);
                    RenderSystem.depthMask(true);GL11.glDepthMask(true);
                    RenderSystem.colorMask(false,false,false,false);GL30.glColorMaski(0,false,false,false,false);
                    contributor.drawDepth.run();
                }
            }finally{depthPass=false;}
            nearest.copyDepthFrom(main);capturedDepth=main.getDepthTextureId();ready=true;
        }
    }

    /** B is copied at the actual late source callback, including earlier legitimate source depth writers. */
    public static ColourScope colour(){
        if(colourPass)return new ColourScope(true,null);
        var mc=Minecraft.getInstance();
        if(!ready||!WorldEffectsForward.sourcePass()||mc.level!=level)return new ColourScope(false,null);
        var target=mc.getMainRenderTarget();
        if(target.width!=before.width||target.height!=before.height||target.getDepthTextureId()!=capturedDepth)return new ColourScope(false,null);
        var saved=new WorldEffectsForward.State();
        try{
            var main=mc.getMainRenderTarget();current.copyDepthFrom(main);main.bindWrite(true);
            colourPass=true;return new ColourScope(true,saved);
        }catch(RuntimeException|Error failure){saved.close();throw failure;}
    }

    /** Called by actual source shader owners; independent sampler images never alias the draw attachment. */
    public static ShaderInstance shader(ShaderInstance shader){
        if(shader==null)return null;
        shader.safeGetUniform("HbmSmokeCutoff").set(colourPass?1F:0F);
        if(colourPass){
            RenderSystem.setShaderTexture(6,before.getDepthTextureId());
            RenderSystem.setShaderTexture(7,nearest.getDepthTextureId());
            RenderSystem.setShaderTexture(8,current.getDepthTextureId());
            shader.setSampler("Sampler6",before.getDepthTextureId());
            shader.setSampler("Sampler7",nearest.getDepthTextureId());
            shader.setSampler("Sampler8",current.getDepthTextureId());
            if(!COLOUR_SHADERS.contains(shader))COLOUR_SHADERS.add(shader);
        }else clearSamplers(shader);
        return shader;
    }
    private static void clearSamplers(ShaderInstance shader){
        shader.setSampler("Sampler6",null);shader.setSampler("Sampler7",null);shader.setSampler("Sampler8",null);
    }

    public static final class ColourScope implements AutoCloseable {
        private final boolean active;
        private final WorldEffectsForward.State saved;
        private boolean closed;
        private ColourScope(boolean active,WorldEffectsForward.State saved){this.active=active;this.saved=saved;}
        public boolean active(){return active;}
        @Override public void close(){
            if(closed)return;closed=true;if(saved==null)return;
            try{clearColour();}finally{saved.close();}
        }
    }

    static void resetFrame(){
        clearColour();attempted=false;ready=false;depthPass=false;
        camera=null;frustum=null;pose=null;context=null;level=null;capturedDepth=0;
    }
    private static void clearColour(){
        colourPass=false;
        for(var shader:COLOUR_SHADERS){shader.safeGetUniform("HbmSmokeCutoff").set(0F);clearSamplers(shader);}
        COLOUR_SHADERS.clear();
    }
    public static void release(){
        resetFrame();
        try(var saved=new WorldEffectsForward.State()){destroyTargets(saved);}
    }

    private static void targets(RenderTarget main,WorldEffectsForward.State saved){
        RenderSystem.activeTexture(GL13.GL_TEXTURE0);RenderSystem.bindTexture(main.getDepthTextureId());
        int format=GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D,0,GL11.GL_TEXTURE_INTERNAL_FORMAT);
        if(before!=null&&before.width==main.width&&before.height==main.height&&before.format==format)return;
        destroyTargets(saved);
        try{
            before=new DepthSnapshot(main.width,main.height,format);
            nearest=new DepthSnapshot(main.width,main.height,format);
            current=new DepthSnapshot(main.width,main.height,format);
        }catch(RuntimeException|Error failure){destroyTargets(saved);throw failure;}
    }
    private static void destroyTargets(WorldEffectsForward.State saved){
        // Drop native cached slot references as well as ShaderInstance sampler objects before releasing IDs.
        for(int slot=0;slot<12;slot++){
            int texture=RenderSystem.getShaderTexture(slot);
            if(before!=null&&texture==before.getDepthTextureId()||nearest!=null&&texture==nearest.getDepthTextureId()||current!=null&&texture==current.getDepthTextureId())RenderSystem.setShaderTexture(slot,0);
        }
        if(before!=null){saved.forgetTexture(before.getDepthTextureId());before.destroyBuffers();}
        if(nearest!=null){saved.forgetTexture(nearest.getDepthTextureId());nearest.destroyBuffers();}
        if(current!=null){saved.forgetTexture(current.getDepthTextureId());current.destroyBuffers();}
        before=null;nearest=null;current=null;
    }
    private record Contributor(BooleanSupplier hasGeometry,Runnable drawDepth){}

    /** Native depth-copy API, exact queried source precision, no unused colour image or attachment feedback. */
    private static final class DepthSnapshot extends RenderTarget {
        final int format;
        DepthSnapshot(int width,int height,int format){
            super(true);this.format=format;this.width=width;this.height=height;viewWidth=width;viewHeight=height;
            frameBufferId=GlStateManager.glGenFramebuffers();depthBufferId=TextureUtil.generateTextureId();
            try{
                RenderSystem.bindTexture(depthBufferId);
                GlStateManager._texParameter(GL11.GL_TEXTURE_2D,GL11.GL_TEXTURE_MIN_FILTER,GL11.GL_NEAREST);
                GlStateManager._texParameter(GL11.GL_TEXTURE_2D,GL11.GL_TEXTURE_MAG_FILTER,GL11.GL_NEAREST);
                GlStateManager._texParameter(GL11.GL_TEXTURE_2D,GL12.GL_TEXTURE_BASE_LEVEL,0);
                GlStateManager._texParameter(GL11.GL_TEXTURE_2D,GL12.GL_TEXTURE_MAX_LEVEL,0);
                GlStateManager._texParameter(GL11.GL_TEXTURE_2D,GL14.GL_TEXTURE_COMPARE_MODE,GL11.GL_NONE);
                GlStateManager._texParameter(GL11.GL_TEXTURE_2D,GL11.GL_TEXTURE_WRAP_S,GL12.GL_CLAMP_TO_EDGE);
                GlStateManager._texParameter(GL11.GL_TEXTURE_2D,GL11.GL_TEXTURE_WRAP_T,GL12.GL_CLAMP_TO_EDGE);
                boolean stencil=format==GL30.GL_DEPTH24_STENCIL8||format==GL30.GL_DEPTH32F_STENCIL8;
                int external=stencil?GL30.GL_DEPTH_STENCIL:GL11.GL_DEPTH_COMPONENT;
                int type=stencil?(format==GL30.GL_DEPTH32F_STENCIL8?GL30.GL_FLOAT_32_UNSIGNED_INT_24_8_REV:GL30.GL_UNSIGNED_INT_24_8):GL11.GL_FLOAT;
                GlStateManager._texImage2D(GL11.GL_TEXTURE_2D,0,format,width,height,0,external,type,null);
                GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER,frameBufferId);
                GlStateManager._glFramebufferTexture2D(GL30.GL_FRAMEBUFFER,stencil?GL30.GL_DEPTH_STENCIL_ATTACHMENT:GL30.GL_DEPTH_ATTACHMENT,GL11.GL_TEXTURE_2D,depthBufferId,0);
                GL11.glDrawBuffer(GL11.GL_NONE);GL11.glReadBuffer(GL11.GL_NONE);checkStatus();
            }catch(RuntimeException|Error failure){destroyBuffers();throw failure;}
        }
    }
}
