package com.hbm.client.solarboiler;
import com.hbm.machine.solarboiler.SolarBoilerContent;import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;import net.neoforged.api.distmarker.Dist;import net.neoforged.bus.api.SubscribeEvent;import net.neoforged.fml.common.EventBusSubscriber;import net.neoforged.neoforge.client.event.*;import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class SolarBoilerClient {
 public static int beamLimit(){return com.hbm.client.config.ClientEffectsConfig.solarBoilerHeliostatBeamLimit();}
 @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->id.id().equals(SolarBoilerContent.BEAM.getId())?new SteelBeamModel(model,id.getVariant().equals("inventory"),event.getTextureGetter()):id.id().equals(SolarBoilerContent.MIRROR.getId())&&!id.getVariant().equals("inventory")?new SolarMirrorModel(model,event.getTextureGetter()):model);}
 @SubscribeEvent public static void renderer(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(SolarBoilerContent.BOILER_BE.get(),SolarBoilerRenderer::new);}
 @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private SolarBoilerItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new SolarBoilerItemRenderer()):renderer;}},SolarBoilerContent.BOILER_ITEM.get(),SolarBoilerContent.MIRROR_ITEM.get());}
}
