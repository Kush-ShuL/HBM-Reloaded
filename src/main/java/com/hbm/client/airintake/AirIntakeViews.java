package com.hbm.client.airintake;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.fluid.HbmFluids;
import com.hbm.item.fluid.FluidIconItem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
/** Source-derived informational production display, not a new world recipe or invented ingredient. */
final class AirIntakeViews {
    record View(ItemStack output){}
    static View view(){return new View(FluidIconItem.make(HbmFluids.AIR,1000));}
    static void draw(GuiGraphics graphics,int x,int y){var texture=ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png");graphics.blit(texture,x,y,5,11,166,65,256,256);graphics.blit(texture,x+101,y+23,5,87,18,18,256,256);graphics.blit(texture,x+47,y+23,5,87,18,18,256,256);graphics.drawString(LegacyFonts.get(),Component.translatable("gui.hbm.air_intake.consumption"),x+4,y+4,0x404040,false);graphics.drawString(LegacyFonts.get(),Component.translatable("gui.hbm.air_intake.production"),x+4,y+52,0x404040,false);}
}
