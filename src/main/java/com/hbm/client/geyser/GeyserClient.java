package com.hbm.client.geyser;
import com.hbm.world.structure.geyser.GeyserContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class GeyserClient {@SubscribeEvent public static void renderer(EntityRenderersEvent.RegisterRenderers event){event.registerEntityRenderer(GeyserContent.ORANGE.get(),AgentOrangeRenderer::new);}}
