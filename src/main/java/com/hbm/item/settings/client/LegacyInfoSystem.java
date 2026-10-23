package com.hbm.item.settings.client;

import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.energy.wire.PylonConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

/** Original RenderInfoSystem inbox, keyed replacement, lifetime ordering, four positions and alpha fade. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class LegacyInfoSystem {
    private static final Map<Integer,Entry> INBOX=new HashMap<>(),MESSAGES=new HashMap<>();
    private static int nextID=1000;
    private LegacyInfoSystem() {}
    /** Source ILookOverlay.printGeneric: immediate look data, independent of timed notices and their box/position. */
    public static void printGeneric(GuiGraphics graphics,Component title,int titleColor,int titleShadow,List<Component> lines) {
        var font=LegacyFonts.get();int x=graphics.guiWidth()/2+8,y=graphics.guiHeight()/2;
        graphics.drawString(font,title,x+1,y-9,titleShadow,false);
        graphics.drawString(font,title,x,y-10,titleColor,false);
        for(var line:lines){graphics.drawString(font,line,x,y,0xffffff,true);y+=10;}
    }
    public static void push(Component text,int millis,int id) { INBOX.put(id==0?nextID++:id,new Entry(text.copy(),System.currentTimeMillis(),millis)); }
    public static void push(Component text,int id) { push(text,1000,id); }
    @SubscribeEvent(priority=EventPriority.LOWEST) public static void tick(ClientTickEvent.Post event) {
        MESSAGES.putAll(INBOX);INBOX.clear();long now=System.currentTimeMillis();MESSAGES.values().removeIf(entry->entry.started()+entry.millis()<now);
    }
    @SubscribeEvent(receiveCanceled=true) public static void render(RenderGuiLayerEvent.Pre event) {
        if(!VanillaGuiLayers.CROSSHAIR.equals(event.getName())||MESSAGES.isEmpty()) return;
        var minecraft=Minecraft.getInstance();if(minecraft.options.hideGui||minecraft.level==null||minecraft.player==null) return;
        var entries=new ArrayList<>(MESSAGES.values());Collections.sort(entries);var graphics=event.getGuiGraphics();var font=LegacyFonts.get();int longest=0;
        for(Entry entry:entries) longest=Math.max(longest,font.width(entry.text()));
        int mode=PylonConfig.INFO_POSITION.get(),x=mode==0?15:mode==1?graphics.guiWidth()-longest-15:mode==2?graphics.guiWidth()/2+7:graphics.guiWidth()/2-longest-6,y=mode==0||mode==1?15:graphics.guiHeight()/2+7;
        x+=PylonConfig.INFO_OFFSET_HORIZONTAL.get();y+=PylonConfig.INFO_OFFSET_VERTICAL.get();
        graphics.fill(x-5,y-5,x+longest+5,y+entries.size()*10+2,0x80404040);long now=System.currentTimeMillis();int offset=0;
        for(Entry entry:entries) { int elapsed=(int)(now-entry.started());int alpha=Math.max(Math.min(510*(entry.millis()-elapsed)/entry.millis(),255),5);graphics.drawString(font,entry.text(),x,y+offset,(alpha<<24)|0xFFFFFF,false);offset+=10; }
    }
    private record Entry(Component text,long started,int millis) implements Comparable<Entry> {
        @Override public int compareTo(Entry other) { return Integer.compare(millis,other.millis); }
    }
}
