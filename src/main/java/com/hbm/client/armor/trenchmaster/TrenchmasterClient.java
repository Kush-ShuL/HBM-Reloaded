package com.hbm.client.armor.trenchmaster;

import com.hbm.armor.trenchmaster.TrenchmasterContent;
import com.hbm.client.armor.t51.T51ArmorRenderer;
import com.hbm.weapon.sedna.gun.GunArmorPerks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.extensions.common.*;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class TrenchmasterClient {
    private static final net.minecraft.resources.ResourceLocation STEP = TrenchmasterContent.id("trenchmaster_step");

    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            private final TrenchmasterItemRenderer renderer = new TrenchmasterItemRenderer();
            @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() { return renderer; }
        }, TrenchmasterContent.HELMET.get(), TrenchmasterContent.PLATE.get(), TrenchmasterContent.LEGS.get(), TrenchmasterContent.BOOTS.get());
    }
    @SubscribeEvent public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            for (var item : java.util.List.of(TrenchmasterContent.HELMET, TrenchmasterContent.PLATE, TrenchmasterContent.LEGS, TrenchmasterContent.BOOTS))
                T51ArmorRenderer.register(item.get(), TrenchmasterArmorRenderer::render);
            NeoForge.EVENT_BUS.addListener(TrenchmasterClient::step);
        });
    }
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event) {
        for (String name : new String[]{"trenchmaster_helmet", "trenchmaster_plate", "trenchmaster_legs", "trenchmaster_boots"}) {
            var key = ModelResourceLocation.inventory(TrenchmasterContent.id(name));
            var model = event.getModels().get(key);
            if (model != null) event.getModels().put(key, new BakedModelWrapper<>(model) {
                @Override public ItemTransforms getTransforms() { return ItemTransforms.NO_TRANSFORMS; }
            });
        }
    }
    private static void step(PlayerTickEvent.Pre event) {
        var player = event.getEntity();
        if (player != Minecraft.getInstance().player) return;
        var attribute = player.getAttribute(Attributes.STEP_HEIGHT);
        if (attribute == null) return;
        boolean active = GunArmorPerks.isTrenchMaster(player);
        var previous = attribute.getModifier(STEP);
        if (active && previous != null && attribute.getValue() == (double) 1.003F) return;
        if (previous != null) attribute.removeModifier(STEP);
        if (active) {
            // Source client START phase writes absolute stepSize+0.003F. Preserve unrelated native modifiers.
            double value = attribute.getValue();
            boolean zero = value == 0;
            attribute.addTransientModifier(new AttributeModifier(STEP, zero ? (double) 1.003F : (double) 1.003F / value - 1,
                    zero ? AttributeModifier.Operation.ADD_VALUE : AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }
    }
    private TrenchmasterClient() {}
}
