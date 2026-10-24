package com.hbm.client.network.cargo;
import com.hbm.network.cargo.*;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.event.ClientTickEvent;
/** Source60-tick, untextured opaque debug lines, depth disabled; only generated while holding actual drone tools. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class CargoDroneEffects {
 private static final class Line{final double x,y,z,dx,dy,dz;final int color;int age;Line(double x,double y,double z,double dx,double dy,double dz,int color){this.x=x;this.y=y;this.z=z;this.dx=dx;this.dy=dy;this.dz=dz;this.color=color;}}
 private static final List<Line> LINES=new ArrayList<>();private static ClientLevel world;
 public static java.util.function.Predicate<net.minecraft.world.item.Item> REQUEST_TOOLS=item->false;
 public static void add(double x,double y,double z,double dx,double dy,double dz,int color){var mc=Minecraft.getInstance();if(mc.player==null)return;var held=mc.player.getMainHandItem().getItem();if(held instanceof DroneItem||held==CargoDroneContent.LINKER.get()||held==CargoDroneContent.WAYPOINT_ITEM.get()||REQUEST_TOOLS.test(held))LINES.add(new Line(x,y,z,dx,dy,dz,color));}
 @SubscribeEvent public static void tick(ClientTickEvent.Post event){var current=Minecraft.getInstance().level;if(current!=world){LINES.clear();world=current;}if(current==null||Minecraft.getInstance().isPaused())return;LINES.removeIf(line->line.age++>=60);}
 @SubscribeEvent public static void render(RenderLevelStageEvent event){if(event.getStage()!=RenderLevelStageEvent.Stage.AFTER_PARTICLES||LINES.isEmpty())return;var camera=event.getCamera().getPosition();RenderSystem.setShader(GameRenderer::getPositionColorShader);RenderSystem.enableBlend();RenderSystem.defaultBlendFunc();RenderSystem.disableDepthTest();RenderSystem.lineWidth(1);var vertices=Tesselator.getInstance().begin(VertexFormat.Mode.DEBUG_LINES,DefaultVertexFormat.POSITION_COLOR);var pose=event.getPoseStack().last().pose();for(var line:LINES){int color=0xff000000|line.color;float x=(float)(line.x-camera.x),y=(float)(line.y-camera.y),z=(float)(line.z-camera.z);vertices.addVertex(pose,x,y,z).setColor(color);vertices.addVertex(pose,(float)(x+line.dx),(float)(y+line.dy),(float)(z+line.dz)).setColor(color);}BufferUploader.drawWithShader(vertices.buildOrThrow());RenderSystem.enableDepthTest();RenderSystem.disableBlend();}
 @SubscribeEvent public static void hud(RenderGuiEvent.Post event){var mc=Minecraft.getInstance();if(mc.options.hideGui||mc.player==null)return;var stack=mc.player.getMainHandItem();if(!stack.is(CargoDroneContent.LINKER.get())||!stack.has(DataComponents.CUSTOM_DATA))return;var tag=stack.get(DataComponents.CUSTOM_DATA).copyTag();var text=Component.translatable("hud.hbm.drone.previous",tag.getInt("x"),tag.getInt("y"),tag.getInt("z"));var graphics=event.getGuiGraphics();var font=com.hbm.client.gui.font.LegacyFonts.get();graphics.drawString(font,text,(graphics.guiWidth()-font.width(text))/2,graphics.guiHeight()/2+20,0xffffff,true);}
}
