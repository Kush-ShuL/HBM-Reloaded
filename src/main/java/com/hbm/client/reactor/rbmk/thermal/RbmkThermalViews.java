package com.hbm.client.reactor.rbmk.thermal;
import com.hbm.fluid.*;
import com.hbm.item.fluid.FluidIconItem;
import com.hbm.machine.reactor.rbmk.thermal.RbmkThermalContent;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
/** These views read the same true first Heatable steps, not a second executable recipe table. */
final class RbmkThermalViews{
    record View(ItemStack input,ItemStack output,int heat,double efficiency,boolean cooler){}
    static List<View> recipes(boolean cooler){if(cooler)return List.of(new View(FluidIconItem.make(HbmFluids.PERFLUOROMETHYL_COLD,50),FluidIconItem.make(HbmFluids.PERFLUOROMETHYL,50),200,1,true));var recipes=new ArrayList<View>();for(var type:HbmFluids.getAll()){var trait=type.getTrait(FluidTraits.Heatable.class);if(trait==null)continue;double efficiency=trait.getEfficiency(FluidTraits.HeatingType.HEATEXCHANGER);if(efficiency<=0)continue;var step=trait.getFirstStep();recipes.add(new View(FluidIconItem.make(type,step.amountReq()),FluidIconItem.make(step.typeProduced(),step.amountProduced()),step.heatReq(),efficiency,false));}return recipes;}
    static ItemStack machine(boolean cooler){return new ItemStack((cooler?RbmkThermalContent.COOLER_ITEM:RbmkThermalContent.HEATER_ITEM).get());}
    static Component title(boolean cooler){return Component.translatable(cooler?"block.hbm.rbmk_cooler":"block.hbm.rbmk_heater");}
    static void draw(GuiGraphics graphics,int x,int y,View view){graphics.blit(ResourceLocation.parse("hbm:textures/gui/nei/gui_nei_fluid.png"),x,y,5,11,166,65,256,256);var text=view.cooler()?Component.translatable("gui.hbm.rbmk.cooler.operation"):Component.translatable("gui.hbm.rbmk.heatex.operation",view.heat(),(int)(view.efficiency()*100));var font=Minecraft.getInstance().font;graphics.drawString(font,text,x+(166-font.width(text))/2,y+5,0x404040,false);}
}
