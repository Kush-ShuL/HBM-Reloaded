package com.hbm.client.missileassembly;

import com.hbm.client.missile.custom.*;
import com.hbm.machine.missileassembly.*;
import com.hbm.missile.custom.*;
import com.hbm.entity.missile.CustomMissileEntity;
import java.util.ArrayList;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
import net.neoforged.neoforge.client.model.BakedModelWrapper;

@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class MissileAssemblyClient {
    @SubscribeEvent public static void renderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(MissileAssemblyContent.ENTITY.get(), MissileAssemblyRenderer::new);
        event.registerEntityRenderer(CustomMissileFlightContent.CUSTOM.get(), CustomMissileEntityRenderer::new);
        CustomMissileEntity.CONTRAIL = effect -> {
            if (effect.level() instanceof net.minecraft.client.multiplayer.ClientLevel level) {
                float r = 0, g = 0, b = 0;
                switch (effect.fuel()) {
                    case SOLID -> { r = .3F; g = .2F; b = .05F; }
                    case HYDROGEN -> r = g = b = .7F;
                    case BALEFIRE -> { r = b = .2F; g = .7F; }
                    default -> { }
                }
                net.minecraft.client.Minecraft.getInstance().particleEngine.add(new com.hbm.client.bomb.nuclear.LegacyAbmContrailParticle(level, effect.x(), effect.y(), effect.z(), r, g, b, 1));
            }
        };
    }
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event) { event.register(MissileAssemblyContent.MENU.get(), MissileAssemblyScreen::new); }
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            private final MissileAssemblyItemRenderer renderer = new MissileAssemblyItemRenderer();
            @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() { return renderer; }
        }, MissileAssemblyContent.ITEM.get());
        var parts = new ArrayList<net.minecraft.world.item.Item>();
        for (var item : CustomMissileContent.PARTS.values()) if (CustomMissileModels.part(item.get()) != null) parts.add(item.get());
        parts.add(CustomMissileContent.MISSILE.get());
        event.registerItem(new IClientItemExtensions() {
            private final CustomMissileItemRenderer renderer = new CustomMissileItemRenderer();
            @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() { return renderer; }
        }, parts.toArray(net.minecraft.world.item.Item[]::new));
    }
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event) {
        var names = new ArrayList<net.minecraft.resources.ResourceLocation>(); names.add(MissileAssemblyContent.id("machine_missile_assembly")); names.add(MissileAssemblyContent.id("missile_custom"));
        for (var definition : CustomMissileParts.DEFINITIONS) if (definition.type() != CustomMissileParts.PartType.CHIP) names.add(definition.id());
        for (var name : names) { var key = ModelResourceLocation.inventory(name); var model = event.getModels().get(key); if (model != null) event.getModels().put(key, new BakedModelWrapper<>(model) { @Override public ItemTransforms getTransforms() { return ItemTransforms.NO_TRANSFORMS; } }); }
    }
    private MissileAssemblyClient() {}
}
