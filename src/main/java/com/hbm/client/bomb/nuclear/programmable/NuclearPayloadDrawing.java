package com.hbm.client.bomb.nuclear.programmable;
import com.hbm.bomb.nuclear.programmable.NuclearPayloadDisplays;
import com.hbm.client.gui.font.LegacyFonts;
import net.minecraft.client.gui.GuiGraphics;
/** One shared localized formula view for JEI and REI. */
public final class NuclearPayloadDrawing {
 public static void draw(GuiGraphics graphics,NuclearPayloadDisplays.Display display,int x,int y){var font=LegacyFonts.get();int row=y;for(var line:display.lines()){for(var wrapped:font.split(line,132)){graphics.drawString(font,wrapped,x+22,row,0x404040,false);row+=10;}row+=2;}}
 private NuclearPayloadDrawing(){}
}
