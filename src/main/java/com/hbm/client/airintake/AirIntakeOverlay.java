package com.hbm.client.airintake;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.machine.airintake.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
/** Original no-GUI look information, expressed with the user's requested resource bars; Jade provides the same live state. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class AirIntakeOverlay {
    @SubscribeEvent public static void render(RenderGuiEvent.Post event){var mc=Minecraft.getInstance();if(mc.options.hideGui||mc.level==null||!(mc.hitResult instanceof BlockHitResult hit)||!(mc.level.getBlockState(hit.getBlockPos()).getBlock() instanceof AirIntakeBlock block))return;var core=block.findCore(mc.level,hit.getBlockPos());if(core==null||!(mc.level.getBlockEntity(core) instanceof AirIntakeBlockEntity machine))return;var graphics=event.getGuiGraphics();int x=graphics.guiWidth()/2+8,y=graphics.guiHeight()/2-10;graphics.drawString(LegacyFonts.get(),Component.translatable("block.hbm.machine_intake"),x,y,0xffff00,true);y+=12;bar(graphics,x,y,180,machine.getPowerExact().doubleValue()/2000,machine.running()?0x55ff55:0xff5555);graphics.drawString(LegacyFonts.get(),Component.translatable("gui.hbm.air_intake.energy",machine.getPowerExact().toString(),"2000"),x+2,y+1,0xffffff,true);y+=14;bar(graphics,x,y,180,(double)machine.compair.getFill()/machine.compair.getMaxFill(),machine.compair.getTankType().getColor());graphics.drawString(LegacyFonts.get(),machine.compair.getTankType().displayName().copy().append(" "+machine.compair.getFill()+"/"+machine.compair.getMaxFill()+"mB"),x+2,y+1,0xffffff,true);}
    private static void bar(GuiGraphics graphics,int x,int y,int width,double value,int color){graphics.fill(x+1,y+1,x+1+(int)((width-2)*Math.max(0,Math.min(1,value))),y+11,0xa0000000|color&0xffffff);}
}
