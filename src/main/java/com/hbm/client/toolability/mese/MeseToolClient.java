package com.hbm.client.toolability.mese;
import com.hbm.item.toolability.mese.MeseToolContent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.extensions.common.*;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class MeseToolClient {
 @SubscribeEvent public static void models(ModelEvent.RegisterAdditional event){event.register(ModelResourceLocation.standalone(ResourceLocation.parse("hbm:item/mese_pickaxe_ordinary")));}
 @SubscribeEvent public static void items(RegisterClientExtensionsEvent event){var renderer=new MeseItemRenderer();event.registerItem(new IClientItemExtensions(){@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer;}},MeseToolContent.PICKAXE.get());}
 @SubscribeEvent public static void baked(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->id.getVariant().equals("inventory")&&id.id().equals(MeseToolContent.PICKAXE.getId())?new Model(model):model);}
 private static final class Model extends BakedModelWrapper<BakedModel>{
  Model(BakedModel original){super(original);}
  @Override public boolean isCustomRenderer(){return true;}
  @Override public ItemTransforms getTransforms(){return ItemTransforms.NO_TRANSFORMS;}
  @Override public BakedModel applyTransform(ItemDisplayContext context,PoseStack poses,boolean left){return this;}
 }
 private MeseToolClient(){}
}
