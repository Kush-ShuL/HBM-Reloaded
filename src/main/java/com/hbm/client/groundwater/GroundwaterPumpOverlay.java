package com.hbm.client.groundwater;

import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.fluid.FluidTank;
import com.hbm.machine.groundwater.*;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

/** Original MachinePump ILookOverlay: true core state, three/two resource lines and the source hardcoded-Y70 blink. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class GroundwaterPumpOverlay {
    private static String number(Number value){return String.format(Locale.US,"%,d",value);}
    private static Component fluid(FluidTank tank,boolean input){return Component.literal(input?"-> ":"<- ").withStyle(input?ChatFormatting.GREEN:ChatFormatting.RED)
            .append(tank.getTankType().displayName().copy().withStyle(ChatFormatting.RESET))
            .append(Component.literal(": "+number(tank.getFill())+" / "+number(tank.getMaxFill())+"mB").withStyle(ChatFormatting.RESET));}
    @SubscribeEvent public static void render(RenderGuiEvent.Post event){
        var mc=Minecraft.getInstance();if(mc.level==null||mc.options.hideGui||!(mc.hitResult instanceof BlockHitResult hit))return;
        var state=mc.level.getBlockState(hit.getBlockPos());if(!(state.getBlock() instanceof GroundwaterPumpBlock block))return;
        var pos=block.findCore(mc.level,hit.getBlockPos());if(pos==null||!(mc.level.getBlockEntity(pos) instanceof GroundwaterPumpEntity pump))return;
        var graphics=event.getGuiGraphics();var font=LegacyFonts.get();int x=graphics.guiWidth()/2+8,y=graphics.guiHeight()/2;
        graphics.drawString(font,block.getName(),x+1,y-9,0x404000,false);graphics.drawString(font,block.getName(),x,y-10,0xffff00,false);
        var lines=new ArrayList<Component>();
        if(pump instanceof SteamGroundwaterPump steam){lines.add(fluid(steam.steam,true));lines.add(fluid(steam.lps,false));lines.add(fluid(steam.water,false));}
        if(pump instanceof ElectricGroundwaterPump electric){lines.add(Component.literal("-> ").withStyle(ChatFormatting.GREEN)
                .append(Component.literal(number(electric.getPowerExact())+" / "+number(electric.getMaxPowerExact())+"HE").withStyle(ChatFormatting.RESET)));lines.add(fluid(electric.water,false));}
        for(var line:lines){graphics.drawString(font,line,x,y,0xffffff,true);y+=10;}
        int warning=System.currentTimeMillis()%1000<500?0xff0000:0xffff00;
        if(pos.getY()>70){graphics.drawString(font,Component.translatable("gui.hbm.groundwater.altitude"),x,y,warning,true);y+=10;}
        if(!pump.onGround)graphics.drawString(font,Component.translatable("gui.hbm.groundwater.invalid_ground"),x,y,warning,true);
    }
    private GroundwaterPumpOverlay(){}
}
