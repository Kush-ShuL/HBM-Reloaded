package com.hbm.client.cyclotron;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.*;
import net.minecraft.client.renderer.RenderType;
/** Wrap the actual native font atlas type, preserving its shader/depth/texture while using source additive text. */
final class CyclotronGlyphType extends RenderType {
 private static final Map<RenderType,RenderType> TYPES=new IdentityHashMap<>();
 static RenderType of(RenderType original){return TYPES.computeIfAbsent(original,CyclotronGlyphType::new);}
 private CyclotronGlyphType(RenderType original){super("hbm_cyclotron_galactic",original.format(),original.mode(),256,false,false,()->{original.setupRenderState();RenderSystem.enableBlend();RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE);RenderSystem.disableCull();},()->{RenderSystem.enableCull();original.clearRenderState();RenderSystem.defaultBlendFunc();});}
}
