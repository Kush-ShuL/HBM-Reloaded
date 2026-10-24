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
    /** 观察提示统一门控；读取当前客户端配置，不缓存或读取旧JSON开关。 */
    public static boolean lookOverlayEnabled() {
        var minecraft=Minecraft.getInstance();
        return com.hbm.client.config.ClientEffectsConfig.lookOverlayEnabled()
                &&minecraft.level!=null&&minecraft.player!=null&&!minecraft.options.hideGui;
    }
    /** Source ILookOverlay.printGeneric: immediate look data, independent of timed notices and their box/position. */
    public static void printGeneric(GuiGraphics graphics,Component title,int titleColor,int titleShadow,List<Component> lines) {
        if(!lookOverlayEnabled())return;
        var font=LegacyFonts.get();int x=graphics.guiWidth()/2+8,y=graphics.guiHeight()/2;
        graphics.drawString(font,title,x+1,y-9,titleShadow,false);
        graphics.drawString(font,title,x,y-10,titleColor,false);
        for(var line:lines){graphics.drawString(font,line,x,y,0xffffff,true);y+=10;}
    }
    private static void renderBlockStateDebug(GuiGraphics graphics) {
        var minecraft=Minecraft.getInstance();
        if(!lookOverlayEnabled()||!com.hbm.client.config.ClientEffectsConfig.blockStateDebugEnabled()
                ||minecraft.options.hideGui||minecraft.level==null||minecraft.player==null
                ||!(minecraft.hitResult instanceof net.minecraft.world.phys.BlockHitResult hit)
                ||hit.getType()!=net.minecraft.world.phys.HitResult.Type.BLOCK)return;
        var state=minecraft.level.getBlockState(hit.getBlockPos());
        var lines=new ArrayList<Component>();
        lines.add(Component.literal(net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString()));
        lines.add(Component.literal(state.getBlock().getDescriptionId()));
        for(var entry:state.getValues().entrySet())
            lines.add(Component.literal(entry.getKey().getName()+" = "+entry.getValue()));
        printGeneric(graphics,Component.literal("DEBUG"),0xffff00,0x404000,lines);
    }
    public static void push(Component text,int millis,int id) { INBOX.put(id==0?nextID++:id,new Entry(text.copy(),System.currentTimeMillis(),millis)); }
    public static void push(Component text,int id) { push(text,1000,id); }
    @SubscribeEvent(priority=EventPriority.LOWEST) public static void tick(ClientTickEvent.Post event) {
        MESSAGES.putAll(INBOX);INBOX.clear();long now=System.currentTimeMillis();MESSAGES.values().removeIf(entry->entry.started()+entry.millis()<now);
    }
    @SubscribeEvent(receiveCanceled=true) public static void render(RenderGuiLayerEvent.Pre event) {
        if(!VanillaGuiLayers.CROSSHAIR.equals(event.getName())) return;
        renderBlockStateDebug(event.getGuiGraphics());
        if(MESSAGES.isEmpty()) return;
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
