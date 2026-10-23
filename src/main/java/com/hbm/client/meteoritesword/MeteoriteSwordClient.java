package com.hbm.client.meteoritesword;
import com.hbm.item.meteoritesword.MeteoriteSwordContent;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class MeteoriteSwordClient {
 @SubscribeEvent public static void models(ModelEvent.RegisterAdditional event){event.register(net.minecraft.client.resources.model.ModelResourceLocation.standalone(ResourceLocation.parse("hbm:item/meteorite_sword_ordinary")));}
 @SubscribeEvent public static void items(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private MeteoriteSwordRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new MeteoriteSwordRenderer()):renderer;}},MeteoriteSwordContent.indexedItems().stream().map(net.minecraft.world.item.ItemStack::getItem).toArray(net.minecraft.world.item.Item[]::new));}
 private MeteoriteSwordClient(){}
}
