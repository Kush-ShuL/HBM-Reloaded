package com.hbm.client.reactor.iter.utilities;
import com.hbm.fluid.*;
import com.hbm.item.fluid.FluidIconItem;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
/** Actual fixed 1mBwater→1mBsuperhotsteam callback, source divisor WATER.Heatable.firstStep.heatReq with no heat efficiency invented. */
final class FusionBoilerViews {
 record View(ItemStack input,ItemStack output,String energy){}
 static List<View> recipes(){var water=HbmFluids.fromName("WATER");return List.of(new View(FluidIconItem.make(water,1),FluidIconItem.make(HbmFluids.fromName("SUPERHOTSTEAM"),1),Integer.toString(water.getTrait(FluidTraits.Heatable.class).getFirstStep().heatReq())));}
 static void draw(GuiGraphics graphics,int x,int y,String energy){var texture=ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png");com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing.recipeBackground(graphics,texture,x,y);graphics.blit(texture,x+47,y+23,5,87,18,18,256,256);graphics.blit(texture,x+101,y+23,5,87,18,18,256,256);graphics.blit(texture,x+74,y+14,59,87,18,36,256,256);graphics.drawString(com.hbm.client.gui.font.LegacyFonts.get(),Component.translatable("gui.hbm.fusion_boiler.heat",energy),x+4,y+53,0x404040,false);}
}
