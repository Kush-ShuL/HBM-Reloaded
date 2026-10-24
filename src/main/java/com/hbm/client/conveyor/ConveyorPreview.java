package com.hbm.client.conveyor;

import com.hbm.conveyor.belt.*;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.event.level.LevelEvent;

/** Original opaque cyan/red WorldInAJar preview, preserving failed-route partial cells and yaw cache. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class ConveyorPreview {
 private static final Map<BlockPos,BlockState> CELLS=new LinkedHashMap<>();private static BlockPos lastHit;private static Direction lastSide;private static float lastYaw;private static boolean success;
 public static void clear(){CELLS.clear();lastHit=null;}
 public static void tick(ItemStack stack,Player player){var mc=Minecraft.getInstance();if(!stack.has(DataComponents.CUSTOM_DATA)||!(mc.hitResult instanceof BlockHitResult hit)||hit.getType()!=HitResult.Type.BLOCK){clear();return;}var pos=hit.getBlockPos();var side=hit.getDirection();var level=player.level();if(level.getBlockState(pos).getBlock() instanceof ConveyorBeltBlock belt&&belt.bendable()){var input=belt.input(level,pos);if(input!=null&&level.getBlockState(pos.relative(input)).canBeReplaced())side=input;}if(pos.equals(lastHit)&&lastSide==side&&Math.abs(lastYaw-player.getYRot())<15)return;lastHit=pos.immutable();lastSide=side;lastYaw=player.getYRot();var tag=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();var start=ConveyorBeltBlock.direction(tag.getInt("side"));CELLS.clear();if(start==null)return;success=ConveyorRoute.construct(level,(p,s)->CELLS.put(p.immutable(),s),ConveyorWandItem.type(stack),player,tag.getInt("x"),tag.getInt("y"),tag.getInt("z"),start,pos.getX(),pos.getY(),pos.getZ(),side,tag.getInt("count"))>0;}
 @SubscribeEvent public static void unload(LevelEvent.Unload event){if(event.getLevel().isClientSide())clear();}
 @SubscribeEvent public static void preview(RenderLevelStageEvent event){if(event.getStage()!=RenderLevelStageEvent.Stage.AFTER_PARTICLES||CELLS.isEmpty())return;var mc=Minecraft.getInstance();if(mc.player==null||!(mc.player.getMainHandItem().getItem() instanceof ConveyorWandItem)){clear();return;}var eye=event.getCamera().getPosition();var pose=event.getPoseStack();pose.pushPose();pose.translate(-eye.x,-eye.y,-eye.z);RenderSystem.setShader(GameRenderer::getPositionTexColorShader);RenderSystem.setShaderTexture(0,TextureAtlas.LOCATION_BLOCKS);RenderSystem.disableBlend();RenderSystem.enableDepthTest();RenderSystem.depthMask(true);var buffer=Tesselator.getInstance().begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.POSITION_TEX_COLOR);var atlas=mc.getTextureAtlas(TextureAtlas.LOCATION_BLOCKS);for(var entry:CELLS.entrySet()){var p=entry.getKey();var state=entry.getValue();var belt=(ConveyorBeltBlock)state.getBlock();var quads=ConveyorGeometry.build(name->atlas.apply(name.contains(":")?net.minecraft.resources.ResourceLocation.parse(name).withPrefix("block/"):ConveyorContent.id("block/"+name)),belt.kind,state.getValue(ConveyorBeltBlock.META),ConveyorGeometry.Neighbors.preview(CELLS,p),false);for(var quad:quads){int[] data=quad.getVertices();for(int i=0;i<4;i++){int at=i*8;buffer.addVertex(pose.last().pose(),p.getX()+Float.intBitsToFloat(data[at]),p.getY()+Float.intBitsToFloat(data[at+1]),p.getZ()+Float.intBitsToFloat(data[at+2])).setUv(Float.intBitsToFloat(data[at+4]),Float.intBitsToFloat(data[at+5])).setColor(success?0:255,success?255:0,success?255:0,255);}}}var built=buffer.build();if(built!=null)BufferUploader.drawWithShader(built);pose.popPose();}
 @SubscribeEvent public static void overlay(RenderGuiEvent.Post event){if(!com.hbm.item.settings.client.LegacyInfoSystem.lookOverlayEnabled())return;var mc=Minecraft.getInstance();var player=mc.player;if(player==null||!player.getAbilities().instabuild||!player.isShiftKeyDown()||!(player.getMainHandItem().getItem() instanceof ConveyorWandItem)||!(mc.hitResult instanceof BlockHitResult hit)||hit.getType()!=HitResult.Type.BLOCK||!(player.level().getBlockState(hit.getBlockPos()).getBlock() instanceof ConveyorBeltBlock))return;int x=event.getGuiGraphics().guiWidth()/2+8,y=event.getGuiGraphics().guiHeight()/2;var title=player.level().getBlockState(hit.getBlockPos()).getBlock().getName();var graphics=event.getGuiGraphics();graphics.drawString(mc.font,title,x+1,y-9,0x404000,false);graphics.drawString(mc.font,title,x,y-10,0xFFFF00,false);graphics.drawString(mc.font,Component.translatable("gui.hbm.conveyor.break_line"),x,y,0xFFFFFF,true);}
 private ConveyorPreview(){}
}
