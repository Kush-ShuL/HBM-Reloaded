package com.hbm.client.recipedependencies;
import com.hbm.machine.refinery.deco.DecoSteelModel;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
/** Reuse the one true CTContext/IconCT implementation, same-block connections ignore the source metadata. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class RecipeInputsClient {
 @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){var full=event.getTextureGetter().apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.parse("hbm:block/reinforced_laminate")));var connected=event.getTextureGetter().apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.parse("hbm:block/reinforced_laminate_ct")));event.getModels().replaceAll((key,model)->key.id().equals(ResourceLocation.parse("hbm:reinforced_laminate"))&&!key.getVariant().equals("inventory")?DecoSteelModel.wrap(model,full,connected,(state,other)->other.is(state.getBlock())):model);}
 private RecipeInputsClient(){}
}
