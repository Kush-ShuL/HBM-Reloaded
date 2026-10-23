package com.hbm.client.reactor.dfc;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.*;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
/** Source flare alone does not write depth: submit it against completed real OBJ faces. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class DfcCorePass {
    private static final List<Runnable> FLARES=new ArrayList<>();
    static void submit(MultiBufferSource buffers,Runnable draw){if(buffers==Minecraft.getInstance().renderBuffers().bufferSource())FLARES.add(draw);else draw.run();}
    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void render(RenderLevelStageEvent event){if(event.getStage()!=RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES||FLARES.isEmpty())return;var draws=List.copyOf(FLARES);FLARES.clear();var buffers=Minecraft.getInstance().renderBuffers().bufferSource();buffers.endLastBatch();for(var draw:draws)draw.run();buffers.endBatch(DfcCoreRenderTypes.FLARE);}
    private DfcCorePass(){}
}
