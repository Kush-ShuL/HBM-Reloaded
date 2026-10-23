package com.hbm.client.entity.sourcecreepers;
import com.hbm.entity.mob.sourcecreepers.SourceCreeperContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class SourceCreeperClient {
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerEntityRenderer(SourceCreeperContent.NUCLEAR.get(),context->new SourceCreeperRenderer(context,"creeper.png","creeper_armor.png",5));event.registerEntityRenderer(SourceCreeperContent.PHOSGENE.get(),context->new SourceCreeperRenderer(context,"creeper_phosgene.png","source_creeper_armor.png",1));event.registerEntityRenderer(SourceCreeperContent.VOLATILE.get(),context->new SourceCreeperRenderer(context,"creeper_volatile.png","source_creeper_armor.png",1));event.registerEntityRenderer(SourceCreeperContent.GOLD.get(),context->new SourceCreeperRenderer(context,"creeper_gold.png","source_creeper_armor.png",1));}
 private SourceCreeperClient(){}
}
