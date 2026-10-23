package com.hbm.client.weapon.quadro;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.client.weapon.legendary.LegacyGunGL;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
/** Actual rotating unlit cyan source glyphs; caller supplies every authored animation matrix. */
final class QuadroModelText {
    static void draw(String text,int color){var frame=LegacyGunGL.frame();LegacyFonts.get().drawInBatch(text,0,0,0xff000000|color,false,frame.pose().last().pose(),frame.buffers(),Font.DisplayMode.NORMAL,0,LightTexture.FULL_BRIGHT);}
    private QuadroModelText(){}
}
