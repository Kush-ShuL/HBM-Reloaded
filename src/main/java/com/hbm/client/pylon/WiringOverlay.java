package com.hbm.client.pylon;

import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.energy.wire.WiringItem;
import com.hbm.energy.wire.PylonConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/** Original inventory-wide ID_CABLE tooltip: last matching main-inventory slot wins. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class WiringOverlay {
    private static net.minecraft.network.chat.MutableComponent current;
    private static long updated;
    private WiringOverlay() {}
    @SubscribeEvent public static void tick(ClientTickEvent.Post event) {
        var minecraft=Minecraft.getInstance();if(minecraft.level==null||minecraft.player==null||minecraft.isPaused()) return;
        net.minecraft.network.chat.MutableComponent text=null;
        for(var stack:minecraft.player.getInventory().items) if(stack.getItem() instanceof WiringItem) {
            var start=WiringItem.start(stack);if(start==null) continue;
            int distance=(int)minecraft.player.position().distanceTo(new Vec3(start.getX(),start.getY(),start.getZ()));
            text=stack.getHoverName().copy().append(": "+distance+"m");
        }
        if(text!=null) { current=text;updated=System.currentTimeMillis(); }
    }
    @SubscribeEvent public static void render(RenderGuiEvent.Post event) {
        var minecraft=Minecraft.getInstance();long elapsed=System.currentTimeMillis()-updated;
        if(minecraft.options.hideGui||minecraft.level==null||minecraft.player==null||current==null||elapsed>1000) return;
        var graphics=event.getGuiGraphics();var font=LegacyFonts.get();int width=font.width(current),mode=PylonConfig.INFO_POSITION.get();
        int x=mode==0?15:mode==1?graphics.guiWidth()-width-15:mode==2?graphics.guiWidth()/2+7:graphics.guiWidth()/2-width-6;
        int y=mode==0||mode==1?15:graphics.guiHeight()/2+7;x+=PylonConfig.INFO_OFFSET_HORIZONTAL.get();y+=PylonConfig.INFO_OFFSET_VERTICAL.get();
        int alpha=Math.max(Math.min((int)(510*(1000-elapsed)/1000),255),5);
        graphics.fill(x-5,y-5,x+width+5,y+12,0x80404040);graphics.drawString(font,current,x,y,(alpha<<24)|0xFFFFFF,false);
    }
}
