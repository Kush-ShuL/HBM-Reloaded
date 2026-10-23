package com.hbm.client.oilliquefactor;
import com.hbm.machine.oilliquefactor.*;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.phys.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
/** Original3x4x3 outline and all6actual port-face highlights,including top and bottom. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class LiquefactorPlacementPreview {
 @SubscribeEvent public static void render(RenderHighlightEvent.Block e){var mc=Minecraft.getInstance();var player=mc.player;if(player==null||mc.level==null||mc.options.hideGui||!(player.getMainHandItem().getItem() instanceof BlockItem item)||!(item.getBlock() instanceof LiquefactorBlock block))return;e.setCanceled(true);var hit=player.pick(5,e.getDeltaTracker().getGameTimeDeltaPartialTick(false),false);if(!(hit instanceof BlockHitResult blockHit)||hit.getType()!=HitResult.Type.BLOCK)return;var context=new BlockPlaceContext(player,InteractionHand.MAIN_HAND,player.getMainHandItem(),blockHit);var core=context.getClickedPos().relative(context.getHorizontalDirection().getOpposite(),-block.offset());boolean valid=block.getStateForPlacement(context)!=null;float shade=(float)(Math.sin((System.currentTimeMillis()%(1000D*Math.PI))/250D)*.25+.75);var pose=e.getPoseStack();var cam=e.getCamera().getPosition();pose.pushPose();pose.translate(-cam.x,-cam.y,-cam.z);var buffers=mc.renderBuffers().bufferSource();var lines=buffers.getBuffer(LiquefactorPreviewTypes.LINES);LevelRenderer.renderLineBox(pose,lines,new AABB(core.getX()-1,core.getY(),core.getZ()-1,core.getX()+2,core.getY()+4,core.getZ()+2),valid?0:shade,valid?shade:0,0,1);
  for(var c:LiquefactorEntity.connections(core)){var p=c.origin();var normal=c.direction();int axis=normal.getAxis().ordinal(),one=(axis+1)%3,two=(axis+2)%3;double[] base={p.getX()+.25,p.getY()+.25,p.getZ()+.25};base[axis]=(axis==0?p.getX():axis==1?p.getY():p.getZ())+(normal.getAxisDirection()==Direction.AxisDirection.POSITIVE?1:0);for(int i=0;i<4;i++){double[] a=base.clone(),b=base.clone();a[one]+=(i==1||i==2)?.5:0;a[two]+=(i>=2)?.5:0;b[one]+=(i==0||i==1)?.5:0;b[two]+=(i==1||i==2)?.5:0;float x=(float)(b[0]-a[0]),y=(float)(b[1]-a[1]),z=(float)(b[2]-a[2]);for(double[] v:new double[][]{a,b})lines.addVertex(pose.last(),(float)v[0],(float)v[1],(float)v[2]).setColor(255,255,0,255).setNormal(pose.last(),x*2,y*2,z*2);}}
  buffers.endBatch(LiquefactorPreviewTypes.LINES);pose.popPose();}
}
