package com.hbm.client.weapon.fireext;
import com.hbm.client.model.LegacyItemTransforms;
import com.hbm.client.weapon.legendary.LegacyGunGL;
import com.hbm.client.weapon.pepperbox.GunHandTransforms;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import com.hbm.weapon.sedna.gun.fireext.FireExtBullets;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
/** Source standalone IItemRenderer, deliberately using native hand rendering instead of ItemRenderWeaponBase's camera. */
public final class FireExtItemRenderer extends BlockEntityWithoutLevelRenderer {
 private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/weapons/fireext.obj");
 private static final ResourceLocation WATER=texture("fireext_normal"),FOAM=texture("fireext_foam"),SAND=texture("fireext_sand");
 private static ResourceLocation texture(String name){return ResourceLocation.parse("hbm:textures/models/weapons/"+name+".png");}
 public FireExtItemRenderer(){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());}
 @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
  poses.pushPose();try(var hand=GunHandTransforms.held(poses,context);var scope=LegacyGunGL.push(poses,buffers,light,overlay)){
   var gun=(ItemGunBaseNT)stack.getItem();var magazine=gun.getConfig(stack,0).getReceivers(stack)[0].getMagazine(stack);var type=magazine.getType(stack,null);
   LegacyGunGL.texture(type==FireExtBullets.FOAM?FOAM:type==FireExtBullets.SAND?SAND:WATER);
   if(context.firstPerson()){
    LegacyItemTransforms.held(poses,context);
    LegacyGunGL.glRotated(25,0,0,1);LegacyGunGL.glTranslated(.5,-.5,-.5);LegacyGunGL.glRotated(80,0,1,0);LegacyGunGL.glScaled(.35,.35,.35);
   }else if(context==ItemDisplayContext.THIRD_PERSON_LEFT_HAND||context==ItemDisplayContext.THIRD_PERSON_RIGHT_HAND){
    LegacyGunGL.glScaled(.5,.5,.5);LegacyGunGL.glRotatef(20F,0,0,1);LegacyGunGL.glRotatef(-5,0,1,1);LegacyGunGL.glRotatef(10,0,1,0);LegacyGunGL.glRotatef(15F,1,0,0);LegacyGunGL.glTranslatef(.75F,-2.75F,.5F);
   }else if(context==ItemDisplayContext.GUI){
    // Native item projection uses block units; original INVENTORY matrices below were authored in sixteen pixels.
    poses.translate(0,1,.3125);poses.scale(1F/16,-1F/16,1F/16);
    LegacyGunGL.glTranslated(2,14,0);LegacyGunGL.glRotated(-90,0,1,0);LegacyGunGL.glRotated(-135,1,0,0);LegacyGunGL.glRotated(System.currentTimeMillis()/10%360,0,1,0);LegacyGunGL.glScaled(4.5,4.5,-4.5);
   }else{
    // Source ENTITY opts into bobbing/rotation; native ItemEntityRenderer owns those two helpers.
    poses.translate(.5,context==ItemDisplayContext.GROUND?.25:.5,.5);poses.scale(.5F,.5F,.5F);LegacyGunGL.glScaled(.3,.3,.3);
   }
   LegacyGunGL.mesh(MODEL,null);
  }catch(RuntimeException|Error failure){throw failure;}catch(Exception impossible){throw new AssertionError(impossible);}finally{poses.popPose();}
 }
}
