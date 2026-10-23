package com.hbm.client.structure.woodfixtures46;

import com.hbm.block.woodfixtures46.WoodFixturesContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class WoodFixturesClient {
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event) {
        event.getModels().replaceAll((id, model) -> id.id().equals(WoodFixturesContent.BARRIER.getId())
                ? new WoodFixtureModel(model, true, id.getVariant().equals("inventory"), event.getTextureGetter())
                : id.id().equals(WoodFixturesContent.STRUCTURE.getId())
                ? new WoodFixtureModel(model, false, id.getVariant().equals("inventory"), event.getTextureGetter()) : model);
    }
    private WoodFixturesClient() {}
}
