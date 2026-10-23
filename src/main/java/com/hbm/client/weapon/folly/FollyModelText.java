package com.hbm.client.weapon.folly;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.client.weapon.legendary.LegacyGunGL;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
/** Original unlit orange model-screen text, using the user's sole actual native/ModernUI font. */
final class FollyModelText {
    static void draw(String text,int color){var frame=LegacyGunGL.frame();LegacyFonts.get().drawInBatch(text,0,0,0xff000000|color,false,frame.pose().last().pose(),frame.buffers(),Font.DisplayMode.NORMAL,0,LightTexture.FULL_BRIGHT);}
    private FollyModelText(){}
}
