package com.hbm.client.laminatepane;

import com.hbm.block.laminatepane.LaminatePaneContent;
import com.hbm.client.structure.industrial44.ReinforcedPaneModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

/** Original transparent renderpass1, retaining the sole original RenderBlocks-pane sheets/rims/UV algorithm. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class LaminatePaneClient {
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){
        event.getModels().replaceAll((key,model)->key.id().equals(LaminatePaneContent.PANE.getId())&&!key.getVariant().equals("inventory")
            ?new ReinforcedPaneModel(model,event.getTextureGetter(),ResourceLocation.parse("hbm:block/reinforced_laminate_pane"),ResourceLocation.parse("hbm:block/reinforced_laminate_pane_edge"),RenderType.translucent()):model);
    }
    private LaminatePaneClient(){}
}
