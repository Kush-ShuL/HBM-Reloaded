package com.hbm.client.satellite.attack;
import com.hbm.satellite.attack.OrbitalLaserEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;
/** Exact source red/white250high beam, no synthetic missile/OBJ model or extra animation. */
public final class OrbitalLaserRenderer extends EntityRenderer<OrbitalLaserEntity> {
 public OrbitalLaserRenderer(EntityRendererProvider.Context context){super(context);}
 @Override public void render(OrbitalLaserEntity entity,float yaw,float partial,PoseStack pose,MultiBufferSource buffers,int light){AttackBeamGeometry.render(pose,buffers,0xffff0000,0xffffffff);}
 @Override public ResourceLocation getTextureLocation(OrbitalLaserEntity entity){return null;}
}
