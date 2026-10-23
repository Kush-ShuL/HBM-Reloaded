package com.hbm.client.turret.howard;
import com.hbm.machine.turret.howard.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class HowardTurretClient {
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){for(var kind:HowardTurretKind.values())if(!kind.damaged)event.register(HowardTurretContent.menu(kind),HowardTurretScreen::new);}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){for(var kind:HowardTurretKind.values())event.registerBlockEntityRenderer(HowardTurretContent.entity(kind),HowardTurretRenderer::new);}
    @SubscribeEvent public static void items(net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent event){for(var kind:HowardTurretKind.values())event.registerItem(new net.neoforged.neoforge.client.extensions.common.IClientItemExtensions(){private com.hbm.client.turret.rotary.TurretFamilyItemRenderer renderer;@Override public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new com.hbm.client.turret.rotary.TurretFamilyItemRenderer(kind.damaged?com.hbm.client.turret.rotary.TurretFamilyItemRenderer.Kind.HOWARD_DAMAGED:com.hbm.client.turret.rotary.TurretFamilyItemRenderer.Kind.HOWARD);return renderer;}},HowardTurretContent.block(kind).asItem());}

}
