package com.hbm.client.structure.logic;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

/** The authoring arrows and invisible host keep their own source models; only the visible runtime swaps face icons. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class WandLogicClient {
    @SubscribeEvent public static void bake(ModelEvent.ModifyBakingResult event) {
        var id = ResourceLocation.fromNamespaceAndPath("hbm","logic_block");
        event.getModels().replaceAll((location,model) -> location.id().equals(id) && !location.getVariant().equals("inventory")
                ? new LogicDisguiseModel(model) : model);
    }
    private WandLogicClient() {}
}
