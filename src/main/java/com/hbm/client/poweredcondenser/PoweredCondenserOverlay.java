package com.hbm.client.poweredcondenser;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.item.battery.BatteryItem;
import com.hbm.machine.poweredcondenser.*;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
/** Source no-GUI look overlay; existing user-requested live resource bars and no opaque panel. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class PoweredCondenserOverlay {
 @SubscribeEvent public static void render(RenderGuiEvent.Post e){if(!com.hbm.item.settings.client.LegacyInfoSystem.lookOverlayEnabled())return;var mc=Minecraft.getInstance();if(mc.options.hideGui||mc.level==null||!(mc.hitResult instanceof BlockHitResult hit)||!(mc.level.getBlockState(hit.getBlockPos()).getBlock() instanceof PoweredCondenserBlock block))return;var p=block.findCore(mc.level,hit.getBlockPos());if(p==null||!(mc.level.getBlockEntity(p) instanceof PoweredCondenserEntity c))return;var g=e.getGuiGraphics();var font=LegacyFonts.get();int x=g.guiWidth()/2+8,y=g.guiHeight()/2-10;g.drawString(font,Component.translatable("block.hbm.machine_condenser_powered"),x,y,0xffff00,true);y+=12;bar(g,x,y,(double)c.power/c.getMaxPower(),0xffff00);g.drawString(font,Component.translatable("tooltip.hbm.battery.energy_stored",BatteryItem.shortNumber(c.power),BatteryItem.shortNumber(c.getMaxPower())),x+2,y+1,0xffffff,true);y+=14;for(int i=0;i<2;i++){var tank=c.tanks[i];bar(g,x,y,(double)tank.getFill()/tank.getMaxFill(),tank.getTankType().getColor());var line=Component.literal(i==0?"§a-> §r":"§c<- §r").append(tank.getTankType().displayName()).append(String.format(Locale.US,": %,d/%,dmB",tank.getFill(),tank.getMaxFill()));g.drawString(font,line,x+2,y+1,0xffffff,true);y+=14;}}
 private static void bar(net.minecraft.client.gui.GuiGraphics g,int x,int y,double ratio,int color){g.fill(x+1,y+1,x+1+(int)(178*Math.max(0,Math.min(1,ratio))),y+11,0xa0000000|color&0xffffff);}
}
