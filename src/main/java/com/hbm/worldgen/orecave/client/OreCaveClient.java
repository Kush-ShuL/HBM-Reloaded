package com.hbm.worldgen.orecave.client;

import com.hbm.worldgen.orecave.OreCaves;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

/** Real ItemBlockMulti flat sprites and original seven metadata variants. World cross models declare their native cutout layer. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class OreCaveClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{for(var holder:java.util.List.of(OreCaves.STALAGMITE_ITEM,OreCaves.STALACTITE_ITEM)){var item=holder.get();ItemProperties.register(item,ResourceLocation.parse("hbm:legacy_metadata"),(stack,level,owner,seed)->item.ordinal(stack));}});}
    private OreCaveClient(){}
}
