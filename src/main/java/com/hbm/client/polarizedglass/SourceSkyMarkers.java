package com.hbm.client.polarizedglass;

import com.hbm.block.polarizedglass.PolarizedGlassContent;
import com.hbm.environment.impact.ImpactState;
import com.hbm.exposure.HbmExposure;
import com.hbm.weapon.sedna.gun.quadro.QuadroTargetRay;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

/** The real normal-overworld chainloader overlay, after the existing native/custom sky.
 * Impact's already-ported sky keeps ownership of its own markers while dust/fire is active.
 * No replacement of DimensionSpecialEffects, second shader or second world-effect queue. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class SourceSkyMarkers {
    private static boolean renderLodeStar;
    private static long lastStarCheck;

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void tick(ClientTickEvent.Pre event){
        var mc=Minecraft.getInstance();var level=mc.level;
        if(level==null||!ImpactState.skyboxes())return;
        long millis=System.currentTimeMillis(); // Source Clock.update also caches wall-clock milliseconds.
        if(lastStarCheck+200<millis){
            lastStarCheck=millis;renderLodeStar=false;
            if(mc.player!=null){
                // Vec3NT.rotateAroundXDeg(-15) uses these double operations, from feet rather than eyes.
                double angle=-15D/180D*Math.PI;
                var start=new Vec3(mc.player.getX(),mc.player.getY(),mc.player.getZ());
                var end=start.add(0,-Math.sin(angle)*25D,-Math.cos(angle)*25D);
                var hit=QuadroTargetRay.traceSolidBlock(level,start,end,mc.player);
                renderLodeStar=hit!=null&&level.getBlockState(hit).is(PolarizedGlassContent.GLASS.get());
            }
        }
    }

    @SubscribeEvent public static void sky(RenderLevelStageEvent event){
        if(event.getStage()!=RenderLevelStageEvent.Stage.AFTER_SKY)return;
        var mc=Minecraft.getInstance();var world=mc.level;
        if(world==null||mc.player==null||world.dimension()!=Level.OVERWORLD||!ImpactState.skyboxes()||ImpactState.get(world).active())return;
        var pose=new PoseStack();pose.mulPose(event.getModelViewMatrix());
        float partial=event.getPartialTick().getGameTimeDeltaPartialTick(false);
        try(var ignored=new MarkerState()){
            RenderSystem.setShaderColor(1,1,1,1);
            // One draw-time world RNG sample even when polarized glass was not hit.
            float size=.5F+world.random.nextFloat()*.25F;
            if(renderLodeStar){
                pose.pushPose();pose.mulPose(Axis.XP.rotationDegrees(-75));pose.mulPose(Axis.YP.rotationDegrees(10));
                quad(pose,SourceSkyMarkerTypes.LODE,size,100D,1);pose.popPose();
            }
            float brightness=(float)Math.sin(world.getTimeOfDay(partial)*Math.PI);brightness*=brightness;
            pose.pushPose();pose.mulPose(Axis.YP.rotationDegrees(-90));pose.mulPose(Axis.XP.rotationDegrees(world.getTimeOfDay(partial)*360F));
            pose.mulPose(Axis.XP.rotationDegrees(140));pose.mulPose(Axis.ZP.rotationDegrees(-40));
            float digamma=HbmExposure.digamma(mc.player);
            quad(pose,SourceSkyMarkerTypes.DIGAMMA,1F*(1+digamma*.25F),100D-digamma*2.5,brightness);pose.popPose();
            pose.pushPose();pose.mulPose(Axis.XP.rotationDegrees(-40));
            pose.mulPose(Axis.YP.rotationDegrees((System.currentTimeMillis()%(360*1000))/1000F));
            pose.mulPose(Axis.XP.rotationDegrees((System.currentTimeMillis()%(360*100))/100F));
            quad(pose,SourceSkyMarkerTypes.BOBMAZON,.5F,100D,brightness);pose.popPose();
        }
    }
    /** Source TL/TR/BR/BL positions and deliberately rotated 00/01/11/10 UV order. */
    private static void quad(PoseStack pose,RenderType type,float size,double distance,float color){
        // Source glColor4f is a float uniform, not brightness rounded into an 8-bit vertex colour.
        RenderSystem.setShaderColor(color,color,color,1);
        try(var memory=new ByteBufferBuilder(256)){
            var out=new BufferBuilder(memory,VertexFormat.Mode.QUADS,DefaultVertexFormat.POSITION_TEX_COLOR);
            out.addVertex(pose.last(),-size,(float)distance,-size).setUv(0,0).setColor(255,255,255,255);
            out.addVertex(pose.last(),size,(float)distance,-size).setUv(0,1).setColor(255,255,255,255);
            out.addVertex(pose.last(),size,(float)distance,size).setUv(1,1).setColor(255,255,255,255);
            out.addVertex(pose.last(),-size,(float)distance,size).setUv(1,0).setColor(255,255,255,255);
            // The finite173 material registration preserves these bytes and original shader under Iris.
            type.draw(out.buildOrThrow());
        }
    }
    /** RenderType clear restores defaults; restore the actual incoming sky state, also on failure. */
    private static final class MarkerState implements AutoCloseable {
        final net.minecraft.client.renderer.ShaderInstance shader=RenderSystem.getShader();
        final int texture=RenderSystem.getShaderTexture(0),depthFunction=GL11.glGetInteger(GL11.GL_DEPTH_FUNC);
        final float[] color=RenderSystem.getShaderColor().clone();
        final boolean depth=GL11.glIsEnabled(GL11.GL_DEPTH_TEST),depthWrite=GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK),cull=GL11.glIsEnabled(GL11.GL_CULL_FACE),blend=GL11.glIsEnabled(GL11.GL_BLEND);
        final int source=GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB),destination=GL11.glGetInteger(GL14.GL_BLEND_DST_RGB),sourceAlpha=GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA),destinationAlpha=GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA);
        @Override public void close(){
            RenderSystem.depthFunc(depthFunction);RenderSystem.depthMask(depthWrite);
            if(depth)RenderSystem.enableDepthTest();else RenderSystem.disableDepthTest();
            if(cull)RenderSystem.enableCull();else RenderSystem.disableCull();
            RenderSystem.blendFuncSeparate(source,destination,sourceAlpha,destinationAlpha);
            if(blend)RenderSystem.enableBlend();else RenderSystem.disableBlend();
            RenderSystem.setShaderTexture(0,texture);RenderSystem.setShaderColor(color[0],color[1],color[2],color[3]);RenderSystem.setShader(()->shader);
        }
    }
    private SourceSkyMarkers(){}
}
