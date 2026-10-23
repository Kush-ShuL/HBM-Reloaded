package com.hbm.client.hephaestus;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.fluid.*;
import com.hbm.item.fluid.FluidIconItem;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
/** Query the same real first-step Heatable traits as the machine; no parallel processing or fuel table. */
final class HephaestusViews {
    record View(ItemStack input,ItemStack output,int heat){}
    static List<View> views(){
        var result=new ArrayList<View>();
        for(var fluid:HbmFluids.getAll()){
            var trait=fluid.getTrait(FluidTraits.Heatable.class);
            if(trait!=null&&trait.getEfficiency(FluidTraits.HeatingType.HEATEXCHANGER)>0){
                var step=trait.getFirstStep();
                result.add(new View(FluidIconItem.make(fluid,step.amountReq()),FluidIconItem.make(step.typeProduced(),step.amountProduced()),step.heatReq()));
            }
        }
        return List.copyOf(result);
    }
    static void draw(GuiGraphics g,View view,int x,int y){
        var texture=ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png");
        com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing.recipeBackground(g,texture,x,y);
        g.blit(texture,x+29,y+23,5,87,18,18,256,256);g.blit(texture,x+101,y+23,5,87,18,18,256,256);
        g.drawString(LegacyFonts.get(),Component.translatable("gui.hbm.hephaestus.conversion"),x+4,y+4,0x404040,false);
        g.drawString(LegacyFonts.get(),Component.translatable("gui.hbm.hephaestus.operation",view.heat()),x+4,y+52,0x404040,false);
        g.drawString(LegacyFonts.get(),Component.translatable("gui.hbm.hephaestus.environment"),x+4,y+65,0x404040,false);
    }
}
