package com.hbm.client.door.generic;
import com.hbm.door.generic.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class GenericDoorClient{
 static {GenericDoorBlockEntity.CLIENT_STATE=GenericDoorSound::state;GenericDoorBlockEntity.CLIENT_REMOVE=GenericDoorSound::remove;}
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(GenericDoorContent.ENTITY.get(),GenericDoorRenderer::new);}
 @SubscribeEvent public static void items(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private GenericDoorItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new GenericDoorItemRenderer()):renderer;}},GenericDoorContent.SILO_ITEM.get(),GenericDoorContent.CARGO_ITEM.get(),GenericDoorContent.LARGE_SILO_ITEM.get());}
 @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->(id.id().equals(GenericDoorContent.SILO_ITEM.getId())||id.id().equals(GenericDoorContent.CARGO_ITEM.getId())||id.id().equals(GenericDoorContent.LARGE_SILO_ITEM.getId()))&&id.getVariant().equals("inventory")?new ItemModel(model):model);}
 private static final class ItemModel extends BakedModelWrapper<BakedModel>{ItemModel(BakedModel model){super(model);}@Override public boolean isCustomRenderer(){return true;}@Override public ItemTransforms getTransforms(){return ItemTransforms.NO_TRANSFORMS;}@Override public BakedModel applyTransform(ItemDisplayContext context,PoseStack poses,boolean left){return this;}@Override public boolean useAmbientOcclusion(){return false;}}
}
