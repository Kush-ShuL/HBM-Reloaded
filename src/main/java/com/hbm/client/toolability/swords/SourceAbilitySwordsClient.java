package com.hbm.client.toolability.swords;
import com.hbm.client.toolability.mese.MeseItemRenderer;
import com.hbm.item.toolability.swords.SourceAbilitySwordsContent;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
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
/** Six true ClientProxy522..529 ItemRenderTransformer registrations; electric keeps its original ordinary animated atlas icon. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class SourceAbilitySwordsClient {
 private static final String[] SPECIAL={"schrabidium_sword","alloy_sword","cmb_sword","desh_sword","cobalt_decorated_sword","dnt_sword"};
 @SubscribeEvent public static void additional(ModelEvent.RegisterAdditional event){for(var name:SPECIAL)event.register(ModelResourceLocation.standalone(ResourceLocation.parse("hbm:item/"+name+"_ordinary")));}
 @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){var items=List.of(SourceAbilitySwordsContent.SCHRABIDIUM,SourceAbilitySwordsContent.ALLOY,SourceAbilitySwordsContent.CMB,SourceAbilitySwordsContent.DESH,SourceAbilitySwordsContent.DECORATED_COBALT,SourceAbilitySwordsContent.DNT);for(int i=0;i<SPECIAL.length;i++){String name=SPECIAL[i];boolean low=name.equals("desh_sword");var renderer=new MeseItemRenderer(ResourceLocation.parse("hbm:textures/item/"+name+".png"),ModelResourceLocation.standalone(ResourceLocation.parse("hbm:item/"+name+"_ordinary")),low?0:.125,low?.75:.625,1.36F,1.36F,.68F,32,32);event.registerItem(new IClientItemExtensions(){@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer;}},items.get(i).get());}}
 @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->id.getVariant().equals("inventory")&&id.id().getNamespace().equals("hbm")&&Arrays.asList(SPECIAL).contains(id.id().getPath())?new SourceModel(model):model);}
 private static final class SourceModel extends BakedModelWrapper<BakedModel>{SourceModel(BakedModel original){super(original);}@Override public boolean isCustomRenderer(){return true;}@Override public ItemTransforms getTransforms(){return ItemTransforms.NO_TRANSFORMS;}@Override public BakedModel applyTransform(ItemDisplayContext context,PoseStack poses,boolean left){return this;}}
 private SourceAbilitySwordsClient(){}
}
