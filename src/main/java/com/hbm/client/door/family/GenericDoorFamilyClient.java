package com.hbm.client.door.family;

import com.hbm.door.family.GenericDoorFamilyContent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.extensions.common.*;
import net.neoforged.neoforge.client.model.BakedModelWrapper;

/** Only actual247 item identities; the existing sole GenericDoorRenderer/type/state packet remain registered once. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class GenericDoorFamilyClient {
    @SubscribeEvent public static void items(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private GenericDoorFamilyItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new GenericDoorFamilyItemRenderer()):renderer;}},GenericDoorFamilyContent.DOOR_ITEMS.values().stream().map(item->item.get()).toArray(Item[]::new));}
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->id.getVariant().equals("inventory")&&GenericDoorFamilyContent.DOOR_ITEMS.values().stream().anyMatch(item->id.id().equals(item.getId()))?new ItemModel(model):model);}
    private static final class ItemModel extends BakedModelWrapper<BakedModel>{ItemModel(BakedModel model){super(model);}@Override public boolean isCustomRenderer(){return true;}@Override public ItemTransforms getTransforms(){return ItemTransforms.NO_TRANSFORMS;}@Override public BakedModel applyTransform(ItemDisplayContext context,PoseStack pose,boolean left){return this;}@Override public boolean useAmbientOcclusion(){return false;}}
    private GenericDoorFamilyClient(){}
}
