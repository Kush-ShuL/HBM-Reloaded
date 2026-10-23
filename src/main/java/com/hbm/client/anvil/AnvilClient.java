package com.hbm.client.anvil;
import com.hbm.machine.anvil.AnvilContent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.extensions.common.*;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class AnvilClient{
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->{if(!id.id().getNamespace().equals("hbm"))return model;for(var tier:AnvilContent.TIERS)if(id.id().getPath().equals("anvil_"+tier.symbol()))return new AnvilBakedModel(model,id.getVariant().equals("inventory"),event.getTextureGetter(),tier);if(id.id().equals(AnvilContent.MOLD.getId())&&id.getVariant().equals("inventory"))return new MoldBakedModel(model,event.getTextureGetter());for(var item:AnvilContent.HOT)if(id.id().equals(item.getId())&&id.getVariant().equals("inventory")){HotItemRenderer.BASE.put(item.get(),model);return new HotModel(model);}return model;});}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){for(var item:AnvilContent.HOT)event.registerItem(new IClientItemExtensions(){private HotItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new HotItemRenderer();return renderer;}},item.get());}
    private static final class HotModel extends BakedModelWrapper<BakedModel>{HotModel(BakedModel original){super(original);}@Override public boolean isCustomRenderer(){return true;}@Override public BakedModel applyTransform(ItemDisplayContext context,PoseStack pose,boolean left){if(context!=ItemDisplayContext.GUI)return originalModel.applyTransform(context,pose,left);getTransforms().getTransform(context).apply(left,pose);return this;}}
}
