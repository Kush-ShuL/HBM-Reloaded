package com.hbm.client.weapon.folly;
import com.hbm.client.model.*;
import com.hbm.client.weapon.SednaBeamRenderContext;
import com.hbm.client.weapon.energy.EnergyBeams;
import com.hbm.client.weapon.flaregun.FlareSignalRenderer;
import com.hbm.client.weapon.pepperbox.GunBulletRenderContext;
import com.hbm.entity.projectile.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
/** Original GunFactoryClient237/238, using the single actual flare and BeamPronter implementations. */
final class FollyVisuals {
    static void beam(EntityBulletBeamBase bullet,float partial){
        double age=Mth.clamp(1D-((double)bullet.tickCount-2+partial)/bullet.getBulletConfig().expires,0,1);
        var context=SednaBeamRenderContext.current();var pose=context.poses();
        FlareSignalRenderer.sprite(pose,(1-age)*7.5+1.5,1,1,1,.5F*(float)age,.75F*(float)age);
        pose.pushPose();
        pose.mulPose(Axis.YP.rotationDegrees(180-bullet.getYRot()));pose.mulPose(Axis.XP.rotationDegrees(-bullet.getXRot()-90));
        float scale=(float)((1-age)*25+2.5);pose.scale(scale,1,scale);
        int color=((int)(0x20*age)<<16)|((int)(0x20*age)<<8)|(int)(0x20*age);
        // Zero additive radiance must not leave an invisible occluder at the end of the existing fade.
        if(color!=0)EnergyBeams.solidWithDepth(pose,context.buffers(),bullet.beamLength,color,color,bullet.tickCount/3,(int)(bullet.beamLength/2+1),0,8,.0625F);
        pose.popPose();
    }
    static void nuke(EntityBulletBaseMK4 bullet,float partial){
        var context=GunBulletRenderContext.current();var pose=context.poses();pose.pushPose();pose.scale(.5F,.5F,.5F);pose.mulPose(Axis.ZP.rotationDegrees(90));
        var texture=ResourceLocation.parse("hbm:textures/models/projectiles/rocket_mirv.png");
        var model=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.parse("hbm:models/projectiles/projectiles.obj"));
        LegacyObjRenderer.renderOnly(model,pose,context.buffers().getBuffer(RenderType.entityCutout(texture)),context.light(),OverlayTexture.NO_OVERLAY,-1,LegacyObjRenderer.Convention.HFR,"MissileMIRV");pose.popPose();
    }
    private FollyVisuals(){}
}
