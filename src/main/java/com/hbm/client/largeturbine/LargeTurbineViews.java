package com.hbm.client.largeturbine;
import com.hbm.fluid.*;
import com.hbm.item.fluid.FluidIconItem;
import com.hbm.machine.largeturbine.LargeTurbineEntity;
import java.math.BigDecimal;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
/** Views of the actual Coolable trait operations; no second processing table or invented recipes. */
final class LargeTurbineViews {
 record View(ItemStack input,ItemStack output,String energy){}
 static List<View> recipes(){var result=new ArrayList<View>();for(var type:HbmFluids.getInNiceOrder()){var trait=type.getTrait(FluidTraits.Coolable.class);if(trait==null)continue;double efficiency=trait.getEfficiency(FluidTraits.CoolingType.TURBINE)*LargeTurbineEntity.efficiency;if(efficiency>0){String energy=BigDecimal.valueOf(trait.heatEnergy()*efficiency).stripTrailingZeros().toPlainString();result.add(new View(FluidIconItem.make(type,trait.amountReq()),FluidIconItem.make(trait.coolsTo(),trait.amountProduced()),energy));}}return result;}
 static void draw(GuiGraphics g,int x,int y,String energy){var texture=ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png");com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing.recipeBackground(g,texture,x,y);g.blit(texture,x+47,y+23,5,87,18,18,256,256);g.blit(texture,x+101,y+23,5,87,18,18,256,256);g.blit(texture,x+74,y+14,59,87,18,36,256,256);g.drawString(Minecraft.getInstance().font,Component.translatable("gui.hbm.large_turbine.energy",energy),x+4,y+53,0x404040,false);}
}
