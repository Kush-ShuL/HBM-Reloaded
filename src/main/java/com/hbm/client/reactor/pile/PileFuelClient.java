package com.hbm.client.reactor.pile;

import com.hbm.machine.reactor.pile.*;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;

/** Exact original sprite identity and word-only225px wrapping, using the player's actual Minecraft.font. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class PileFuelClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{ItemProperties.register(PileFuelContent.ITEM.get(),ResourceLocation.parse("hbm:legacy_metadata"),(stack,level,entity,seed)->PileFuelItem.type(stack).ordinal());PileFuelItem.WRAP=PileFuelClient::wrap;});}
    private static List<Component> wrap(Component component){var font=Minecraft.getInstance().font;String[] words=component.getString().split(" ");var lines=new ArrayList<String>();lines.add(words[0]);int indent=font.width(words[0]);for(int i=1;i<words.length;i++){indent+=font.width(" "+words[i]);if(indent<=225)lines.set(lines.size()-1,lines.getLast()+" "+words[i]);else{lines.add(words[i]);indent=font.width(words[i]);}}return lines.stream().map(line->(Component)Component.literal(line)).toList();}
    @SubscribeEvent public static void bars(RegisterItemDecorationsEvent event){event.register(PileFuelContent.ITEM.get(),(graphics,font,stack,x,y)->{if(!stack.isBarVisible())return false;int green=(int)Math.round(255-PileFuelItem.getDurabilityForDisplay(stack)*255),color=(255-green)<<16|green<<8,shadow=(255-green)/4<<16|16128;graphics.fill(net.minecraft.client.renderer.RenderType.guiOverlay(),x+2,y+13,x+15,y+15,0xff000000);graphics.fill(net.minecraft.client.renderer.RenderType.guiOverlay(),x+2,y+13,x+14,y+14,0xff000000|shadow);graphics.fill(net.minecraft.client.renderer.RenderType.guiOverlay(),x+2,y+13,x+2+stack.getBarWidth(),y+14,0xff000000|color);return false;});}
    private PileFuelClient(){}
}
