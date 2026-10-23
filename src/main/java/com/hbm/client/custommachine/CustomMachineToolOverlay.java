package com.hbm.client.custommachine;
import com.hbm.machine.custommachine.*;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
/** Source positioning readout; user preference removes only the old opaque crosshair background. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class CustomMachineToolOverlay {
 @SubscribeEvent public static void logout(ClientPlayerNetworkEvent.LoggingOut event){CustomMachinesConfig.restoreLocal();}
 @SubscribeEvent public static void draw(RenderGuiLayerEvent.Post event){if(!VanillaGuiLayers.CROSSHAIR.equals(event.getName()))return;var mc=Minecraft.getInstance();if(mc.player==null||mc.level==null||mc.options.hideGui)return;var stack=mc.player.getMainHandItem();if(!stack.is(CustomMachineContent.STRUCTURE_TOOL.get()))return;var anchor=CMStructureItem.anchor(stack);var lines=new ArrayList<Component>();if(anchor==null)lines.add(Component.translatable("gui.hbm.custom_machine.no_anchor").withStyle(ChatFormatting.RED));else{lines.add(Component.translatable("gui.hbm.custom_machine.anchor",anchor.getX(),anchor.getY(),anchor.getZ()).withStyle(ChatFormatting.GOLD));var tag=CMStructureItem.data(stack);for(int i=1;i<=2;i++)if(tag.contains("x"+i))lines.add(Component.translatable("gui.hbm.custom_machine.position",i,tag.getInt("x"+i),tag.getInt("y"+i),tag.getInt("z"+i)).withStyle(ChatFormatting.YELLOW));}int x=event.getGuiGraphics().guiWidth()/2+7,y=event.getGuiGraphics().guiHeight()/2+7;event.getGuiGraphics().drawString(mc.font,stack.getHoverName(),x,y,0xffff00,false);for(var line:lines){y+=10;event.getGuiGraphics().drawString(mc.font,line,x,y,0xffffff,false);}}
}
