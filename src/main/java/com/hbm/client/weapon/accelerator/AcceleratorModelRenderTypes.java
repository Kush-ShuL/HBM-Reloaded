package com.hbm.client.weapon.accelerator;
import com.mojang.blaze3d.vertex.*;
import java.util.*;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
/** Source alpha cutoff and true lighting bypass, rather than a fullbright lightmap-only approximation. */
public final class AcceleratorModelRenderTypes extends RenderType {
    static ShaderInstance shader;
    private record Key(ResourceLocation texture,boolean fullbright,boolean cull,float cutoff){}
    private static final Map<Key,RenderType> TYPES=new HashMap<>();
    private AcceleratorModelRenderTypes(){super("hbm_accelerator_unused",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
    public static RenderType model(ResourceLocation texture,boolean fullbright,boolean cull,float cutoff){return TYPES.computeIfAbsent(new Key(texture,fullbright,cull,cutoff),key->create("hbm_accelerator_model",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,8192,false,false,CompositeState.builder()
        .setShaderState(new ShaderStateShard(()->{var value=Objects.requireNonNull(shader,"Accelerator model shader");value.safeGetUniform("HbmAlphaCutoff").set(key.cutoff);value.safeGetUniform("HbmFullbright").set(key.fullbright?1F:0F);return value;}))
        .setTextureState(new TextureStateShard(key.texture,false,false)).setTransparencyState(NO_TRANSPARENCY).setCullState(key.cull?CULL:NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true)));}
}
