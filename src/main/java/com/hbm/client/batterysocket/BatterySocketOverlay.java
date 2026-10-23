package com.hbm.client.batterysocket;

import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.item.battery.BatteryItem;
import com.hbm.machine.batterysocket.BatterySocketBlock;
import com.hbm.machine.batterysocket.BatterySocketBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class BatterySocketOverlay {
    private BatterySocketOverlay(){}
    @SubscribeEvent public static void render(RenderGuiEvent.Post event){
        var mc=Minecraft.getInstance();if(mc.options.hideGui||mc.level==null||!(mc.hitResult instanceof BlockHitResult hit)||!(mc.level.getBlockState(hit.getBlockPos()).getBlock() instanceof BatterySocketBlock block))return;
        var pos=block.findCore(mc.level,hit.getBlockPos());if(pos==null||!(mc.level.getBlockEntity(pos) instanceof BatterySocketBlockEntity socket)||socket.displayedStack().isEmpty())return;
        var graphics=event.getGuiGraphics();var font=LegacyFonts.get();int x=graphics.guiWidth()/2+8,y=graphics.guiHeight()/2;var title=socket.displayedStack().getHoverName();
        graphics.drawString(font,title,x+1,y-9,0x404000,false);graphics.drawString(font,title,x,y-10,0xFFFF00,false);
        graphics.drawString(font,BatteryItem.shortNumber(socket.displayedPower())+" / "+BatteryItem.shortNumber(socket.displayedMaxPower())+"HE",x,y,0xFFFFFF,true);
        double percent=(double)socket.displayedPower()/socket.displayedMaxPower();int charge=(int)Math.floor(percent*10000D),color=((int)(255-255*percent))<<16|((int)(255*percent)<<8);
        graphics.drawString(font,charge/100D+"%",x,y+10,color,true);
    }
}
