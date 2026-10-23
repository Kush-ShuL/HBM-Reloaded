package com.hbm.client.weapon.fatman;
import com.hbm.client.model.*;
import com.hbm.client.weapon.pepperbox.GunBulletRenderContext;
import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
/** Actual LegoClient RENDER_NUKE/BALEFIRE/GRENADE/HIVE/BOMB matrices, without a fake generic projectile appearance. */
final class FatmanVisuals {
 private static void draw(ResourceLocation model,ResourceLocation texture,String part,com.mojang.blaze3d.vertex.PoseStack pose,MultiBufferSource buffers,int light){LegacyObjRenderer.renderOnly(LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),model),pose,buffers.getBuffer(RenderType.entityCutout(texture)),light,OverlayTexture.NO_OVERLAY,-1,LegacyObjRenderer.Convention.HFR,part);}
 static void nuke(EntityBulletBaseMK4 bullet,float partial){nuke(bullet,partial,false,false);}
 static void balefire(EntityBulletBaseMK4 bullet,float partial){nuke(bullet,partial,true,false);}
 static void bomb(EntityBulletBaseMK4 bullet,float partial){nuke(bullet,partial,false,true);}
 private static void nuke(EntityBulletBaseMK4 bullet,float partial,boolean balefire,boolean bomb){var ctx=GunBulletRenderContext.current();var pose=ctx.poses();pose.pushPose();float scale=bomb?.0625F:.125F;pose.scale(scale,scale,scale);pose.mulPose(Axis.YP.rotationDegrees(-90));pose.translate(0,-1,1);if(balefire)FatmanGlint.render(pose,ctx.light(),OverlayTexture.NO_OVERLAY,partial);else draw(ResourceLocation.parse("hbm:models/weapons/fatman.obj"),bomb?ResourceLocation.parse("hbm:textures/models/weapons/fatman_submunition.png"):FatmanModels.fatman_mininuke_tex,"MiniNuke",pose,ctx.buffers(),ctx.light());pose.popPose();}
 static void tiny(EntityBulletBaseMK4 bullet,float partial){var ctx=GunBulletRenderContext.current();var pose=ctx.poses();pose.pushPose();pose.scale(.25F,.25F,.25F);pose.mulPose(Axis.ZP.rotationDegrees(90));draw(ResourceLocation.parse("hbm:models/projectiles/projectiles.obj"),ResourceLocation.parse("hbm:textures/models/projectiles/grenade.png"),"Grenade",pose,ctx.buffers(),ctx.light());pose.popPose();}
 static void hive(EntityBulletBaseMK4 bullet,float partial){var ctx=GunBulletRenderContext.current();var pose=ctx.poses();pose.pushPose();pose.scale(.125F,.125F,.125F);pose.mulPose(Axis.YP.rotationDegrees(-90));pose.translate(0,0,3.5);draw(ResourceLocation.parse("hbm:models/weapons/panzerschreck.obj"),ResourceLocation.parse("hbm:textures/models/weapons/panzerschreck.png"),"Rocket",pose,ctx.buffers(),ctx.light());pose.popPose();}
 private FatmanVisuals(){}
}
