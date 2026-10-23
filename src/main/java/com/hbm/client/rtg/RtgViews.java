package com.hbm.client.rtg;

import com.hbm.machine.rtg.*;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

final class RtgViews {
    record Decay(ItemStack pellet,ItemStack depleted){}
    static List<Decay> recipes(){return RtgConfig.decays()?RtgContent.PELLETS.stream().map(holder->new Decay(new ItemStack(holder.get()),holder.get().decayItem())).toList():List.of();}
    static void draw(GuiGraphics graphics,int x,int y){var texture=ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png");graphics.blit(texture,x+47,y+23,5,87,18,18,256,256);graphics.blit(texture,x+101,y+23,5,87,18,18,256,256);graphics.blit(texture,x+74,y+14,59,87,18,36,256,256);}
    private RtgViews(){}
}
