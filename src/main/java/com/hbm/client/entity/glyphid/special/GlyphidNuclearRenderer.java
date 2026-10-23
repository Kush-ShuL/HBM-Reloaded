package com.hbm.client.entity.glyphid.special;

import com.hbm.client.entity.glyphid.GlyphidRenderer;
import com.hbm.entity.mob.glyphid.special.EntityGlyphidNuclear;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;

/** Original fourth-power swell and exact ARGB colorMultiplier, with no ordinary infected-texture pass. */
public final class GlyphidNuclearRenderer extends GlyphidRenderer<EntityGlyphidNuclear> {
    private static final RenderType FLASH = RenderType.entityTranslucent(ResourceLocation.parse("hbm:textures/entity/glyphid_nuclear_flash_white.png"));
    public GlyphidNuclearRenderer(EntityRendererProvider.Context context) {
        super(context, new GlyphidNuclearModel(), false);
        addLayer(new RenderLayer<>(this) {
            @Override public void render(PoseStack poses, MultiBufferSource buffers, int light, EntityGlyphidNuclear entity,
                    float limb, float amount, float partial, float age, float yaw, float pitch) {
                int argb = sourceColorMultiplier(entity, partial);
                if (entity.isInvisible() || (argb >>> 24) == 0) return;
                ((GlyphidNuclearModel)getParentModel()).renderColorOverlay(poses, buffers.getBuffer(FLASH),
                        light, OverlayTexture.NO_OVERLAY, argb);
            }
        });
    }
    @Override protected void scale(EntityGlyphidNuclear entity, PoseStack poses, float partial) {
        float swell = (entity.deathTicks + partial) / 95F;
        float flash = 1F + Mth.sin(swell * 100F) * swell * .01F;
        swell = Mth.clamp(swell, 0, 1); swell *= swell; swell *= swell;
        float horizontal = (1F + swell * .4F) * flash;
        float vertical = (1F + swell * .1F) / flash;
        poses.scale(horizontal, vertical, horizontal);
    }
    private static int sourceColorMultiplier(EntityGlyphidNuclear entity, float partial) {
        float swell = (entity.deathTicks + partial) / 20F;
        int alpha = (int)(swell * .2F * 255F);
        // Source returns alpha*.75 as a whole ARGB integer: alpha channel zero, not a dimmer white flash.
        if ((int)(swell * 10F) % 4 < 2) return alpha *= .75;
        alpha = Mth.clamp(alpha, 0, 255);
        return alpha << 24 | 0xFFFFFF;
    }
    // The original model explicitly enables lighting, even on the texture-disabled colorMultiplier pass.
    // A white1x1 NEW_ENTITY texture retains that lighting, source normal alpha blend and real depth.
}
