package com.hbm.client.armor.desh;
import com.hbm.armor.desh.DeshContent;
import com.hbm.client.armor.t51.T51ArmorRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.*;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.extensions.common.*;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
/** Source actual worn OBJ and ItemRendererProvider both retained. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class DeshClient {
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private final DeshItemRenderer renderer=new DeshItemRenderer();@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer;}},DeshContent.HELMET.get(),DeshContent.PLATE.get(),DeshContent.LEGS.get(),DeshContent.BOOTS.get());}
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{for(var item:DeshContent.indexedItems())T51ArmorRenderer.register(item.getItem(),DeshArmorRenderer::render);});}
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){for(var name:new String[]{"helmet","plate","legs","boots"}){var id=ModelResourceLocation.inventory(DeshContent.id("steamsuit_"+name));var model=event.getModels().get(id);if(model!=null)event.getModels().put(id,new BakedModelWrapper<BakedModel>(model){@Override public boolean isCustomRenderer(){return true;}@Override public boolean isGui3d(){return true;}@Override public ItemTransforms getTransforms(){return ItemTransforms.NO_TRANSFORMS;}@Override public BakedModel applyTransform(ItemDisplayContext context,PoseStack pose,boolean left){return this;}});}}
    private DeshClient(){}
}
