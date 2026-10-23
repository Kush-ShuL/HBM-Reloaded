package com.hbm.client.reactor.dfc.components;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.*;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
/** Native fixed buffers: actual opaque OBJ faces are submitted before source-depth-tested beams. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class DfcComponentBeamPass {
 @SubscribeEvent(priority=EventPriority.LOWEST)public static void render(RenderLevelStageEvent event){if(event.getStage()!=RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES)return;var buffers=Minecraft.getInstance().renderBuffers().bufferSource();buffers.endLastBatch();buffers.endBatch(DfcComponentBeams.SOLID);buffers.endBatch(DfcComponentBeams.LINES);}
 private DfcComponentBeamPass(){}
}
