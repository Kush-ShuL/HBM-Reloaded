package com.hbm.client.fan;

import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.energy.wire.PylonConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

/** Source ID_FAN_MODE single-entry lifetime/fade/panel and existing shared source INFO_POSITION/offset settings. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class FanModeOverlay {
    private static final String[] KEYS={"tile.fan.falloffOn","tile.fan.falloffOff","tile.fan.suckOn","tile.fan.suckOff"};
    private static Component current;
    private static long start;
    public static void show(int mode) { current=Component.translatable(KEYS[mode]).withStyle(ChatFormatting.GOLD);start=System.currentTimeMillis(); }
    @SubscribeEvent(receiveCanceled=true) public static void render(RenderGuiLayerEvent.Pre event) {
        var mc=Minecraft.getInstance();long elapsed=System.currentTimeMillis()-start;
        if(!event.getName().equals(VanillaGuiLayers.CROSSHAIR)||mc.level==null||mc.player==null||mc.options.hideGui||current==null||elapsed>1000)return;
        var graphics=event.getGuiGraphics();var font=LegacyFonts.get();int width=font.width(current),mode=PylonConfig.INFO_POSITION.get();
        int x=mode==0?15:mode==1?graphics.guiWidth()-width-15:mode==2?graphics.guiWidth()/2+7:graphics.guiWidth()/2-width-6;
        int y=mode==0||mode==1?15:graphics.guiHeight()/2+7;x+=PylonConfig.INFO_OFFSET_HORIZONTAL.get();y+=PylonConfig.INFO_OFFSET_VERTICAL.get();
        int alpha=Math.max(Math.min((int)(510*(1000-elapsed)/1000),255),5);
        graphics.fill(x-5,y-5,x+width+5,y+12,0x80404040);graphics.drawString(font,current,x,y,(alpha<<24)|0xffffff,false);
    }
}
