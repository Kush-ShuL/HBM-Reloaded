package com.hbm.client.weapon.heavypowder;
import com.hbm.client.weapon.legendary.LegacyGunGL;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.item.ItemStack;
/** Actual source Bolter's red, fullbright on-model magazine count, retaining the original authored matrix and centering. */
final class HeavyPowderDisplay {
 static void counter(ItemStack stack){var frame=LegacyGunGL.frame();var poses=frame.pose();var font=Minecraft.getInstance().font;var gun=(ItemGunBaseNT)stack.getItem();String text=gun.getConfig(stack,0).getReceivers(stack)[0].getMagazine(stack).getAmount(stack,null)+"";
  poses.pushPose();try{poses.translate(.025F-(font.width(text)/2)*.04F,2.11F,2.91F);poses.scale(.04F,-.04F,.04F);poses.mulPose(Axis.XP.rotationDegrees(45));font.drawInBatch(text,0,0,0xffff0000,false,poses.last().pose(),frame.buffers(),Font.DisplayMode.NORMAL,0,LightTexture.FULL_BRIGHT);}finally{poses.popPose();}
 }
 private HeavyPowderDisplay(){}
}
