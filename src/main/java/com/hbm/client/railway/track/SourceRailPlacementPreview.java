package com.hbm.client.railway.track;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.railway.track.*;

import com.mojang.blaze3d.vertex.*;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.phys.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
/** Original BlockDummyable single-volume outer-edge placement highlight for the eleven rail registrations, using the sole true placement validation. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class SourceRailPlacementPreview {
    @SubscribeEvent public static void render(RenderHighlightEvent.Block event){var minecraft=Minecraft.getInstance();var player=minecraft.player;if(player==null||minecraft.level==null||minecraft.options.hideGui||!(player.getMainHandItem().getItem() instanceof net.minecraft.world.item.BlockItem held&&held.getBlock() instanceof SourceRailBlock))return;event.setCanceled(true);float partial=event.getDeltaTracker().getGameTimeDeltaPartialTick(false);var hit=player.pick(5D,partial,false);if(!(hit instanceof BlockHitResult blockHit)||hit.getType()!=HitResult.Type.BLOCK)return;var context=new BlockPlaceContext(player,InteractionHand.MAIN_HAND,player.getMainHandItem(),blockHit);var block=(SourceRailBlock)((net.minecraft.world.item.BlockItem)player.getMainHandItem().getItem()).getBlock();var facing=context.getHorizontalDirection().getOpposite();var core=context.getClickedPos().above(block.heightOffset()).relative(facing,-block.offset());boolean valid=block.getStateForPlacement(context)!=null;int shade=(int)(255D*(Math.sin((System.currentTimeMillis()%(1000D*Math.PI))/250D)*.25D+.75D));int red=valid?0:shade,green=valid?shade:0;var pose=event.getPoseStack();var camera=event.getCamera().getPosition();pose.pushPose();pose.translate(-camera.x,-camera.y,-camera.z);var buffers=minecraft.renderBuffers().bufferSource();var lines=buffers.getBuffer(SourceRailPreviewTypes.LINES);var dimensions=DummyableBlock.rotate(block.dimensions(),facing);var cells=new LinkedHashSet<BlockPos>();for(int x=-dimensions[4];x<=dimensions[5];x++)for(int y=-dimensions[1];y<=dimensions[0];y++)for(int z=-dimensions[2];z<=dimensions[3];z++)cells.add(core.offset(x,y,z));
        // Original cell-neighbor tests submit each exposed boundary edge from its two adjacent outer faces.
        for(var cell:cells)for(var face:Direction.values())if(!cells.contains(cell.relative(face)))for(var side:Direction.values())if(side.getAxis()!=face.getAxis()&&!cells.contains(cell.relative(side))){double[] a={cell.getX(),cell.getY(),cell.getZ()},b=a.clone();int axis=axis(face),other=axis(side),free=3-axis-other;a[axis]+=face.getAxisDirection()==Direction.AxisDirection.POSITIVE?1:0;b[axis]=a[axis];a[other]+=side.getAxisDirection()==Direction.AxisDirection.POSITIVE?1:0;b[other]=a[other];b[free]++;line(lines,pose,a[0],a[1],a[2],b[0],b[1],b[2],red,green,0);}
        buffers.endBatch(SourceRailPreviewTypes.LINES);pose.popPose();
    }
    private static int axis(Direction direction){return direction.getAxis()==Direction.Axis.X?0:direction.getAxis()==Direction.Axis.Y?1:2;}
    private static void line(VertexConsumer lines,PoseStack poses,double x0,double y0,double z0,double x1,double y1,double z1,int r,int g,int b){float nx=(float)(x1-x0),ny=(float)(y1-y0),nz=(float)(z1-z0),length=(float)Math.sqrt(nx*nx+ny*ny+nz*nz);if(length==0)return;nx/=length;ny/=length;nz/=length;var pose=poses.last();lines.addVertex(pose,(float)x0,(float)y0,(float)z0).setColor(r,g,b,255).setNormal(pose,nx,ny,nz);lines.addVertex(pose,(float)x1,(float)y1,(float)z1).setColor(r,g,b,255).setNormal(pose,nx,ny,nz);}
    private SourceRailPlacementPreview(){}
}
