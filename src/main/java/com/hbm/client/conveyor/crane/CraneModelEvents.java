package com.hbm.client.conveyor.crane;

import com.hbm.conveyor.crane.CraneBaseBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

/** Sole IBlockSideRotation renderer for actual CraneBaseBlock subclasses; no unknown-ID substitution. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class CraneModelEvents {
    @SubscribeEvent public static void bake(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((location,model)->{if(!location.id().getNamespace().equals("hbm"))return model;var block=BuiltInRegistries.BLOCK.getOptional(location.id()).orElse(null);return block instanceof CraneBaseBlock crane?new CraneBakedModel(model,crane,event.getTextureGetter()):model;});}
    private CraneModelEvents(){}
}
