package com.hbm.client.reactor.iter.mhdt;
import com.hbm.fluid.HbmFluids;
import com.hbm.item.fluid.FluidIconItem;
import com.hbm.machine.reactor.iter.mhdt.MhdtConfig;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
/** Source50mB/t coolant plus true TU→HE gain and actual configured minimum for half-output. */
final class MhdtViews {
 record View(ItemStack input,ItemStack output,String energy){}
 static List<View> recipes(){return List.of(new View(FluidIconItem.make(HbmFluids.fromName("PERFLUOROMETHYL_COLD"),50),FluidIconItem.make(HbmFluids.fromName("PERFLUOROMETHYL"),50),Long.toString(MhdtConfig.minimumPlasma)));}
 static void draw(GuiGraphics graphics,int x,int y,String threshold){var texture=ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png");com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing.recipeBackground(graphics,texture,x,y);graphics.blit(texture,x+47,y+23,5,87,18,18,256,256);graphics.blit(texture,x+101,y+23,5,87,18,18,256,256);graphics.blit(texture,x+74,y+14,59,87,18,36,256,256);graphics.drawString(com.hbm.client.gui.font.LegacyFonts.get(),Component.translatable("gui.hbm.fusion_mhdt.formula"),x+4,y+44,0x404040,false);graphics.drawString(com.hbm.client.gui.font.LegacyFonts.get(),Component.translatable("gui.hbm.fusion_mhdt.minimum",threshold),x+4,y+54,0x404040,false);}
}
