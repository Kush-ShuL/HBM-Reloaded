package com.hbm.client.equipment.npcheadgear;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.RenderType;

/** Source GL_TEXTURE_2D disabled + lighting disabled; opaque authored 1,1,.8 flame. */
final class No9RenderType extends RenderType {
    static final RenderType FLAME = create("hbm_no9_flame", DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS, 256, false, false, CompositeState.builder()
                    .setShaderState(POSITION_COLOR_SHADER).setWriteMaskState(COLOR_DEPTH_WRITE)
                    .createCompositeState(false));
    private No9RenderType(String name, VertexFormat format, VertexFormat.Mode mode, int size,
            boolean crumbling, boolean sort, Runnable setup, Runnable clear) {
        super(name, format, mode, size, crumbling, sort, setup, clear);
    }
}
