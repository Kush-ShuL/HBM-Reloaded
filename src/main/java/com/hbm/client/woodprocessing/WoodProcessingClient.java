package com.hbm.client.woodprocessing;
import com.hbm.machine.woodprocessing.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class WoodProcessingClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(WoodProcessingContent.SAWMILL_ENTITY.get(),SawmillRenderer::new);event.registerBlockEntityRenderer(WoodProcessingContent.AUTOSAW_ENTITY.get(),AutosawRenderer::new);event.registerEntityRenderer(WoodProcessingContent.SAWBLADE.get(),SawbladeRenderer::new);}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){var extension=new IClientItemExtensions(){private WoodItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new WoodItemRenderer()):renderer;}};event.registerItem(extension,WoodProcessingContent.SAWMILL_ITEM.get(),WoodProcessingContent.AUTOSAW_ITEM.get());}
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{WoodEffects.CLIENT=effect->com.hbm.client.bomb.conventional.SmallExplosionEffects.sourceBlockDustBurst(effect.x(),effect.y(),effect.z(),effect.count(),.1D,net.minecraft.world.level.block.Blocks.REDSTONE_BLOCK.defaultBlockState());AutosawEntity.CLIENT_TICK=AutosawSound::tick;AutosawEntity.CLIENT_REMOVED=AutosawSound::removed;});}
}
