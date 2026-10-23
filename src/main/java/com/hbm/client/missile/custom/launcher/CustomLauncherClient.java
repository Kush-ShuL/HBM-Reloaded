package com.hbm.client.missile.custom.launcher;

import com.hbm.client.missile.launchpad.rusted.LegacyLaunchSmokeParticle;
import com.hbm.machine.missileassembly.MissileAssemblyContent;
import com.hbm.missile.custom.launcher.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
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
public final class CustomLauncherClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(CustomLauncherContent.COMPACT_ENTITY.get(), CustomLauncherRenderer::new);
        event.registerBlockEntityRenderer(CustomLauncherContent.TABLE_ENTITY.get(), CustomLauncherRenderer::new);
        CustomLauncherEntity.SMOKE = effect -> { if (effect.level() instanceof ClientLevel level) Minecraft.getInstance().particleEngine.add(new LegacyLaunchSmokeParticle(level, effect.x(), effect.y(), effect.z(), effect.mx(), effect.my(), effect.mz())); };
    }
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event) { event.register(CustomLauncherContent.COMPACT_MENU.get(), CustomLauncherScreen::new); event.register(CustomLauncherContent.TABLE_MENU.get(), CustomLauncherScreen::new); }
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event) {
        for (boolean large : new boolean[]{false, true}) event.registerItem(new IClientItemExtensions() {
            private final CustomLauncherItemRenderer renderer = new CustomLauncherItemRenderer(large);
            @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() { return renderer; }
        }, large ? CustomLauncherContent.TABLE_ITEM.get() : CustomLauncherContent.COMPACT_ITEM.get());
    }
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event) {
        for (String name : new String[]{"compact_launcher", "launch_table"}) { var key = ModelResourceLocation.inventory(MissileAssemblyContent.id(name)); var model = event.getModels().get(key);
            if (model != null) event.getModels().put(key, new BakedModelWrapper<>(model) { @Override public ItemTransforms getTransforms() { return ItemTransforms.NO_TRANSFORMS; } }); }
    }
    private CustomLauncherClient() {}
}
