package com.hbm.client.weapon.missilelauncher;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.client.weapon.legendary.LegacyGunGL;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
/** The original red AUTO label is unlit and depth-tested, at the caller's exact authored matrix. */
final class MissileLauncherModelText {
    static void draw(String text,int color){var frame=LegacyGunGL.frame();LegacyFonts.get().drawInBatch(text,0,0,0xff000000|color,false,frame.pose().last().pose(),frame.buffers(),Font.DisplayMode.NORMAL,0,LightTexture.FULL_BRIGHT);}
    private MissileLauncherModelText(){}
}
