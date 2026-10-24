package com.hbm.client.massstorage;

import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.machine.massstorage.MassStorageBlockEntity;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

/** Source ILookOverlay title/shadow, comma-separated amount, hundredth-percent and red-to-green color. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class MassStorageOverlay {
    @SubscribeEvent public static void render(RenderGuiLayerEvent.Pre event){if(!com.hbm.item.settings.client.LegacyInfoSystem.lookOverlayEnabled())return;
        if(!event.getName().equals(VanillaGuiLayers.CROSSHAIR))return;var client=Minecraft.getInstance();if(client.options.hideGui||client.level==null||!(client.hitResult instanceof BlockHitResult hit)||!(client.level.getBlockEntity(hit.getBlockPos()) instanceof MassStorageBlockEntity storage))return;
        var graphics=event.getGuiGraphics();var font=LegacyFonts.get();boolean full=!storage.displayType.isEmpty();var title=full?storage.displayType.getHoverName():Component.translatable("hud.hbm.mass_storage.empty");int x=graphics.guiWidth()/2+8,y=graphics.guiHeight()/2;
        graphics.drawString(font,title,x+1,y-9,full?0x404000:0x004040,false);graphics.drawString(font,title,x,y-10,full?0xffff00:0x00ffff,false);
        if(full){graphics.drawString(font,String.format(Locale.US,"%,d / %,d",storage.getStockpile(),storage.getCapacity()),x,y,0xffffff,true);double fraction=(double)storage.getStockpile()/storage.getCapacity();int color=(int)(255-255*fraction)<<16|(int)(255*fraction)<<8;graphics.drawString(font,((int)Math.floor(fraction*10_000)/100D)+"%",x,y+10,color,true);}
    }
    private MassStorageOverlay(){}
}
