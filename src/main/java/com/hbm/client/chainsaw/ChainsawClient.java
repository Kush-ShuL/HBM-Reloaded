package com.hbm.client.chainsaw;

import com.hbm.item.chainsaw.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.extensions.common.*;
import net.neoforged.neoforge.client.model.BakedModelWrapper;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class ChainsawClient {
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event) {
        var renderer=new ChainsawRenderer();event.registerItem(new IClientItemExtensions() {
            @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() { return renderer; }
        },ChainsawContent.CHAINSAW.get());
    }
    @SubscribeEvent public static void setup(FMLClientSetupEvent event) { event.enqueueWork(()->ChainsawAnimationPacket.CLIENT=ChainsawAnimations::receive); }
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event) {
        event.getModels().replaceAll((id,model)->id.getVariant().equals("inventory")&&id.id().equals(ChainsawContent.CHAINSAW.getId())?new Model(model):model);
    }
    private static final class Model extends BakedModelWrapper<BakedModel> {
        Model(BakedModel original) { super(original); }
        @Override public boolean isCustomRenderer() { return true; }
        @Override public boolean isGui3d() { return true; }
        @Override public ItemTransforms getTransforms() { return ItemTransforms.NO_TRANSFORMS; }
        @Override public BakedModel applyTransform(ItemDisplayContext context,PoseStack poses,boolean left) { return this; }
    }
    private ChainsawClient() { }
}
