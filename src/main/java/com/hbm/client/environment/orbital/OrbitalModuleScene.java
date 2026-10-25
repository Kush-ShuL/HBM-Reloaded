package com.hbm.client.environment.orbital;

import com.hbm.LegacyStartup;
import com.hbm.client.config.ClientEffectsConfig;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.client.model.LegacyObjModels;
import com.hbm.client.model.LegacyObjRenderer;
import com.hbm.client.model.WavefrontMesh;
import com.hbm.client.render.WorldEffectsForward;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.lwjgl.opengl.GL11;

/** The source fixed world scene; no registered block, entity, collision or saved world object. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class OrbitalModuleScene {
    private static final Vec3 ANCHOR=new Vec3(0,500,0);
    private static final double RANGE_SQUARED=300D*300D;
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/soyuz_module.obj");
    // Source OpenGlHelper lightmap coordinates: block U6500 and sky V30, retained for native lightmap sampling.
    private static final int SOURCE_LIGHT=6500|(30<<16);
    private static final MultiBufferSource.BufferSource BUFFERS=MultiBufferSource.immediate(new ByteBufferBuilder(262144));
    private OrbitalModuleScene(){}

    @SubscribeEvent public static void render(RenderLevelStageEvent event){
        if(event.getStage()!=RenderLevelStageEvent.Stage.AFTER_WEATHER||WorldEffectsForward.shadowPass())return;
        var minecraft=Minecraft.getInstance();
        if(minecraft.level==null||minecraft.player==null||!minecraft.level.dimension().equals(Level.OVERWORLD))return;
        var messages=OrbitalMessageSource.messages();
        if(messages.isEmpty())return;
        float partial=event.getPartialTick().getGameTimeDeltaPartialTick(false);
        if(minecraft.player.getPosition(partial).distanceToSqr(ANCHOR)>=RANGE_SQUARED)return;
        String message=messages.get(new Random(LegacyStartup.startupTime).nextInt(messages.size()));
        var pose=new PoseStack();
        pose.last().pose().set(event.getPoseStack().last().pose());
        pose.last().normal().set(event.getPoseStack().last().normal());
        var camera=event.getCamera().getPosition();pose.translate(ANCHOR.x-camera.x,ANCHOR.y-camera.y,ANCHOR.z-camera.z);
        long millis=System.currentTimeMillis();
        Runnable draw=()->draw(pose,millis,message);
        if(!WorldEffectsForward.defer(draw))draw.run();
    }

    private static void draw(PoseStack pose,long millis,String message){
        boolean oldWrite=GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK),oldDepth=GL11.glIsEnabled(GL11.GL_DEPTH_TEST),oldCull=GL11.glIsEnabled(GL11.GL_CULL_FACE);
        int oldFunction=GL11.glGetInteger(GL11.GL_DEPTH_FUNC);float[] oldColour=RenderSystem.getShaderColor().clone();
        pose.pushPose();
        try{
            // Keep world depth and real opaque self-occlusion, including the forward queue's normally read-only depth pass.
            RenderSystem.enableDepthTest();RenderSystem.depthFunc(GL11.GL_LEQUAL);RenderSystem.depthMask(true);RenderSystem.enableCull();RenderSystem.setShaderColor(1,1,1,1);
            pose.mulPose(Axis.ZP.rotationDegrees(80));pose.mulPose(Axis.YP.rotationDegrees(30));
            pose.mulPose(Axis.ZP.rotationDegrees((float)(Math.sin(millis*.0005)*5)));
            pose.mulPose(Axis.XP.rotationDegrees((float)(Math.sin(millis*.0005+Math.PI*.5)*5)));
            pose.translate(0,-3,0);
            var model=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);
            part(model,pose,"Dome","dome");part(model,pose,"Capsule","lander");part(model,pose,"Propulsion","propulsion");part(model,pose,"Solar","solar");
            BUFFERS.endBatch();
            pose.mulPose(Axis.YN.rotationDegrees((float)(millis*.025%360)));
            pose.translate(0,3.75,0);pose.mulPose(Axis.XP.rotationDegrees(180));
            text(pose,message);
        }finally{
            try{BUFFERS.endBatch();}finally{
                pose.popPose();RenderSystem.depthMask(oldWrite);RenderSystem.depthFunc(oldFunction);
                if(oldDepth)RenderSystem.enableDepthTest();else RenderSystem.disableDepthTest();
                if(oldCull)RenderSystem.enableCull();else RenderSystem.disableCull();
                RenderSystem.setShaderColor(oldColour[0],oldColour[1],oldColour[2],oldColour[3]);
            }
        }
    }
    private static void part(WavefrontMesh model,PoseStack pose,String group,String texture){
        var material=RenderType.entityCutout(ResourceLocation.parse("hbm:textures/models/soyuz_capsule/module_"+texture+".png"));
        LegacyObjRenderer.renderOnly(model,pose,BUFFERS.getBuffer(material),SOURCE_LIGHT,OverlayTexture.NO_OVERLAY,-1,LegacyObjRenderer.Convention.HFR,group);
    }
    private static void text(PoseStack pose,String message){
        var font=LegacyFonts.get();float width=font.width(message);
        // A blank selected line still leaves the real module visible, exactly like a nonempty source message list.
        if(width<=0)return;
        float scale=5F/width,rotation=0;
        for(char character:message.toCharArray()){
            pose.pushPose();pose.mulPose(Axis.YP.rotationDegrees(rotation));
            String glyph=String.valueOf(character);rotation-=font.width(glyph)*scale*50;
            pose.translate(2,0,0);pose.mulPose(Axis.YN.rotationDegrees(90));pose.scale(scale,scale,scale);
            font.drawInBatch(glyph,0,0,0xffff00ff,false,pose.last().pose(),BUFFERS,Font.DisplayMode.NORMAL,0,LightTexture.FULL_BRIGHT);
            pose.popPose();
        }
    }
}
