package com.hbm.client.rebar;
import com.hbm.block.rebar.*;
import com.hbm.item.settings.client.LegacyInfoSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class RebarPreview {
 @SubscribeEvent public static void render(RenderLevelStageEvent event){if(event.getStage()==RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS){RebarWetRenderer.drawn=0;return;}if(event.getStage()!=RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES)return;var mc=Minecraft.getInstance();if(mc.level==null||mc.player==null||!(mc.hitResult instanceof BlockHitResult hit))return;var stack=mc.player.getMainHandItem();if(!stack.is(RebarContent.PLACER.get()))return;int[] first=RebarPlacerItem.data(stack).getIntArray("pos");if(first.length!=3)return;var second=hit.getBlockPos().relative(hit.getDirection());double minX=Math.min(first[0],second.getX())+.125,maxX=Math.max(first[0],second.getX())+.875,minY=Math.min(first[1],second.getY())+.125,maxY=Math.max(first[1],second.getY())+.875,minZ=Math.min(first[2],second.getZ())+.125,maxZ=Math.max(first[2],second.getZ())+.875;var pose=event.getPoseStack();var eye=event.getCamera().getPosition();pose.pushPose();pose.translate(-eye.x,-eye.y,-eye.z);LevelRenderer.renderLineBox(pose,mc.renderBuffers().bufferSource().getBuffer(RenderType.lines()),minX,minY,minZ,maxX,maxY,maxZ,1,1,1,1);pose.popPose();long required=((long)Math.abs(first[0]-second.getX())+1)*(Math.abs(first[1]-second.getY())+1)*(Math.abs(first[2]-second.getZ())+1);int available=RebarPlacerItem.available(mc.player);LegacyInfoSystem.push(Component.literal(available+" / "+required).withStyle(required>available?ChatFormatting.RED:ChatFormatting.GREEN),1000,3);}
}
