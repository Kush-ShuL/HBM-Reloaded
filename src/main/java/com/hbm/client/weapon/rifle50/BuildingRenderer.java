package com.hbm.client.weapon.rifle50;
import com.hbm.client.model.*;
import com.hbm.weapon.sedna.gun.rifle50.EntityBuilding;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
/** Original RenderBoxcar EntityBuilding branch: no yaw, no culling, original Forge normals/UV inset, zero circular shadow. */
public final class BuildingRenderer extends EntityRenderer<EntityBuilding> {
 private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/weapons/building.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/weapons/building.png");
 public BuildingRenderer(EntityRendererProvider.Context context){super(context);}
 @Override public void render(EntityBuilding entity,float yaw,float partial,PoseStack poses,MultiBufferSource buffers,int light){var model=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);LegacyObjRenderer.renderAll(model,poses,buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)),light,OverlayTexture.NO_OVERLAY,-1,LegacyObjRenderer.Convention.FORGE);}
 @Override public ResourceLocation getTextureLocation(EntityBuilding entity){return TEXTURE;}
}
