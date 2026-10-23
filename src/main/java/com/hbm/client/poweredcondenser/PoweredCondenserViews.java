package com.hbm.client.poweredcondenser;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.fluid.HbmFluids;
import com.hbm.item.fluid.FluidIconItem;
import com.hbm.machine.poweredcondenser.PoweredCondenserConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
/** Actual fixed 1:1 conversion and source configured HE/mB; no invented recipe table. */
final class PoweredCondenserViews {
 record View(ItemStack input,ItemStack output){}
 static View view(){return new View(FluidIconItem.make(HbmFluids.SPENTSTEAM,100),FluidIconItem.make(HbmFluids.WATER,100));}
 static void draw(GuiGraphics g,int x,int y){var texture=ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png");com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing.recipeBackground(g,texture,x,y);g.blit(texture,x+29,y+23,5,87,18,18,256,256);g.blit(texture,x+101,y+23,5,87,18,18,256,256);g.drawString(LegacyFonts.get(),Component.translatable("gui.hbm.powered_condenser.conversion"),x+4,y+4,0x404040,false);g.drawString(LegacyFonts.get(),Component.translatable("gui.hbm.powered_condenser.energy",PoweredCondenserConfig.powerConsumption),x+4,y+52,0x404040,false);}
}
