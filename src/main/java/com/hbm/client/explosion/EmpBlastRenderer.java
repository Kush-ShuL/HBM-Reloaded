package com.hbm.client.explosion;

import com.hbm.client.model.LegacyObjModels;
import com.hbm.client.model.LegacyObjRenderer;
import com.hbm.entity.effect.EntityEMPBlast;
import com.hbm.explosion.ThermalContent;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/** Original Ring.obj, Forge UV inset, tick-step XZ scale, no lighting/culling/blending. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class EmpBlastRenderer extends EntityRenderer<EntityEMPBlast> {
    private static final ResourceLocation MODEL=ResourceLocation.fromNamespaceAndPath("hbm","models/ring.obj");
    private static final ResourceLocation TEXTURE=ResourceLocation.fromNamespaceAndPath("hbm","textures/models/empblast.png");
    public EmpBlastRenderer(EntityRendererProvider.Context context) { super(context); }
    @SubscribeEvent public static void register(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(ThermalContent.EMP.get(),EmpBlastRenderer::new); }
    @Override public ResourceLocation getTextureLocation(EntityEMPBlast entity) { return TEXTURE; }
    @Override public boolean shouldRender(EntityEMPBlast entity,Frustum frustum,double x,double y,double z) { return entity.shouldRender(x,y,z); }
    @Override public void render(EntityEMPBlast entity,float yaw,float partial,PoseStack poses,MultiBufferSource buffers,int light) {
        poses.pushPose();poses.scale(entity.scale,1,entity.scale);
        LegacyObjRenderer.renderAll(LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL),poses,buffers.getBuffer(RingType.RING),LightTexture.FULL_BRIGHT,OverlayTexture.NO_OVERLAY,0xFFFFFFFF,LegacyObjRenderer.Convention.FORGE);
        poses.popPose();
    }
    private static final class RingType extends RenderType {
        private RingType() { super("unused",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{}); }
        private static final RenderType RING=create("hbm_emp_ring",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,1536,false,false,
                CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER).setTextureState(new TextureStateShard(TEXTURE,false,false)).setCullState(NO_CULL).setTransparencyState(NO_TRANSPARENCY).createCompositeState(false));
    }
}
