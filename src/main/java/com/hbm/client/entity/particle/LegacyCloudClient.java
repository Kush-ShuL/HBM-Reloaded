package com.hbm.client.entity.particle;
import com.hbm.entity.particle.LegacyCloudContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
/** Source ClientProxy764/766 actual8frame maps; same one shared MultiCloud renderer as existing pink. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class LegacyCloudClient {
    private LegacyCloudClient(){}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(LegacyCloudContent.CHLORINE.get(),context->new SourceMultiCloudRenderer<>(context,"chlorine"));
        event.registerEntityRenderer(LegacyCloudContent.CLOUD.get(),context->new SourceMultiCloudRenderer<>(context,"cloud"));
    }
}
