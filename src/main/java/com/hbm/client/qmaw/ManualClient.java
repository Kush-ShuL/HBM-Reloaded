package com.hbm.client.qmaw;

import com.hbm.client.wiaj.WiajTutorials;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class ManualClient {
 public static final KeyMapping KEY=new KeyMapping("key.hbm.qmaw",KeyConflictContext.GUI,InputConstants.Type.KEYSYM,GLFW.GLFW_KEY_F1,"key.categories.hbm");
 @SubscribeEvent public static void keys(RegisterKeyMappingsEvent event){event.register(KEY);}
 @SubscribeEvent public static void reload(RegisterClientReloadListenersEvent event){event.registerReloadListener(QMAWLoader.INSTANCE);}
 @EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
 public static final class Events {
  private static QuickManualAndWiki lastQmaw;private static ItemStack lastCannery=ItemStack.EMPTY;private static long qmawTime,canneryTime;
  @SubscribeEvent public static void tooltip(ItemTooltipEvent event){
   var stack=event.getItemStack();var page=QMAWLoader.forStack(stack);long now=System.nanoTime();
   if(page!=null){event.getToolTip().add(Component.translatable("qmaw.tab",KEY.getTranslatedKeyMessage()).withStyle(ChatFormatting.YELLOW));lastQmaw=page;qmawTime=now;}
   if(WiajTutorials.has(stack)){event.getToolTip().add(Component.translatable("cannery.f1",Component.literal("Shift + ").append(KEY.getTranslatedKeyMessage())).withStyle(ChatFormatting.GREEN));lastCannery=stack.copyWithCount(1);canneryTime=now;}
  }
  @SubscribeEvent public static void key(ScreenEvent.KeyPressed.Pre event){
   if(!KEY.matches(event.getKeyCode(),event.getScanCode()))return;var mc=Minecraft.getInstance();if(mc.player==null||mc.screen==null)return;long now=System.nanoTime();
   if(Screen.hasShiftDown()){
    ItemStack stack=now-canneryTime<100_000_000L?lastCannery:ItemStack.EMPTY;
    if(stack.isEmpty()&&event.getScreen() instanceof AbstractContainerScreen<?> container){var slot=container.getSlotUnderMouse();if(slot!=null)stack=slot.getItem();}
    if(!stack.isEmpty()&&WiajTutorials.has(stack)){Screen previous=mc.screen instanceof AbstractContainerScreen<?>?null:mc.screen;if(mc.screen instanceof AbstractContainerScreen<?>)mc.player.closeContainer();if(WiajTutorials.open(stack,previous))event.setCanceled(true);}
   }else{
    var page=now-qmawTime<100_000_000L?lastQmaw:null;if(page==null)return;if(mc.screen instanceof AbstractContainerScreen<?>)mc.player.closeContainer();mc.setScreen(new GuiQMAW(page));event.setCanceled(true);
   }
  }
 }
 private ManualClient(){}
}
