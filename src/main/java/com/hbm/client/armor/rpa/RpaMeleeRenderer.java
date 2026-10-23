package com.hbm.client.armor.rpa;
import com.hbm.armor.rpa.*;
import com.hbm.client.animation.bus.HbmAnimations;
import com.hbm.client.weapon.legendary.*;
import static com.hbm.client.weapon.legendary.LegacyGunGL.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
/** Original ItemRenderPAMelee caller + ArmorRPAMelee two-arm matrix. No gun model substituted for powered fists. */
public final class RpaMeleeRenderer extends LegacyGunRenderer {
 public interface ComponentRenderer {default void setup(ItemStack stack){}void render(ItemStack stack);}
 private static final java.util.Map<PowerArmorMelee,ComponentRenderer> PROVIDERS=new java.util.IdentityHashMap<>();
 public static void register(PowerArmorMelee component,ComponentRenderer renderer){PROVIDERS.put(component,renderer);}
 public static void registerRpa(){register(RpaMelee.INSTANCE,RpaMeleeRenderer::renderRpaArms);}
 private ComponentRenderer component(){return PROVIDERS.get(PAWeaponsProvider.melee(Minecraft.getInstance().player));}
 @Override public boolean isAkimbo(LivingEntity entity){return true;}
 @Override public float swayMagnitude(ItemStack stack){return 2F;}@Override public float swayPeriod(ItemStack stack){return .5F;}
 @Override public void setupFirstPerson(ItemStack stack){var renderer=component();if(renderer!=null)renderer.setup(stack);}
 @Override public void renderFirstPerson(ItemStack stack){var renderer=component();if(renderer!=null)renderer.render(stack);}
 @Override public void setupThirdPerson(ItemStack stack){}@Override public void setupThirdPersonAkimbo(ItemStack stack){}
 @Override public void setupInv(ItemStack stack){glAlphaFunc(GL_GREATER,0);glEnable(GL_ALPHA_TEST);glScaled(1,1,-1);glTranslated(8,8,0);glScaled(2.5,2.5,2.5);}
 @Override public void setupModTable(ItemStack stack){glScaled(-12.5,-12.5,-12.5);glRotated(90,0,1,0);glTranslated(0,-.5,1);}
 @Override public void renderInv(ItemStack stack){glEnable(GL_LIGHTING);glShadeModel(GL_SMOOTH);texture(RpaContent.id("textures/armor/ncrpa_arm.png"));glPushMatrix();glScaled(.3125,.3125,.3125);glRotated(135,0,0,1);glRotated(135,0,1,0);glTranslated(0,-5.5,0);glTranslated(-3.5,0,0);mesh(RpaContent.id("models/armor/ncrpa.obj"),"Leftarm");glTranslated(7,1,-1);mesh(RpaContent.id("models/armor/ncrpa.obj"),"RightArm");glPopMatrix();glShadeModel(GL_FLAT);}
 @Override public void renderOther(ItemStack stack,ItemRenderType type,Object...data){if(type==ItemRenderType.EQUIPPED)return;glEnable(GL_LIGHTING);glShadeModel(GL_SMOOTH);texture(RpaContent.id("textures/armor/ncrpa_arm.png"));glPushMatrix();glScaled(.3125,.3125,.3125);glRotated(90,1,0,0);glTranslated(0,-5.5,0);glTranslated(-2,0,0);mesh(RpaContent.id("models/armor/ncrpa.obj"),"Leftarm");glTranslated(4,0,0);mesh(RpaContent.id("models/armor/ncrpa.obj"),"RightArm");glPopMatrix();glShadeModel(GL_FLAT);}
 private static void renderRpaArms(ItemStack stack){texture(RpaArmorRenderer.ARM);glTranslated(0,-1.5,.5);glScaled(.125,.125,.125);double[] equip=HbmAnimations.getRelevantTransformation("EQUIP");double right=HbmAnimations.getRelevantTransformation("SWINGRIGHT")[0],left=HbmAnimations.getRelevantTransformation("SWINGLEFT")[0],turn=HbmAnimations.getRelevantTransformation("SLAPTURN")[0],slap=HbmAnimations.getRelevantTransformation("SLAP")[0],tilt=60-60*equip[0];
  glPushMatrix();glTranslated(-12*left+2*turn-5*slap,6*slap,5*left+8*slap);glRotated(tilt-right*20,1,0,0);glTranslated(3,0,0);glTranslated(6,8,0);glRotated(60*left+45*slap,0,0,1);glRotated(60+15*left+45*turn,0,1,0);glTranslated(-6,-8,0);mesh(RpaArmorRenderer.MODEL,"LeftArm");glPopMatrix();
  glPushMatrix();glTranslated(12*right-2*turn+5*slap,6*slap,5*right+8*slap);glRotated(tilt-left*20,1,0,0);glTranslated(-3,0,0);glTranslated(-6,8,0);glRotated(-60*right-45*slap,0,0,1);glRotated(-60-15*right-45*turn,0,1,0);glTranslated(6,-8,0);mesh(RpaArmorRenderer.MODEL,"RightArm");glPopMatrix();
 }
}
