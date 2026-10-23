package com.hbm.client.missile.launchpad.rusted;
import com.hbm.missile.launchpad.rusted.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class RustedLaunchPadClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(RustedLaunchPadContent.ENTITY.get(),RustedLaunchPadRenderer::new);RustedLaunchPadBlockEntity.smoke=effect->{if(effect.level() instanceof ClientLevel level)Minecraft.getInstance().particleEngine.add(new LegacyLaunchSmokeParticle(level,effect.x(),effect.y(),effect.z(),effect.mx(),effect.my(),effect.mz()));};}
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(RustedLaunchPadContent.MENU.get(),RustedLaunchPadScreen::new);}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private RustedLaunchPadItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new RustedLaunchPadItemRenderer();return renderer;}},RustedLaunchPadContent.ITEM.get());}
}
