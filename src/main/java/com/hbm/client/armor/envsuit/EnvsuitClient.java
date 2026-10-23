package com.hbm.client.armor.envsuit;

import com.hbm.armor.envsuit.EnvsuitContent;
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
public final class EnvsuitClient {
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            private EnvsuitItemRenderer renderer;
            @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) renderer = new EnvsuitItemRenderer();
                return renderer;
            }
        }, EnvsuitContent.HELMET.get(), EnvsuitContent.PLATE.get(), EnvsuitContent.LEGS.get(), EnvsuitContent.BOOTS.get());
    }
    @SubscribeEvent public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            for (var item : java.util.List.of(EnvsuitContent.HELMET, EnvsuitContent.PLATE, EnvsuitContent.LEGS, EnvsuitContent.BOOTS))
                T51ArmorRenderer.register(item.get(), EnvsuitArmorRenderer::render);
        });
    }
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event) {
        for (String part : new String[] {"helmet", "plate", "legs", "boots"}) {
            var key = ModelResourceLocation.inventory(EnvsuitContent.id("envsuit_" + part));
            var model = event.getModels().get(key);
            if (model != null) event.getModels().put(key, new BakedModelWrapper<>(model) {
                @Override public ItemTransforms getTransforms() { return ItemTransforms.NO_TRANSFORMS; }
            });
        }
    }
    private EnvsuitClient() { }
}
