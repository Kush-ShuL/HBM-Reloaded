package com.hbm.client.hephaestus;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.machine.hephaestus.*;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
/** Actual no-GUI ILookOverlay, using the existing clear-background resource-bar convention. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class HephaestusOverlay {
    @SubscribeEvent public static void render(RenderGuiEvent.Post event){
        var mc=Minecraft.getInstance();
        if(mc.options.hideGui||mc.level==null||!(mc.hitResult instanceof BlockHitResult hit)||!(mc.level.getBlockState(hit.getBlockPos()).getBlock() instanceof HephaestusBlock block))return;
        var pos=block.findCore(mc.level,hit.getBlockPos());
        if(pos==null||!(mc.level.getBlockEntity(pos) instanceof HephaestusEntity machine))return;
        var g=event.getGuiGraphics();var font=LegacyFonts.get();int x=g.guiWidth()/2+8,y=g.guiHeight()/2-10;
        g.drawString(font,Component.translatable("block.hbm.machine_hephaestus"),x,y,0xffff00,true);y+=12;
        g.drawString(font,Component.translatable("gui.hbm.hephaestus.heat",String.format(Locale.US,"%,d",machine.bufferedHeat)),x,y,0xffffff,true);y+=14;
        var tanks=machine.getAllTanks();
        for(int i=0;i<tanks.length;i++){
            var tank=tanks[i];double ratio=(double)tank.getFill()/tank.getMaxFill();
            g.fill(x+1,y+1,x+1+(int)(178*Math.max(0,Math.min(1,ratio))),y+11,0xa0000000|tank.getTankType().getColor()&0xffffff);
            var line=Component.literal(i==0?"§a-> §r":"§c<- §r").append(tank.getTankType().displayName()).append(String.format(Locale.US,": %,d/%,dmB",tank.getFill(),tank.getMaxFill()));
            g.drawString(font,line,x+2,y+1,0xffffff,true);y+=14;
        }
    }
}
