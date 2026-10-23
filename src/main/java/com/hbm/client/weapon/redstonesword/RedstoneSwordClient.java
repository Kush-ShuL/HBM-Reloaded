package com.hbm.client.weapon.redstonesword;

import com.hbm.weapon.redstonesword.RedstoneSwordContent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.model.BakedModelWrapper;

@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class RedstoneSwordClient {
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            private RedstoneSwordItemRenderer renderer;
            @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) renderer = new RedstoneSwordItemRenderer();
                return renderer;
            }
            @Override public boolean shouldBobAsEntity(ItemStack stack) { return false; }
        }, RedstoneSwordContent.SWORD.get());
    }
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event) {
        event.getModels().replaceAll((id, model) -> id.getVariant().equals("inventory")
            && id.id().toString().equals("hbm:redstone_sword") ? new SourceModel(model) : model);
    }
    private static final class SourceModel extends BakedModelWrapper<BakedModel> {
        SourceModel(BakedModel original) { super(original); }
        @Override public boolean isCustomRenderer() { return true; }
        @Override public boolean isGui3d() { return true; }
        @Override public ItemTransforms getTransforms() { return ItemTransforms.NO_TRANSFORMS; }
        @Override public BakedModel applyTransform(ItemDisplayContext context, PoseStack poses, boolean left) {
            return context.firstPerson() || context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND
                || context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND || context == ItemDisplayContext.GROUND
                ? this : originalModel.applyTransform(context, poses, left);
        }
    }
    private RedstoneSwordClient() { }
}
