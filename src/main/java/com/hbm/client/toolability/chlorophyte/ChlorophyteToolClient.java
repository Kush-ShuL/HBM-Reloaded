package com.hbm.client.toolability.chlorophyte;
import com.hbm.client.toolability.mese.MeseItemRenderer;
import com.hbm.item.toolability.chlorophyte.ChlorophyteToolContent;
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
/** ClientProxy535/537/539: the same real ItemRenderTransformer as Mese. Star/decorated cobalt retain ordinary full3D item rendering. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class ChlorophyteToolClient {
 private static final String[] NAMES={"bismuth_pickaxe","volcanic_pickaxe","chlorophyte_pickaxe"};
 @SubscribeEvent public static void models(ModelEvent.RegisterAdditional event){for(String name:NAMES)event.register(ModelResourceLocation.standalone(ResourceLocation.parse("hbm:item/"+name+"_ordinary")));}
 @SubscribeEvent public static void items(RegisterClientExtensionsEvent event){
  var items=java.util.List.of(ChlorophyteToolContent.BISMUTH,ChlorophyteToolContent.VOLCANIC,ChlorophyteToolContent.CHLOROPHYTE);
  for(int i=0;i<NAMES.length;i++){
   String name=NAMES[i];var renderer=new MeseItemRenderer(ResourceLocation.parse("hbm:textures/item/"+name+".png"),ModelResourceLocation.standalone(ResourceLocation.parse("hbm:item/"+name+"_ordinary")));
   event.registerItem(new IClientItemExtensions(){@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer;}},items.get(i).get());
  }
 }
 @SubscribeEvent public static void baked(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->id.getVariant().equals("inventory")&&id.id().getNamespace().equals("hbm")&&java.util.Arrays.asList(NAMES).contains(id.id().getPath())?new Model(model):model);}
 private static final class Model extends BakedModelWrapper<BakedModel>{
  Model(BakedModel original){super(original);}
  @Override public boolean isCustomRenderer(){return true;}
  @Override public ItemTransforms getTransforms(){return ItemTransforms.NO_TRANSFORMS;}
  @Override public BakedModel applyTransform(ItemDisplayContext context,PoseStack poses,boolean left){return this;}
 }
 private ChlorophyteToolClient(){}
}
