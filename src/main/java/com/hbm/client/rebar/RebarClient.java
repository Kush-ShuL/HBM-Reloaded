package com.hbm.client.rebar;
import com.hbm.block.rebar.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.client.event.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class RebarClient {
 private static ModelResourceLocation model(String name){return ModelResourceLocation.standalone(ResourceLocation.parse("hbm:block/"+name));}
 @SubscribeEvent public static void additional(ModelEvent.RegisterAdditional event){event.register(model("rebar_simple"));event.register(model("rebar_wet"));}
 @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){var simple=java.util.Objects.requireNonNull(event.getModels().get(model("rebar_simple")));event.getModels().replaceAll((id,original)->id.id().equals(RebarContent.BLOCK.getId())&&!id.getVariant().equals("inventory")?new RebarBakedModel(original,simple):original);}
 @SubscribeEvent public static void renderer(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(RebarContent.ENTITY.get(),RebarWetRenderer::new);}
 @SubscribeEvent public static void screen(RegisterMenuScreensEvent event){event.register(RebarContent.MENU.get(),RebarPlacerScreen::new);}
 @SubscribeEvent public static void config(ModConfigEvent.Reloading event){if(event.getConfig().getSpec()==RebarConfig.SPEC)Minecraft.getInstance().execute(()->{if(Minecraft.getInstance().level!=null)Minecraft.getInstance().levelRenderer.allChanged();});}
}
