package com.hbm.client.oldlantern;
import com.hbm.world.oldlantern.OldLanternContent;
import com.hbm.world.oldlantern.OldLanternPacket;
import com.hbm.world.oldlantern.OldLanternEntity;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class OldLanternClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(OldLanternContent.ENTITY.get(),OldLanternRenderer::new);}
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->OldLanternPacket.CLIENT=packet->{var level=Minecraft.getInstance().level;if(level!=null&&level.getBlockEntity(packet.pos()) instanceof OldLanternEntity lantern)lantern.isBroken=packet.broken();});}
}
