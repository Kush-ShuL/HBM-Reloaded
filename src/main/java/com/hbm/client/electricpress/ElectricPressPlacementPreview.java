package com.hbm.client.electricpress;
import com.hbm.machine.electricpress.*;
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
/** Source1x3x1 outline and five advertised core faces for both machines; node-side around6 remains a distinct original detail. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class ElectricPressPlacementPreview {
 @SubscribeEvent public static void render(RenderHighlightEvent.Block e){var mc=Minecraft.getInstance();var player=mc.player;if(player==null||mc.level==null||mc.options.hideGui||!(player.getMainHandItem().getItem() instanceof BlockItem item)||!(item.getBlock() instanceof com.hbm.multiblock.DummyableBlock block)||!(block instanceof ElectricPressBlock||block instanceof ConveyorPressBlock))return;e.setCanceled(true);var hit=player.pick(5,e.getDeltaTracker().getGameTimeDeltaPartialTick(false),false);if(!(hit instanceof BlockHitResult blockHit)||hit.getType()!=HitResult.Type.BLOCK)return;var context=new BlockPlaceContext(player,InteractionHand.MAIN_HAND,player.getMainHandItem(),blockHit);var core=context.getClickedPos().relative(context.getHorizontalDirection().getOpposite(),-block.offset());boolean valid=block.getStateForPlacement(context)!=null;float shade=(float)(Math.sin((System.currentTimeMillis()%(1000D*Math.PI))/250D)*.25+.75);var pose=e.getPoseStack();var cam=e.getCamera().getPosition();pose.pushPose();pose.translate(-cam.x,-cam.y,-cam.z);var buffers=mc.renderBuffers().bufferSource();var lines=buffers.getBuffer(ElectricPressPreviewTypes.LINES);LevelRenderer.renderLineBox(pose,lines,new AABB(core.getX(),core.getY(),core.getZ(),core.getX()+1,core.getY()+3,core.getZ()+1),valid?0:shade,valid?shade:0,0,1);
  for(var c:java.util.Arrays.stream(Direction.values()).filter(side->side!=Direction.UP).map(side->new com.hbm.energy.NodeConnection(core.relative(side),side)).toList()){var p=c.origin();var normal=c.direction();int axis=normal.getAxis().ordinal(),one=(axis+1)%3,two=(axis+2)%3;double[] base={p.getX()+.25,p.getY()+.25,p.getZ()+.25};base[axis]=(axis==0?p.getX():axis==1?p.getY():p.getZ())+(normal.getAxisDirection()==Direction.AxisDirection.POSITIVE?1:0);for(int i=0;i<4;i++){double[] a=base.clone(),b=base.clone();a[one]+=(i==1||i==2)?.5:0;a[two]+=(i>=2)?.5:0;b[one]+=(i==0||i==1)?.5:0;b[two]+=(i==1||i==2)?.5:0;float x=(float)(b[0]-a[0]),y=(float)(b[1]-a[1]),z=(float)(b[2]-a[2]);for(double[] v:new double[][]{a,b})lines.addVertex(pose.last(),(float)v[0],(float)v[1],(float)v[2]).setColor(255,255,0,255).setNormal(pose.last(),x*2,y*2,z*2);}}
  buffers.endBatch(ElectricPressPreviewTypes.LINES);pose.popPose();}
}
