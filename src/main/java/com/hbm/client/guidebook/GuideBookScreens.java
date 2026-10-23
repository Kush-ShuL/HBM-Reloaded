package com.hbm.client.guidebook;
import com.hbm.item.guidebook.*;import net.minecraft.client.Minecraft;import net.minecraft.world.item.ItemStack;import net.neoforged.api.distmarker.Dist;import net.neoforged.bus.api.SubscribeEvent;import net.neoforged.fml.common.EventBusSubscriber;import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
/** Thin client bridge for the item and root-owned F1/manual entry. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class GuideBookScreens {
 public static boolean open(ItemStack stack){if(!(stack.getItem() instanceof GuideBookItem))return false;Minecraft.getInstance().setScreen(new GuideBookScreen(GuideBookItem.type(stack)));return true;}
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->GuideBookItem.OPEN=GuideBookScreens::open);}
 private GuideBookScreens(){}
}
