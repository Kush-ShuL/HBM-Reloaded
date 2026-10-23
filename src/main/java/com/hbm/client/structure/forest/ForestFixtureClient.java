package com.hbm.client.structure.forest;
import com.hbm.world.structure.forest.ForestFixtureContent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class ForestFixtureClient {
 @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->id.id().equals(ForestFixtureContent.TAPE.getId())?new FixtureBakedModel(model,id.getVariant().equals("inventory"),false,event.getTextureGetter()):id.id().equals(ForestFixtureContent.POLES.getId())?new FixtureBakedModel(model,id.getVariant().equals("inventory"),true,event.getTextureGetter()):id.id().equals(ForestFixtureContent.RECEIVER.getId())&&id.getVariant().equals("inventory")?new ReceiverModel(model):model);}
 @SubscribeEvent public static void renderer(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(ForestFixtureContent.RECEIVER_ENTITY.get(),PoleReceiverRenderer::new);}
 @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private PoleReceiverItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new PoleReceiverItemRenderer()):renderer;}},ForestFixtureContent.RECEIVER_ITEM.get());}
 private static final class ReceiverModel extends BakedModelWrapper<BakedModel>{ReceiverModel(BakedModel model){super(model);}@Override public boolean isCustomRenderer(){return true;}@Override public ItemTransforms getTransforms(){return ItemTransforms.NO_TRANSFORMS;}@Override public BakedModel applyTransform(ItemDisplayContext context,PoseStack poses,boolean left){return context==ItemDisplayContext.GUI||context==ItemDisplayContext.FIXED||context==ItemDisplayContext.HEAD||context==ItemDisplayContext.NONE?originalModel.applyTransform(context,poses,left):this;}}
}
