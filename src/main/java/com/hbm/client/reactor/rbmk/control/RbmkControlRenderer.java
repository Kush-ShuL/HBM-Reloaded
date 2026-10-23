package com.hbm.client.reactor.rbmk.control;
import com.hbm.client.model.*;
import com.hbm.machine.reactor.rbmk.control.RbmkControlEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Original RenderRBMKControlRod: actual top lighting, exact Lid mesh and interpolated extraction. */
public final class RbmkControlRenderer implements BlockEntityRenderer<RbmkControlEntity> {
    public RbmkControlRenderer(BlockEntityRendererProvider.Context context){}
    @Override public void render(RbmkControlEntity control,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){var world=control.getLevel();if(world==null)return;int offset=1;for(int y=1;y<16;y++){if(world.getBlockState(control.getBlockPos().above(y)).is(control.getBlockState().getBlock()))offset=y;else break;}poses.pushPose();poses.translate(.5,offset,.5);poses.translate(0,control.lastExtraction+(control.extraction-control.lastExtraction)*partial,0);String name=control.automatic()?"rbmk_control_auto":control.color==null?"rbmk_control":"rbmk_control_"+control.color.name().toLowerCase(java.util.Locale.ROOT);var texture=ResourceLocation.parse("hbm:textures/blocks/rbmk/"+name+".png");var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.parse("hbm:models/rbmk/rbmk_rods.obj"));int topLight=LevelRenderer.getLightColor(world,control.getBlockPos().above(offset+1));LegacyObjRenderer.render(mesh,poses,buffers.getBuffer(RenderType.entityCutout(texture)),topLight,overlay,-1,LegacyObjRenderer.Convention.HFR_FLAT,face->face.legacyPart().equals("Lid"));poses.popPose();}
    @Override public boolean shouldRenderOffScreen(RbmkControlEntity control){return true;}@Override public int getViewDistance(){return 256;}
    @Override public AABB getRenderBoundingBox(RbmkControlEntity control){var p=control.getBlockPos();return new AABB(p.getX(),p.getY(),p.getZ(),p.getX()+1,p.getY()+18,p.getZ()+1);}
}
