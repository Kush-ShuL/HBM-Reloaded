package com.hbm.client.rockmill;
import com.hbm.machine.rockmill.*;
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
/** Original five-by-five body outline and all eight outward proxy faces at the final offset2 core. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class RockMillPlacementPreview {
    @SubscribeEvent public static void render(RenderHighlightEvent.Block event){
        var client=Minecraft.getInstance();var player=client.player;if(player==null||client.level==null||client.options.hideGui||!(player.getMainHandItem().getItem() instanceof BlockItem item)||!(item.getBlock() instanceof RockMillBlock block))return;
        event.setCanceled(true);var hit=player.pick(5,event.getDeltaTracker().getGameTimeDeltaPartialTick(false),false);if(!(hit instanceof BlockHitResult blockHit)||hit.getType()!=HitResult.Type.BLOCK)return;
        var context=new BlockPlaceContext(player,InteractionHand.MAIN_HAND,player.getMainHandItem(),blockHit);var facing=context.getHorizontalDirection().getOpposite();var core=context.getClickedPos().relative(facing,-block.offset()).above(block.heightOffset());boolean valid=block.getStateForPlacement(context)!=null;
        float shade=(float)(Math.sin((System.currentTimeMillis()%(1000D*Math.PI))/250D)*.25+.75);var pose=event.getPoseStack();var camera=event.getCamera().getPosition();pose.pushPose();pose.translate(-camera.x,-camera.y,-camera.z);var buffers=client.renderBuffers().bufferSource();var lines=buffers.getBuffer(RockMillPreviewTypes.LINES);
        LevelRenderer.renderLineBox(pose,lines,new AABB(core.getX()-2,core.getY(),core.getZ()-2,core.getX()+3,core.getY()+3,core.getZ()+3),valid?0:shade,valid?shade:0,0,1);
        for(var port:RockMillEntity.portDefinitions(core))for(var face:java.util.List.of(port.side())){
            int axis=face.getAxis()==Direction.Axis.X?0:2,other=axis==0?2:0;var pos=port.position();double[] low={pos.getX()+.25,pos.getY()+.25,pos.getZ()+.25};low[axis]=axis==0?pos.getX():pos.getZ();if(face.getAxisDirection()==Direction.AxisDirection.POSITIVE)low[axis]+=1;
            for(int edge=0;edge<4;edge++){double[] a=low.clone(),b=low.clone();a[other]+=(edge==1||edge==2)?.5:0;a[1]+=(edge>=2)?.5:0;b[other]+=(edge==0||edge==1)?.5:0;b[1]+=(edge==1||edge==2)?.5:0;line(lines,pose,a,b);}
        }
        buffers.endBatch(RockMillPreviewTypes.LINES);pose.popPose();
    }
    private static void line(VertexConsumer out,PoseStack pose,double[] a,double[] b){float x=(float)(b[0]-a[0]),y=(float)(b[1]-a[1]),z=(float)(b[2]-a[2]),length=(float)Math.sqrt(x*x+y*y+z*z);x/=length;y/=length;z/=length;out.addVertex(pose.last(),(float)a[0],(float)a[1],(float)a[2]).setColor(255,255,0,255).setNormal(pose.last(),x,y,z);out.addVertex(pose.last(),(float)b[0],(float)b[1],(float)b[2]).setColor(255,255,0,255).setNormal(pose.last(),x,y,z);}
}
