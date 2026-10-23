package com.hbm.client.armor.blackjack;

import com.hbm.armor.blackjack.BlackjackContent;
import com.hbm.client.armor.t51.T51ArmorRenderer;
import com.hbm.client.armor.t51.T51ClientEvents;
import com.hbm.client.weapon.rifle50.Rifle50ThermalSight;
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

@EventBusSubscriber(modid="hbm", value=Dist.CLIENT, bus=EventBusSubscriber.Bus.MOD)
public final class BlackjackClient {
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            private final BlackjackItemRenderer renderer = new BlackjackItemRenderer();
            @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() { return renderer; }
        }, BlackjackContent.armor().stream().map(holder -> holder.get()).toArray(net.minecraft.world.item.Item[]::new));
    }
    @SubscribeEvent public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            BlackjackContent.armor().forEach(holder -> T51ArmorRenderer.register(holder.get(), BlackjackArmorRenderer::render));
            Rifle50ThermalSight.installHudGate(T51ClientEvents::sourceHudEnabled);
        });
    }
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event) {
        for (String name : new String[]{"bj_helmet", "bj_plate", "bj_plate_jetpack", "bj_legs", "bj_boots"}) {
            var key = ModelResourceLocation.inventory(BlackjackContent.id(name));
            var model = event.getModels().get(key);
            if (model != null) event.getModels().put(key, new BakedModelWrapper<>(model) {
                @Override public ItemTransforms getTransforms() { return ItemTransforms.NO_TRANSFORMS; }
            });
        }
    }
    private BlackjackClient() { }
}
