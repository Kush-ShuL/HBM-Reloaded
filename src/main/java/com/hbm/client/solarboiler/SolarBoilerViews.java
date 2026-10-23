package com.hbm.client.solarboiler;
import com.hbm.client.gui.font.LegacyFonts;import com.hbm.fluid.HbmFluids;import com.hbm.item.fluid.FluidIconItem;import net.minecraft.client.gui.GuiGraphics;import net.minecraft.network.chat.Component;import net.minecraft.resources.ResourceLocation;import net.minecraft.world.item.ItemStack;
/** Information for the actual passive conversion algorithm, not an invented RecipeManager recipe. */
final class SolarBoilerViews {
 record View(ItemStack input,ItemStack output){}
 static View view(){return new View(FluidIconItem.make(HbmFluids.WATER,1),FluidIconItem.make(HbmFluids.fromName("STEAM"),100));}
 static void draw(GuiGraphics g,int x,int y){var texture=ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png");g.blit(texture,x,y,5,11,166,65,256,256);g.blit(texture,x+29,y+23,5,87,18,18,256,256);g.blit(texture,x+101,y+23,5,87,18,18,256,256);g.drawString(LegacyFonts.get(),Component.translatable("gui.hbm.solar_boiler.conversion"),x+4,y+4,0x404040,false);g.drawString(LegacyFonts.get(),Component.translatable("gui.hbm.solar_boiler.source"),x+4,y+52,0x404040,false);}
}
