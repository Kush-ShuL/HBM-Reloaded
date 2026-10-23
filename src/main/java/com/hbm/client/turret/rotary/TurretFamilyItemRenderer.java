package com.hbm.client.turret.rotary;

import com.hbm.client.model.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;

/** Original independent ItemRenderLibrary providers, with static source parts and item matrices. */
public final class TurretFamilyItemRenderer extends BlockEntityWithoutLevelRenderer {
    public enum Kind {CHEKHOV, FRIENDLY, JEREMY, HOWARD, HOWARD_DAMAGED, GREG}
    private static final ResourceLocation COMMON=model("turret_chekhov"), CANNON=model("turret_jeremy"),
            HOWARD=model("turret_howard"), DAMAGED=model("turret_howard_damaged"), ARTILLERY=model("turret_arty");
    private final Kind kind;

    public TurretFamilyItemRenderer(Kind kind) {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        this.kind=kind;
    }

    private static ResourceLocation model(String name) {
        return ResourceLocation.parse("hbm:models/turrets/"+name+".obj");
    }

    private static WavefrontMesh mesh(ResourceLocation model) {
        return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(), model);
    }

    private static void part(WavefrontMesh mesh, String texture, String group, PoseStack pose,
                             MultiBufferSource buffers, int light, int overlay) {
        LegacyObjRenderer.renderOnly(mesh, pose, buffers.getBuffer(RenderType.entityCutout(
                ResourceLocation.parse("hbm:textures/models/turrets/"+texture+".png"))),
                light, overlay, -1, LegacyObjRenderer.Convention.HFR, group);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack pose,
                             MultiBufferSource buffers, int light, int overlay) {
        pose.pushPose();
        try {
            LegacyItemTransforms.held(pose, context);
            if (context==ItemDisplayContext.GUI) {
                pose.translate(0,1,.5);
                pose.scale(1F/16,-1F/16,1F/16);
                pose.translate(8,10,0);
                pose.mulPose(Axis.XP.rotationDegrees(-30));
                pose.mulPose(Axis.YP.rotationDegrees(45));
                pose.scale(-1,-1,-1);
                switch (kind) {
                    case JEREMY -> {pose.translate(0,-2,0); pose.scale(2.5F,2.5F,2.5F);}
                    case HOWARD, HOWARD_DAMAGED -> {pose.translate(0,-4.5,0); pose.scale(4,4,4);}
                    case GREG -> {pose.translate(-3,-4,0); pose.scale(3.5F,3.5F,3.5F);}
                    default -> {pose.translate(0,-3,0); pose.scale(4,4,4);}
                }
            } else {
                if (context==ItemDisplayContext.GROUND) {
                    pose.translate(.5,0,.5);
                    pose.scale(1.5F,1.5F,1.5F);
                } else pose.translate(.5,.25,0);
                pose.scale(.25F,.25F,.25F);
                if (context!=ItemDisplayContext.THIRD_PERSON_LEFT_HAND && context!=ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
                    pose.mulPose(Axis.YP.rotationDegrees(90));
            }

            if (kind==Kind.GREG) {
                pose.mulPose(Axis.YP.rotationDegrees(-90));
                pose.scale(.5F,.5F,.5F);
                var arty=mesh(ARTILLERY);
                part(arty,"arty","Base",pose,buffers,light,overlay);
                part(arty,"arty","Carriage",pose,buffers,light,overlay);
                pose.translate(0,3,0);
                pose.mulPose(Axis.XP.rotationDegrees(45));
                pose.translate(0,-3,0);
                part(arty,"arty","Cannon",pose,buffers,light,overlay);
                part(arty,"arty","Barrel",pose,buffers,light,overlay);
                return;
            }

            pose.translate(kind==Kind.JEREMY?-.5:-.75,0,0);
            var common=mesh(COMMON);
            if (kind==Kind.HOWARD || kind==Kind.HOWARD_DAMAGED) {
                boolean damaged=kind==Kind.HOWARD_DAMAGED;
                String prefix=damaged?"rusted/":"";
                part(common,prefix+"base","Base",pose,buffers,light,overlay);
                // The damaged source still uses the intact model's Carriage group.
                part(mesh(HOWARD),prefix+"carriage_ciws","Carriage",pose,buffers,light,overlay);
                var body=mesh(damaged?DAMAGED:HOWARD);
                part(body,prefix+"howard","Body",pose,buffers,light,overlay);
                part(body,prefix+"howard_barrels","BarrelsTop",pose,buffers,light,overlay);
                part(body,prefix+"howard_barrels","BarrelsBottom",pose,buffers,light,overlay);
            } else {
                boolean friendly=kind==Kind.FRIENDLY;
                part(common,friendly?"base_friendly":"base","Base",pose,buffers,light,overlay);
                part(common,friendly?"carriage_friendly":"carriage","Carriage",pose,buffers,light,overlay);
                if (kind==Kind.JEREMY) part(mesh(CANNON),"jeremy","Gun",pose,buffers,light,overlay);
                else {
                    part(common,"chekhov","Body",pose,buffers,light,overlay);
                    part(common,"chekhov_barrels","Barrels",pose,buffers,light,overlay);
                }
            }
        } finally {
            pose.popPose();
        }
    }
}
