package com.hbm.client.oilreformer;
import com.hbm.machine.oilreformer.*;
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
/** Original3-cuboid rotated main+2elevatedXR lobes and6outward HE/fluid faces. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class ReformerPlacementPreview {
    @SubscribeEvent public static void render(RenderHighlightEvent.Block event){var client=Minecraft.getInstance();var player=client.player;if(player==null||client.level==null||client.options.hideGui||!(player.getMainHandItem().getItem() instanceof BlockItem item)||!(item.getBlock() instanceof ReformerBlock block))return;event.setCanceled(true);var hit=player.pick(5,event.getDeltaTracker().getGameTimeDeltaPartialTick(false),false);if(!(hit instanceof BlockHitResult blockHit)||hit.getType()!=HitResult.Type.BLOCK)return;var context=new BlockPlaceContext(player,InteractionHand.MAIN_HAND,player.getMainHandItem(),blockHit);var facing=context.getHorizontalDirection().getOpposite();var core=context.getClickedPos().relative(facing,-block.offset()).above(block.heightOffset());boolean valid=block.getStateForPlacement(context)!=null;int shade=(int)(255*(Math.sin((System.currentTimeMillis()%(1000D*Math.PI))/250D)*.25+.75)),red=valid?0:shade,green=valid?shade:0;var pose=event.getPoseStack();var camera=event.getCamera().getPosition();pose.pushPose();pose.translate(-camera.x,-camera.y,-camera.z);var buffers=client.renderBuffers().bufferSource();var lines=buffers.getBuffer(ReformerPreviewTypes.LINES);var cells=ReformerBlock.cells(core,facing);for(var cell:cells)for(var face:Direction.values())if(!cells.contains(cell.relative(face)))for(var side:Direction.values())if(side.getAxis()!=face.getAxis()&&!cells.contains(cell.relative(side))){double[] a={cell.getX(),cell.getY(),cell.getZ()},b=a.clone();int axis=axis(face),other=axis(side),free=3-axis-other;a[axis]+=face.getAxisDirection()==Direction.AxisDirection.POSITIVE?1:0;b[axis]=a[axis];a[other]+=side.getAxisDirection()==Direction.AxisDirection.POSITIVE?1:0;b[other]=a[other];b[free]++;line(lines,pose,a,b,red,green,0);}ports(lines,pose,core,facing);buffers.endBatch(ReformerPreviewTypes.LINES);pose.popPose();}
    private static void ports(VertexConsumer out,PoseStack pose,BlockPos core,Direction facing){for(var p:ReformerEntity.portPositions(core,facing))for(var face:ReformerEntity.portSides(core,facing,p)){int a=axis(face),b=a==0?2:0;double[] low={p.getX()+.25,p.getY()+.25,p.getZ()+.25};low[a]=a==0?p.getX():p.getZ();if(face.getAxisDirection()==Direction.AxisDirection.POSITIVE)low[a]+=1;for(int e=0;e<4;e++){double[] start=low.clone(),end=low.clone();start[b]+=(e==1||e==2)?.5:0;start[1]+=(e>=2)?.5:0;end[b]+=(e==0||e==1)?.5:0;end[1]+=(e==1||e==2)?.5:0;line(out,pose,start,end,255,255,0);}}}
    private static int axis(Direction direction){return direction.getAxis()==Direction.Axis.X?0:direction.getAxis()==Direction.Axis.Y?1:2;}
    private static void line(VertexConsumer out,PoseStack pose,double[] a,double[] b,int r,int g,int blue){float x=(float)(b[0]-a[0]),y=(float)(b[1]-a[1]),z=(float)(b[2]-a[2]),length=(float)Math.sqrt(x*x+y*y+z*z);if(length==0)return;x/=length;y/=length;z/=length;out.addVertex(pose.last(),(float)a[0],(float)a[1],(float)a[2]).setColor(r,g,blue,255).setNormal(pose.last(),x,y,z);out.addVertex(pose.last(),(float)b[0],(float)b[1],(float)b[2]).setColor(r,g,blue,255).setNormal(pose.last(),x,y,z);}
    private ReformerPlacementPreview(){}
}
