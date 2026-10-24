package com.hbm.client.reactor.pwr;

import com.hbm.machine.reactor.pwr.PwrPackets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

/** Source RenderOverhead marker: red12-edge box,5second lifetime,128range and distance-aware label. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class PwrMarkers {
    private record Marker(Component label,int color,long expires,double distance){}
    private static final Map<BlockPos,Marker> MARKERS=new LinkedHashMap<>();
    public static void add(PwrPackets.Marker packet){add(packet.position(),Component.translatable(packet.label()),0xff0000,5000,128);}
    /** Original AuxParticle marker fields, shared by PWR and the actual SurveyScanner. */
    public static void add(BlockPos pos,Component label,int color,int expires,double distance){MARKERS.put(pos.immutable(),new Marker(label==null?null:label.copy(),color,expires>0?System.currentTimeMillis()+expires:0,distance));}
    @SubscribeEvent public static void unload(LevelEvent.Unload event){if(event.getLevel().isClientSide())MARKERS.clear();}
    @SubscribeEvent public static void render(RenderLevelStageEvent event){if(event.getStage()!=RenderLevelStageEvent.Stage.AFTER_PARTICLES||!com.hbm.client.armor.t51.T51ClientEvents.sourceHudEnabled()||MARKERS.isEmpty())return;var minecraft=Minecraft.getInstance();if(minecraft.player==null)return;var camera=event.getCamera();var eye=camera.getPosition();var poses=event.getPoseStack();MARKERS.entrySet().removeIf(entry->(entry.getValue().expires>0&&System.currentTimeMillis()>entry.getValue().expires)||(entry.getValue().distance>0&&Vec3.atCenterOf(entry.getKey()).distanceToSqr(eye)>entry.getValue().distance*entry.getValue().distance));if(MARKERS.isEmpty())return;
        poses.pushPose();poses.translate(-eye.x,-eye.y,-eye.z);RenderSystem.setShader(GameRenderer::getPositionColorShader);RenderSystem.disableDepthTest();RenderSystem.depthMask(false);RenderSystem.enableBlend();RenderSystem.defaultBlendFunc();var buffer=Tesselator.getInstance().begin(VertexFormat.Mode.DEBUG_LINES,DefaultVertexFormat.POSITION_COLOR);for(var entry:MARKERS.entrySet())for(int axis=0;axis<3;axis++)for(int a=0;a<2;a++)for(int b=0;b<2;b++){var pos=entry.getKey();float[] start={pos.getX(),pos.getY(),pos.getZ()};start[(axis+1)%3]+=a;start[(axis+2)%3]+=b;buffer.addVertex(poses.last().pose(),start[0],start[1],start[2]).setColor(0xff000000|entry.getValue().color);start[axis]++;buffer.addVertex(poses.last().pose(),start[0],start[1],start[2]).setColor(0xff000000|entry.getValue().color);}BufferUploader.drawWithShader(buffer.buildOrThrow());poses.popPose();RenderSystem.enableDepthTest();RenderSystem.depthMask(true);
        var buffers=minecraft.renderBuffers().bufferSource();for(var entry:MARKERS.entrySet()){if(entry.getValue().label==null)continue;var offset=Vec3.atCenterOf(entry.getKey()).subtract(eye);double length=offset.length();if(length>100||length==0)continue;var normal=offset.normalize();var scaled=offset.scale(Math.min(length,16)/length);String label=entry.getValue().label.getString();var look=minecraft.player.getLookAngle();if(Math.abs(look.x-normal.x)+Math.abs(look.y-normal.y)+Math.abs(look.z-normal.z)<.15)label+=" "+(int)length+"m";poses.pushPose();poses.translate(scaled.x,scaled.y+1,scaled.z);poses.mulPose(camera.rotation());poses.scale(-.026666668F,-.026666668F,.026666668F);int x=-minecraft.font.width(label)/2;minecraft.font.drawInBatch(label,x,0,0xff000000|entry.getValue().color,false,poses.last().pose(),buffers,Font.DisplayMode.SEE_THROUGH,0x40000000,LightTexture.FULL_BRIGHT);minecraft.font.drawInBatch(label,x,0,0xff000000|entry.getValue().color,false,poses.last().pose(),buffers,Font.DisplayMode.NORMAL,0,LightTexture.FULL_BRIGHT);poses.popPose();}buffers.endBatch();RenderSystem.disableBlend();
    }
    private PwrMarkers(){}
}
