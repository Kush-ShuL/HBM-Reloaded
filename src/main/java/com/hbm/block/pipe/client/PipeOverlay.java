package com.hbm.block.pipe.client;

import com.hbm.HbmReloaded;
import com.hbm.block.pipe.NativePipeBlockEntity;
import com.hbm.block.pipe.InstrumentPipeBlockEntity;
import com.hbm.block.pipe.PipeKind;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.item.tool.WrenchItem;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

/** Source ILookOverlay offsets, title colors, instrument lines and default wrench info placement. */
@EventBusSubscriber(modid = HbmReloaded.MOD_ID, value = Dist.CLIENT)
public final class PipeOverlay {
    private PipeOverlay() {}
    @SubscribeEvent public static void render(RenderGuiEvent.Post event) {
        var minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui || minecraft.level == null || minecraft.player == null) return;
        var graphics=event.getGuiGraphics(); var font=LegacyFonts.get();
        var held=minecraft.player.getMainHandItem();
        var pos=WrenchItem.start(held);
        if (held.getItem() instanceof WrenchItem && pos != null) {
            int distance=(int)minecraft.player.position().distanceTo(new net.minecraft.world.phys.Vec3(pos.getX(),pos.getY(),pos.getZ()));
            Component text=held.getHoverName().copy().append(": "+distance+"m");
            graphics.fill(10,10,20+font.width(text),27,0x80404040); graphics.drawString(font,text,15,15,0xFFFFFFFF,false);
        }
        if (!(minecraft.hitResult instanceof BlockHitResult hit) || !(minecraft.level.getBlockEntity(hit.getBlockPos()) instanceof NativePipeBlockEntity pipe)) return;
        int x=graphics.guiWidth()/2+8,y=graphics.guiHeight()/2;
        var title=pipe.getBlockState().getBlock().getName();
        graphics.drawString(font,title,x+1,y-9,0x404000,false); graphics.drawString(font,title,x,y-10,0xFFFF00,false);
        for (var type:pipe.networkTypes()) { graphics.drawString(font,type.displayName(),x,y,pipe.kind().exhaust()?0xFFFFFF:type.getColor(),true); y+=10; }
        if (pipe instanceof InstrumentPipeBlockEntity instrument) {
            if (pipe.kind()==PipeKind.GAUGE) {
                graphics.drawString(font,String.format(java.util.Locale.US,"%,d mB/t",instrument.deltaTick()),x,y,0xFFFFFF,true);
                graphics.drawString(font,String.format(java.util.Locale.US,"%,d mB/s",instrument.deltaLastSecond()),x,y+10,0xFFFFFF,true);
            } else graphics.drawString(font,"Counter: "+instrument.counter(),x,y,0xFFFFFF,true);
        }
    }
}
