package com.hbm.client.turret.greg;
import com.hbm.machine.turret.greg.*;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.*;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.phys.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
/** Source4x4x2 Dummyable boundary preview with offset1. Actual TurretArty has no getAllPorts preview definition,so no invented yellow port markers. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class GregTurretPlacementPreview {
    @SubscribeEvent public static void render(RenderHighlightEvent.Block event){var client=Minecraft.getInstance();var player=client.player;if(player==null||client.level==null||client.options.hideGui||!(player.getMainHandItem().getItem() instanceof BlockItem item)||!(item.getBlock() instanceof GregTurretBlock block))return;event.setCanceled(true);var hit=player.pick(5,event.getDeltaTracker().getGameTimeDeltaPartialTick(false),false);if(!(hit instanceof BlockHitResult blockHit)||hit.getType()!=HitResult.Type.BLOCK)return;var context=new BlockPlaceContext(player,InteractionHand.MAIN_HAND,player.getMainHandItem(),blockHit);var facing=context.getHorizontalDirection().getOpposite();var core=context.getClickedPos().relative(facing,-block.offset()).above(block.heightOffset());boolean valid=block.getStateForPlacement(context)!=null;int shade=(int)(255*(Math.sin((System.currentTimeMillis()%(1000D*Math.PI))/250D)*.25+.75)),red=valid?0:shade,green=valid?shade:0;var pose=event.getPoseStack();var camera=event.getCamera().getPosition();pose.pushPose();pose.translate(-camera.x,-camera.y,-camera.z);var buffers=client.renderBuffers().bufferSource();var lines=buffers.getBuffer(GregTurretPreviewTypes.LINES);var d=DummyableBlock.rotate(block.dimensions(),facing);var cells=new LinkedHashSet<BlockPos>();for(int x=-d[4];x<=d[5];x++)for(int y=-d[1];y<=d[0];y++)for(int z=-d[2];z<=d[3];z++)cells.add(core.offset(x,y,z));for(var cell:cells)for(var face:Direction.values())if(!cells.contains(cell.relative(face)))for(var side:Direction.values())if(side.getAxis()!=face.getAxis()&&!cells.contains(cell.relative(side))){double[] a={cell.getX(),cell.getY(),cell.getZ()},b=a.clone();int axis=axis(face),other=axis(side),free=3-axis-other;a[axis]+=face.getAxisDirection()==Direction.AxisDirection.POSITIVE?1:0;b[axis]=a[axis];a[other]+=side.getAxisDirection()==Direction.AxisDirection.POSITIVE?1:0;b[other]=a[other];b[free]++;line(lines,pose,a,b,red,green,0);}buffers.endBatch(GregTurretPreviewTypes.LINES);pose.popPose();}
    private static int axis(Direction direction){return direction.getAxis()==Direction.Axis.X?0:direction.getAxis()==Direction.Axis.Y?1:2;}
    private static void line(VertexConsumer out,PoseStack pose,double[] a,double[] b,int r,int g,int blue){float x=(float)(b[0]-a[0]),y=(float)(b[1]-a[1]),z=(float)(b[2]-a[2]),length=(float)Math.sqrt(x*x+y*y+z*z);if(length==0)return;x/=length;y/=length;z/=length;out.addVertex(pose.last(),(float)a[0],(float)a[1],(float)a[2]).setColor(r,g,blue,255).setNormal(pose.last(),x,y,z);out.addVertex(pose.last(),(float)b[0],(float)b[1],(float)b[2]).setColor(r,g,blue,255).setNormal(pose.last(),x,y,z);}
    private GregTurretPlacementPreview(){}
}
