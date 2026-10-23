package com.hbm.client.precisionassembler;
import com.hbm.machine.precisionassembler.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class PrecisionAssemblerClient {
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{PrecisionAssemblerBlockEntity.CLIENT_TICK=PrecisionAssemblerAnimation::tick;PrecisionAssemblerBlockEntity.CLIENT_REMOVED=PrecisionAssemblerAnimation::removed;});}
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(PrecisionAssemblerContent.ENTITY.get(),PrecisionAssemblerRenderer::new);}
 @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private PrecisionAssemblerItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new PrecisionAssemblerItemRenderer();return renderer;}},PrecisionAssemblerContent.ITEM.get());}
 @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->id.id().equals(PrecisionAssemblerContent.BROKEN.getId())&&id.getVariant().equals("inventory")?new BrokenItemModel(model,event.getTextureGetter()):model);}
}
