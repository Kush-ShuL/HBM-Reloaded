package com.hbm.client.thresher;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.machine.thresher.ThresherEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
/** Original look info fallback; native Jade uses the same real server fluid view and suspension field. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class ThresherOverlay {
    @SubscribeEvent public static void render(RenderGuiEvent.Post event){var mc=Minecraft.getInstance();if(mc.options.hideGui||mc.level==null||!(mc.hitResult instanceof BlockHitResult hit)||!(mc.level.getBlockEntity(hit.getBlockPos()) instanceof ThresherEntity machine))return;
        var graphics=event.getGuiGraphics();var font=LegacyFonts.get();int x=graphics.guiWidth()/2+8,y=graphics.guiHeight()/2-10;
        graphics.drawString(font,Component.translatable("block.hbm.machine_thresher"),x,y,0xffff00,true);y+=12;
        graphics.fill(x+1,y+1,x+1+(int)(178D*machine.tank.getFill()/machine.tank.getMaxFill()),y+11,0xa0000000|machine.tank.getTankType().getColor()&0xffffff);
        graphics.drawString(font,machine.tank.getTankType().displayName().copy().append(": "+machine.tank.getFill()+"/"+machine.tank.getMaxFill()+"mB"),x+2,y+1,0xffffff,true);
        if(machine.isSuspended)graphics.drawString(font,Component.literal("! ").append(Component.translatable("tile.machine_thresher.suspended")).append(" !").withStyle(ChatFormatting.RED),x,y+14,0xff0000,true);
    }
    private ThresherOverlay(){}
}
