package com.hbm.client.turret.rotary;
import com.hbm.machine.turret.rotary.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class RotaryTurretClient {
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){for(var kind:RotaryTurretKind.values())event.register(RotaryTurretContent.menu(kind),RotaryTurretScreen::new);}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){for(var kind:RotaryTurretKind.values())event.registerBlockEntityRenderer(RotaryTurretContent.entity(kind),RotaryTurretRenderer::new);}
    @SubscribeEvent public static void extensions(net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent event){for(var kind:RotaryTurretKind.values())event.registerItem(new net.neoforged.neoforge.client.extensions.common.IClientItemExtensions(){private TurretFamilyItemRenderer renderer;@Override public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new TurretFamilyItemRenderer(kind==RotaryTurretKind.FRIENDLY?TurretFamilyItemRenderer.Kind.FRIENDLY:TurretFamilyItemRenderer.Kind.CHEKHOV);return renderer;}},RotaryTurretContent.block(kind).asItem());}
}
