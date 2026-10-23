package com.hbm.client.toolability.sourceextras;

import com.hbm.client.toolability.mese.MeseItemRenderer;
import com.hbm.item.toolability.sourceextras.SourceAbilityToolsContent;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.model.BakedModelWrapper;

/** ClientProxy536/538/540/542: reuse the single real parameterized ItemRenderTransformer renderer. */
@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class SourceAbilityToolsClient {
    private static final String[] SPECIAL = {"bismuth_axe", "volcanic_axe", "chlorophyte_axe", "mese_axe"};
    @SubscribeEvent public static void models(ModelEvent.RegisterAdditional event) {
        for (String name : SPECIAL) event.register(ModelResourceLocation.standalone(ResourceLocation.parse("hbm:item/" + name + "_ordinary")));
    }
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event) {
        var items = List.of(SourceAbilityToolsContent.BISMUTH_AXE, SourceAbilityToolsContent.VOLCANIC_AXE,
                SourceAbilityToolsContent.CHLOROPHYTE_AXE, SourceAbilityToolsContent.MESE_AXE);
        for (int i = 0; i < SPECIAL.length; i++) {
            String name = SPECIAL[i];
            var renderer = new MeseItemRenderer(ResourceLocation.parse("hbm:textures/item/" + name + ".png"),
                    ModelResourceLocation.standalone(ResourceLocation.parse("hbm:item/" + name + "_ordinary")));
            event.registerItem(new IClientItemExtensions() {
                @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() { return renderer; }
            }, items.get(i).get());
        }
    }
    @SubscribeEvent public static void baked(ModelEvent.ModifyBakingResult event) {
        event.getModels().replaceAll((id, model) -> id.getVariant().equals("inventory") && id.id().getNamespace().equals("hbm")
                && Arrays.asList(SPECIAL).contains(id.id().getPath()) ? new CustomModel(model) : model);
    }
    private static final class CustomModel extends BakedModelWrapper<BakedModel> {
        CustomModel(BakedModel original) { super(original); }
        @Override public boolean isCustomRenderer() { return true; }
        @Override public ItemTransforms getTransforms() { return ItemTransforms.NO_TRANSFORMS; }
        @Override public BakedModel applyTransform(ItemDisplayContext context, PoseStack poses, boolean left) { return this; }
    }
    private SourceAbilityToolsClient() { }
}
