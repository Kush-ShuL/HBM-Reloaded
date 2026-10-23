package com.hbm.client.fractiontower;
import com.hbm.client.model.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
/** Original static AdvancedModelLoader meshes, tower two-sided and spacer culled. Neither has a source facing matrix. */
public final class FractionRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
 private final boolean spacer;public FractionRenderer(boolean spacer){this.spacer=spacer;}
 static String stem(boolean spacer){return spacer?"fraction_spacer":"fraction_tower";}
 static WavefrontMesh mesh(boolean spacer){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.parse("hbm:models/machines/"+stem(spacer)+".obj"));}
 static void model(boolean spacer,boolean world,PoseStack pose,MultiBufferSource buffers,int light,int overlay){var texture=ResourceLocation.parse("hbm:textures/models/machines/"+stem(spacer)+".png");LegacyObjRenderer.renderAll(mesh(spacer),pose,buffers.getBuffer(world&&!spacer?RenderType.entityCutoutNoCull(texture):RenderType.entityCutout(texture)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.FORGE);}
 @Override public void render(T tower,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){LegacyContactShadows.render(tower,mesh(spacer),new org.joml.Matrix4f().translation(.5F,0,.5F),pose,buffers);pose.pushPose();pose.translate(.5,0,.5);model(spacer,true,pose,buffers,light,overlay);pose.popPose();}
 @Override public boolean shouldRenderOffScreen(T tower){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(T tower){var p=tower.getBlockPos();return new AABB(p.getX()-1,p.getY(),p.getZ()-1,p.getX()+2,p.getY()+(spacer?1:3),p.getZ()+2);}
}
