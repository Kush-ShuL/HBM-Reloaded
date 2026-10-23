package com.hbm.client.generator.leviathan;
import com.hbm.fluid.*;
import com.hbm.item.fluid.FluidIconItem;
import com.hbm.machine.generator.leviathan.*;
import java.math.*;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
/** Actual current Coolable/TURBINE traits withsource.85 efficiency; batch conversion multiplies before finalintegerrounding. */
final class LeviathanViews {
    record View(ItemStack input,ItemStack output,String energy){}
    static List<View> recipes(){var result=new ArrayList<View>();for(var type:HbmFluids.getInNiceOrder()){var trait=type.getTrait(FluidTraits.Coolable.class);if(trait!=null){double efficiency=trait.getEfficiency(FluidTraits.CoolingType.TURBINE)*LeviathanConfig.efficiency;if(efficiency>0){String energy=BigDecimal.valueOf(trait.heatEnergy()).multiply(BigDecimal.valueOf(efficiency)).stripTrailingZeros().toPlainString();result.add(new View(FluidIconItem.make(type,trait.amountReq()),FluidIconItem.make(trait.coolsTo(),trait.amountProduced()),energy));}}}return result;}
    static void draw(GuiGraphics graphics,int x,int y,String energy){var texture=ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png");com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing.recipeBackground(graphics,texture,x,y);graphics.blit(texture,x+47,y+23,5,87,18,18,256,256);graphics.blit(texture,x+101,y+23,5,87,18,18,256,256);graphics.blit(texture,x+74,y+14,59,87,18,36,256,256);graphics.drawString(com.hbm.client.gui.font.LegacyFonts.get(),Component.translatable("gui.hbm.leviathan.energy",energy),x+4,y+53,0x404040,false);}
}
