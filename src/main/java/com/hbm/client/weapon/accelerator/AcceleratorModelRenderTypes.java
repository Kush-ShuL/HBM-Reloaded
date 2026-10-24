package com.hbm.client.weapon.accelerator;
import com.mojang.blaze3d.vertex.*;
import com.hbm.client.weapon.pepperbox.GunShaderCompat;
import java.util.*;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
/** Source alpha cutoff and true lighting bypass, rather than a fullbright lightmap-only approximation. */
public final class AcceleratorModelRenderTypes extends RenderType {
    static ShaderInstance shader;
    private record Key(ResourceLocation texture,boolean fullbright,boolean cull,float cutoff){}
    private static final Map<Key,RenderType> TYPES=new HashMap<>();
    private AcceleratorModelRenderTypes(){super("hbm_accelerator_unused",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
    public static RenderType model(ResourceLocation texture,boolean fullbright,boolean cull,float cutoff){return TYPES.computeIfAbsent(new Key(texture,fullbright,cull,cutoff),key->wrap(key,create("hbm_accelerator_model",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,8192,false,false,CompositeState.builder()
        .setShaderState(new ShaderStateShard(()->{var hand=GunShaderCompat.modelShader(key.fullbright,key.cutoff);if(hand!=null)return hand;var value=Objects.requireNonNull(shader,"Accelerator model shader");value.safeGetUniform("HbmAlphaCutoff").set(key.cutoff);value.safeGetUniform("HbmFullbright").set(key.fullbright?1F:0F);return value;}))
        .setTextureState(new TextureStateShard(key.texture,false,false)).setTransparencyState(NO_TRANSPARENCY).setCullState(key.cull?CULL:NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true))));}
    private static RenderType wrap(Key key,RenderType delegate) {
        return key.fullbright && key.cutoff==.1F?new UnlitModel(key,delegate):delegate;
    }
    /** 仅原全亮材质的真实实体绘制；仍复用原生立即缓冲上传，不增加渲染队列或目标。 */
    private static final class UnlitModel extends RenderType {
        private final Key key;
        private final RenderType delegate;
        UnlitModel(Key key,RenderType delegate) {
            super("hbm_accelerator_unlit",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,8192,false,false,
                    delegate::setupRenderState,delegate::clearRenderState);
            this.key=key;this.delegate=delegate;
        }
        @Override public Optional<RenderType> outline() { return delegate.outline(); }
        @Override public boolean isOutline() { return delegate.isOutline(); }
        @Override public void draw(MeshData mesh) {
            ShaderInstance nativeShader=GunShaderCompat.unlitWorldModelShader(key.cutoff);
            if(nativeShader==null) { delegate.draw(mesh);return; }
            try {
                setupRenderState();
                com.mojang.blaze3d.systems.RenderSystem.setShader(()->nativeShader);
                try {
                    nativeShader.setDefaultUniforms(mesh.drawState().mode(),
                            com.mojang.blaze3d.systems.RenderSystem.getModelViewMatrix(),
                            com.mojang.blaze3d.systems.RenderSystem.getProjectionMatrix(),
                            net.minecraft.client.Minecraft.getInstance().getWindow());
                    nativeShader.apply();
                    // Iris 的全亮实体程序保留0.1裁剪，但其默认 SpiderEyes 混合会覆盖源 NO_TRANSPARENCY。
                    // 在 apply 之后仅临时关闭实际混合；逐 draw buffer 原样恢复，随后再让 Iris 正常 clear。
                    int count=org.lwjgl.opengl.GL11.glGetInteger(org.lwjgl.opengl.GL20.GL_MAX_DRAW_BUFFERS);
                    boolean[] blend=new boolean[count];
                    for(int i=0;i<count;i++)blend[i]=org.lwjgl.opengl.GL30.glIsEnabledi(org.lwjgl.opengl.GL11.GL_BLEND,i);
                    try {
                        org.lwjgl.opengl.GL11.glDisable(org.lwjgl.opengl.GL11.GL_BLEND);
                        BufferUploader.draw(mesh);
                    } finally {
                        for(int i=0;i<count;i++) {
                            if(blend[i])org.lwjgl.opengl.GL30.glEnablei(org.lwjgl.opengl.GL11.GL_BLEND,i);
                            else org.lwjgl.opengl.GL30.glDisablei(org.lwjgl.opengl.GL11.GL_BLEND,i);
                        }
                    }
                } finally { nativeShader.clear(); }
            } finally {
                try { clearRenderState(); }
                finally { mesh.close(); }
            }
        }
    }
}
