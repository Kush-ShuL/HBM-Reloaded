package com.hbm.client.woodprocessing;
import com.hbm.machine.woodprocessing.WoodProcessingContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.core.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.phys.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
/** Source single-box placement outline, original flat orange heat marker and four .6m inventory faces. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class SawmillPlacementPreview {
    @SubscribeEvent public static void render(RenderLevelStageEvent event){
        if(event.getStage()!=RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES)return;var mc=Minecraft.getInstance();if(mc.player==null||mc.level==null||!(mc.hitResult instanceof BlockHitResult hit))return;var held=mc.player.getMainHandItem();if(!held.is(WoodProcessingContent.SAWMILL_ITEM.get())||mc.player.getEyePosition().distanceToSqr(hit.getLocation())>25)return;
        var context=new BlockPlaceContext(mc.player,InteractionHand.MAIN_HAND,held,hit);var facing=context.getHorizontalDirection().getOpposite();var core=context.getClickedPos().relative(facing,-1);boolean canPlace=WoodProcessingContent.SAWMILL.get().getStateForPlacement(context)!=null;
        float pulse=(float)(Math.sin((System.currentTimeMillis()%(1000D*Math.PI))/250D)*.25+.75);var pose=event.getPoseStack();var eye=event.getCamera().getPosition();var buffers=mc.renderBuffers().bufferSource();var lines=buffers.getBuffer(RenderType.lines());pose.pushPose();pose.translate(-eye.x,-eye.y,-eye.z);
        LevelRenderer.renderLineBox(pose,lines,core.getX()-1,core.getY(),core.getZ()-1,core.getX()+2,core.getY()+2,core.getZ()+2,canPlace?0:pulse,canPlace?pulse:0,0,1);
        var lateral=facing.getClockWise();double cx=core.getX()+.5,cz=core.getZ()+.5,x0=cx+.5*facing.getStepX()+.4*lateral.getStepX(),x1=cx-.3*facing.getStepX()-.4*lateral.getStepX(),z0=cz+.5*facing.getStepZ()+.4*lateral.getStepZ(),z1=cz-.3*facing.getStepZ()-.4*lateral.getStepZ();
        LevelRenderer.renderLineBox(pose,lines,Math.min(x0,x1),core.getY(),Math.min(z0,z1),Math.max(x0,x1),core.getY(),Math.max(z0,z1),1,165F/255F,0,1);
        for(var side:Direction.Plane.HORIZONTAL){var port=core.relative(side);double px=port.getX(),py=port.getY(),pz=port.getZ();if(side.getAxis()==Direction.Axis.X){double x=px+(side.getStepX()>0?1:0);LevelRenderer.renderLineBox(pose,lines,x,py+.2,pz+.2,x,py+.8,pz+.8,pulse,pulse,0,1);}else{double z=pz+(side.getStepZ()>0?1:0);LevelRenderer.renderLineBox(pose,lines,px+.2,py+.2,z,px+.8,py+.8,z,pulse,pulse,0,1);}}
        pose.popPose();
    }
}
