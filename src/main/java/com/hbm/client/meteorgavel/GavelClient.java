package com.hbm.client.meteorgavel;

import com.hbm.item.meteorgavel.MeteorGavelContent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.extensions.common.*;
import net.neoforged.neoforge.client.model.BakedModelWrapper;

@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class GavelClient {
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event) {
        var extension = new IClientItemExtensions() { private GavelItemRenderer renderer; @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() { if (renderer == null) renderer = new GavelItemRenderer(); return renderer; } };
        MeteorGavelContent.all().forEach(item -> event.registerItem(extension, item.get()));
    }
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event) {
        event.getModels().replaceAll((id, model) -> id.getVariant().equals("inventory") && id.id().getNamespace().equals("hbm") && java.util.Set.of("lead_gavel", "diamond_gavel", "mese_gavel").contains(id.id().getPath()) ? new GavelModel(model) : model);
    }
    private static final class GavelModel extends BakedModelWrapper<BakedModel> {
        GavelModel(BakedModel original) { super(original); }
        @Override public boolean isCustomRenderer() { return true; }
        @Override public boolean isGui3d() { return true; }
        @Override public ItemTransforms getTransforms() { return ItemTransforms.NO_TRANSFORMS; }
        @Override public BakedModel applyTransform(ItemDisplayContext context, PoseStack poses, boolean left) {
            // Original handleRenderType explicitly declines INVENTORY: retain its real generated icon/model and transforms.
            if (context == ItemDisplayContext.GUI) return originalModel.applyTransform(context, poses, left);
            return this;
        }
    }
}
