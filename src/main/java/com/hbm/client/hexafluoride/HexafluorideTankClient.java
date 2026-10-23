package com.hbm.client.hexafluoride;

import com.hbm.machine.hexafluoride.HexafluorideTankContent;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.*;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class HexafluorideTankClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(HexafluorideTankContent.UF6_ENTITY.get(),HexafluorideTankRenderer::new);event.registerBlockEntityRenderer(HexafluorideTankContent.PUF6_ENTITY.get(),HexafluorideTankRenderer::new);}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private HexafluorideTankItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new HexafluorideTankItemRenderer()):renderer;}},HexafluorideTankContent.UF6_ITEM.get(),HexafluorideTankContent.PUF6_ITEM.get());}
    private HexafluorideTankClient(){}
}
