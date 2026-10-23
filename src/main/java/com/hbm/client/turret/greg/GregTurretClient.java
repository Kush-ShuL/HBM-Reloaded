package com.hbm.client.turret.greg;
import com.hbm.machine.turret.greg.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class GregTurretClient {
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){for(var kind:GregTurretKind.values())event.register(GregTurretContent.menu(kind),GregTurretScreen::new);}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){for(var kind:GregTurretKind.values())event.registerBlockEntityRenderer(GregTurretContent.entity(kind),GregTurretRenderer::new);}
    @SubscribeEvent public static void items(net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent event){for(var kind:GregTurretKind.values())event.registerItem(new net.neoforged.neoforge.client.extensions.common.IClientItemExtensions(){private com.hbm.client.turret.rotary.TurretFamilyItemRenderer renderer;@Override public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new com.hbm.client.turret.rotary.TurretFamilyItemRenderer(com.hbm.client.turret.rotary.TurretFamilyItemRenderer.Kind.GREG);return renderer;}},GregTurretContent.block(kind).asItem());}

}
