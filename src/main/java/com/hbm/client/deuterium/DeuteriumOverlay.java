package com.hbm.client.deuterium;

import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.item.battery.BatteryItem;
import com.hbm.machine.deuterium.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

/** Original ILookOverlay, independent of the optional Jade plugin and without a fabricated GUI/progress bar. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class DeuteriumOverlay {
    @SubscribeEvent public static void render(RenderGuiEvent.Post event){
        var mc=Minecraft.getInstance();if(mc.level==null||mc.options.hideGui||!(mc.hitResult instanceof BlockHitResult hit))return;
        var state=mc.level.getBlockState(hit.getBlockPos());var pos=hit.getBlockPos();
        if(state.getBlock() instanceof DeuteriumTowerBlock block){pos=block.findCore(mc.level,pos);if(pos==null)return;}
        if(!(mc.level.getBlockEntity(pos) instanceof DeuteriumEntity machine))return;
        var g=event.getGuiGraphics();var font=LegacyFonts.get();int x=g.guiWidth()/2+8,y=g.guiHeight()/2;
        var title=state.getBlock().getName();g.drawString(font,title,x+1,y-9,0x404000,false);g.drawString(font,title,x,y-10,0xffff00,false);
        var energy=machine.getPowerExact();String number=energy.bitLength()<64?BatteryItem.shortNumber(energy.longValueExact()):energy.toString();
        g.drawString(font,Component.translatable("gui.hbm.deuterium.power",number).withStyle(machine.hasPower()?ChatFormatting.GREEN:ChatFormatting.RED),x,y,0xffffff,true);y+=10;
        for(int i=0;i<machine.tanks.length;i++){var tank=machine.tanks[i];var line=Component.literal(i==0?"-> ":"<- ").withStyle(i==0?ChatFormatting.GREEN:ChatFormatting.RED).append(tank.getTankType().displayName().copy().withStyle(ChatFormatting.RESET)).append(Component.literal(": "+tank.getFill()+"/"+tank.getMaxFill()+"mB").withStyle(ChatFormatting.RESET));g.drawString(font,line,x,y,0xffffff,true);y+=10;}
    }
}
