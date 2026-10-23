package com.hbm.client.woodprocessing;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.machine.woodprocessing.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
/** Source live look information, requested real bars in place of the original typed progress marks. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class WoodProcessingOverlay {
    @SubscribeEvent public static void render(RenderGuiEvent.Post event){var mc=Minecraft.getInstance();if(mc.options.hideGui||mc.level==null||!(mc.hitResult instanceof BlockHitResult hit))return;var block=mc.level.getBlockState(hit.getBlockPos()).getBlock();var g=event.getGuiGraphics();int x=g.guiWidth()/2+8,y=g.guiHeight()/2-10;var font=LegacyFonts.get();
        if(block instanceof SawmillBlock sawBlock){var saw=sawBlock.core(mc.level,hit.getBlockPos());if(saw==null)return;{g.drawString(font,Component.translatable("block.hbm.machine_sawmill"),x,y,0xffff00,true);y+=12;bar(g,x,y,saw.displayHeat/300D,0xff9900);g.drawString(font,saw.displayHeat+"TU/t",x+2,y+1,0xffffff,true);y+=14;bar(g,x,y,saw.progress/600D,0x55ff55);y+=14;for(int i=0;i<3;i++)if(!saw.slots.get(i).isEmpty()){var stack=saw.slots.get(i);g.drawString(font,Component.literal(i==0?"§a-> §r":"§c<- §r").append(stack.getHoverName()).append(stack.getCount()>1?" x"+stack.getCount():""),x,y,0xffffff,true);y+=12;}}
            if(saw.displayHeat>300){g.drawString(font,Component.translatable("gui.hbm.woodprocessing.overspeed"),x,y,System.currentTimeMillis()%1000<500?0xff0000:0xffff00,true);y+=12;}if(!saw.hasBlade)g.drawString(font,Component.translatable("gui.hbm.woodprocessing.blade_missing"),x,y,0xff0000,true);
        }else if(mc.level.getBlockEntity(hit.getBlockPos()) instanceof AutosawEntity saw){{g.drawString(font,Component.translatable("block.hbm.machine_autosaw"),x,y,0xffff00,true);y+=12;bar(g,x,y,(double)saw.tank.getFill()/saw.tank.getMaxFill(),saw.tank.getTankType().getColor());g.drawString(font,saw.tank.getTankType().displayName().copy().append(": "+saw.tank.getFill()+"/"+saw.tank.getMaxFill()+"mB"),x+2,y+1,0xffffff,true);y+=14;}if(saw.isSuspended)g.drawString(font,Component.literal("! ").append(Component.translatable("tile.machine_autosaw.suspended")).append(" !"),x,y,0xff0000,true);}
    }
    private static void bar(GuiGraphics g,int x,int y,double value,int color){g.fill(x+1,y+1,x+1+(int)(178*Math.max(0,Math.min(1,value))),y+11,0xa0000000|color&0xffffff);}
}
