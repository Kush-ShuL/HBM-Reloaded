package com.hbm.client.generator.stirling;
import com.hbm.client.gui.font.LegacyFonts;import com.hbm.machine.generator.stirling.*;import net.minecraft.client.gui.GuiGraphics;import net.minecraft.network.chat.Component;import net.minecraft.resources.ResourceLocation;import net.minecraft.world.item.ItemStack;
/** Actual source TU conversion information; no synthetic crafting or fuel recipe. */
final class StirlingViews {
 record View(StirlingKind kind,ItemStack machine){}static java.util.List<View> views(){return java.util.Arrays.stream(StirlingKind.values()).map(kind->new View(kind,new ItemStack(StirlingContent.ITEM.get(kind).get()))).toList();}static void draw(GuiGraphics g,View view,int x,int y){var texture=ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png");g.blit(texture,x,y,5,11,166,65,256,256);g.blit(texture,x+73,y+23,5,87,18,18,256,256);g.drawString(LegacyFonts.get(),Component.translatable("gui.hbm.stirling.source"),x+4,y+4,0x404040,false);g.drawString(LegacyFonts.get(),Component.translatable(view.kind()==StirlingKind.CREATIVE?"gui.hbm.stirling.creative_conversion":"gui.hbm.stirling.conversion"),x+4,y+52,0x404040,false);}
}
