package com.hbm.client.turret.jeremy;
import com.hbm.machine.turret.jeremy.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class JeremyTurretClient {
    @SubscribeEvent public static void setup(net.neoforged.fml.event.lifecycle.FMLClientSetupEvent event){event.enqueueWork(()->net.minecraft.client.renderer.item.ItemProperties.register(JeremyTurretContent.AMMO.get(),net.minecraft.resources.ResourceLocation.parse("hbm:legacy_meta"),(stack,world,living,seed)->JeremyTurretContent.AMMO.get().ordinal(stack)));}
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){for(var kind:JeremyTurretKind.values())event.register(JeremyTurretContent.menu(kind),JeremyTurretScreen::new);}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){for(var kind:JeremyTurretKind.values())event.registerBlockEntityRenderer(JeremyTurretContent.entity(kind),JeremyTurretRenderer::new);}
    @SubscribeEvent public static void extensions(net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent event){for(var kind:JeremyTurretKind.values())event.registerItem(new net.neoforged.neoforge.client.extensions.common.IClientItemExtensions(){private com.hbm.client.turret.rotary.TurretFamilyItemRenderer renderer;@Override public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new com.hbm.client.turret.rotary.TurretFamilyItemRenderer(com.hbm.client.turret.rotary.TurretFamilyItemRenderer.Kind.JEREMY);return renderer;}},JeremyTurretContent.block(kind).asItem());}
}
