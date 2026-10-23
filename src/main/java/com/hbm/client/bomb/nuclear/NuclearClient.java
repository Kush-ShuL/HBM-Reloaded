package com.hbm.client.bomb.nuclear;

import com.hbm.bomb.nuclear.NuclearContent;
import com.hbm.bomb.nuclear.NuclearTerrain;
import com.hbm.block.ore.SellafieldModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class NuclearClient {
 private NuclearClient(){}
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerEntityRenderer(NuclearContent.TOREX.get(),NuclearTorexRenderer::new);event.registerEntityRenderer(NuclearContent.MK5.get(),NoopRenderer::new);event.registerEntityRenderer(NuclearContent.FALLOUT_RAIN.get(),com.hbm.client.bomb.nuclear.fallout.FalloutRainRenderer::new);event.registerEntityRenderer(NuclearContent.FALLING_BLOCK.get(),NuclearFallingBlockRenderer::new);event.registerBlockEntityRenderer(NuclearContent.BOMB_ENTITY.get(),NuclearBombRenderer::new);}
 @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){for(var menu:NuclearContent.MENUS.values())event.register(menu.get(),NuclearAssemblyScreen::new);}
 @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){var extension=new IClientItemExtensions(){private NuclearBombItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new NuclearBombItemRenderer();return renderer;}};for(var block:NuclearContent.BOMBS.values())event.registerItem(extension,block.get().asItem());}
 private static ModelResourceLocation model(String name,int variant){return ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("hbm","block/"+name+"."+variant));}
 @SubscribeEvent public static void additionalModels(ModelEvent.RegisterAdditional event){for(String name:new String[]{"waste_trinitite","waste_trinitite_red"})for(int i=0;i<4;i++)event.register(model(name,i));}
 @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){for(String name:new String[]{"waste_trinitite","waste_trinitite_red"}){var variants=new BakedModel[4];for(int i=0;i<4;i++)variants[i]=event.getModels().get(model(name,i));event.getModels().replaceAll((key,original)->key.id().equals(ResourceLocation.fromNamespaceAndPath("hbm",name))?new NuclearTrinititeModel(original,variants):original);}var hosts=new BakedModel[4];for(int i=0;i<4;i++)hosts[i]=event.getModels().get(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("hbm","block/sellafield_slaked.host_"+i)));event.getModels().replaceAll((key,original)->key.id().equals(ResourceLocation.fromNamespaceAndPath("hbm","sellafield_bedrock"))?new SellafieldModel(original,hosts):original);}
 @SubscribeEvent public static void colors(RegisterColorHandlersEvent.Block event){event.register((state,level,pos,tint)->tint==0?java.awt.Color.HSBtoRGB(0,0,1F-state.getValue(NuclearTerrain.SlakedBedrock.META)/15F):0xFFFFFF,NuclearTerrain.BEDROCK.get());}
}
