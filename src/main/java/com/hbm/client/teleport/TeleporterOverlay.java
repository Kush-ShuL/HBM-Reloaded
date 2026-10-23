package com.hbm.client.teleport;

import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.machine.teleport.TeleporterEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

/** Source look overlay, localized; retain the user's real-resource bar preference with no empty black backing. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class TeleporterOverlay {
    @SubscribeEvent public static void render(RenderGuiEvent.Post event){
        var mc=Minecraft.getInstance();if(mc.options.hideGui||mc.level==null||!(mc.hitResult instanceof BlockHitResult hit)||!(mc.level.getBlockEntity(hit.getBlockPos()) instanceof TeleporterEntity machine))return;
        var g=event.getGuiGraphics();int x=g.guiWidth()/2+8,y=g.guiHeight()/2-10;
        g.drawString(LegacyFonts.get(),Component.translatable("tile.machine_teleporter.name"),x,y,0xffff00,true);y+=12;
        if(machine.targetY==-1){g.drawString(LegacyFonts.get(),Component.translatable("overlay.hbm.teleporter.no_destination"),x,y,0xff5555,true);return;}
        boolean ready=machine.power.compareTo(TeleporterEntity.CONSUMPTION)>=0;
        int width=(int)(178*Math.max(0,Math.min(1,machine.power.doubleValue()/1_500_000D)));
        if(width>0)g.fill(x+1,y+1,x+1+width,y+11,0xa0000000|(ready?0x55ff55:0xff5555));
        g.drawString(LegacyFonts.get(),Component.literal(String.format(java.util.Locale.US,"%,d / %,d",machine.power,1_500_000)),x+2,y+1,ready?0x55ff55:0xff5555,true);
        g.drawString(LegacyFonts.get(),Component.translatable("overlay.hbm.teleporter.destination",machine.targetX,machine.targetY,machine.targetZ,machine.dimensionLabel()),x,y+14,0xffffff,true);
    }
    private TeleporterOverlay(){}
}
