package com.hbm.client.armor.hev;

import com.hbm.armor.hev.HevArmorContent;
import com.hbm.client.armor.t51.T51ArmorRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.model.BakedModelWrapper;

@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class HevClient {
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            private HevItemRenderer renderer;
            @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) renderer = new HevItemRenderer();
                return renderer;
            }
        }, HevArmorContent.HELMET.get(), HevArmorContent.PLATE.get(), HevArmorContent.LEGS.get(), HevArmorContent.BOOTS.get());
    }
    @SubscribeEvent public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            for (var item : java.util.List.of(HevArmorContent.HELMET, HevArmorContent.PLATE, HevArmorContent.LEGS, HevArmorContent.BOOTS))
                T51ArmorRenderer.register(item.get(), HevArmorRenderer::render);
        });
    }
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event) {
        for (String part : new String[] {"helmet", "plate", "legs", "boots"}) {
            var key = ModelResourceLocation.inventory(HevArmorContent.id("hev_" + part));
            var model = event.getModels().get(key);
            if (model != null) event.getModels().put(key, new BakedModelWrapper<>(model) {
                @Override public ItemTransforms getTransforms() { return ItemTransforms.NO_TRANSFORMS; }
            });
        }
    }
    private HevClient() { }
}
