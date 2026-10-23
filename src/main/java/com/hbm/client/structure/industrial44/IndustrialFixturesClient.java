package com.hbm.client.structure.industrial44;
import com.hbm.world.structure.dependencies.industrial44.IndustrialFixturesContent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class IndustrialFixturesClient {
 @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->id.id().equals(IndustrialFixturesContent.POLE.getId())?new PoleTopModel(model,id.getVariant().equals("inventory"),event.getTextureGetter()):id.id().equals(IndustrialFixturesContent.PANE.getId())&&!id.getVariant().equals("inventory")?new ReinforcedPaneModel(model,event.getTextureGetter()):id.id().equals(IndustrialFixturesContent.ROOF.getId())&&id.getVariant().equals("inventory")?new RoofItemModel(model):model);}
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(IndustrialFixturesContent.ROOF_ENTITY.get(),SteelRoofRenderer::new);}
 @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private SteelRoofItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new SteelRoofItemRenderer()):renderer;}},IndustrialFixturesContent.ROOF_ITEM.get());}
 private static final class RoofItemModel extends BakedModelWrapper<BakedModel>{RoofItemModel(BakedModel model){super(model);}@Override public boolean isCustomRenderer(){return true;}@Override public ItemTransforms getTransforms(){return ItemTransforms.NO_TRANSFORMS;}@Override public BakedModel applyTransform(ItemDisplayContext context,PoseStack poses,boolean left){return context==ItemDisplayContext.GUI||context==ItemDisplayContext.FIXED||context==ItemDisplayContext.HEAD||context==ItemDisplayContext.NONE?originalModel.applyTransform(context,poses,left):this;}}
 private IndustrialFixturesClient(){}
}
