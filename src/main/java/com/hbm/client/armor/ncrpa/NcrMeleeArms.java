package com.hbm.client.armor.ncrpa;
import com.hbm.client.animation.bus.HbmAnimations;
import static com.hbm.client.weapon.legendary.LegacyGunGL.*;
import net.minecraft.world.item.ItemStack;
/** ArmorNCRPAMelee's original two articulated blade-arm matrices, registered into the existing PA render dispatch. */
public final class NcrMeleeArms {
 public static void render(ItemStack stack){
  texture(NcrArmorRenderer.ARM);glTranslated(0,-1.5,.5);glScaled(.125,.125,.125);
  double[] equip=HbmAnimations.getRelevantTransformation("EQUIP");double right=HbmAnimations.getRelevantTransformation("SWINGRIGHT")[0],left=HbmAnimations.getRelevantTransformation("SWINGLEFT")[0],turn=HbmAnimations.getRelevantTransformation("SWEEPTURN")[0],cut=HbmAnimations.getRelevantTransformation("SWEEPCUT")[0],tilt=60-60*equip[0];
  glPushMatrix();glTranslated(-14*left-4*turn,6*cut,2*left+8*cut);glRotated(tilt+right*40-60*cut,1,0,0);glTranslated(3,0,0);glTranslated(6,8,0);glRotated(90*left,0,0,1);glRotated(60+30*left-90*turn,0,1,0);glTranslated(-6,-8,0);mesh(NcrArmorRenderer.MODEL,"LeftArm");glPopMatrix();
  glPushMatrix();glTranslated(14*right+4*turn,6*cut,2*right+8*cut);glRotated(tilt+left*40-60*cut,1,0,0);glTranslated(-3,0,0);glTranslated(-6,8,0);glRotated(-90*right,0,0,1);glRotated(-60-30*right+90*turn,0,1,0);glTranslated(6,-8,0);mesh(NcrArmorRenderer.MODEL,"RightArm");glPopMatrix();
 }
 private NcrMeleeArms(){}
}
