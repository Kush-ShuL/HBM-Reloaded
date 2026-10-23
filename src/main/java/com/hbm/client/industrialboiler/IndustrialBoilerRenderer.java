package com.hbm.client.industrialboiler;
import com.hbm.client.model.*;
import com.hbm.machine.industrialboiler.IndustrialBoilerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Source RenderIndustrialBoiler deliberately applies no facing rotation; original HFR smooth/asVBO mesh. */
public final class IndustrialBoilerRenderer implements BlockEntityRenderer<IndustrialBoilerBlockEntity> {
 static final ResourceLocation MODEL=ResourceLocation.fromNamespaceAndPath("hbm","models/machines/industrial_boiler.obj"),TEXTURE=ResourceLocation.fromNamespaceAndPath("hbm","textures/models/machines/industrial_boiler.png");
 public IndustrialBoilerRenderer(BlockEntityRendererProvider.Context context){}static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}static void model(PoseStack poses,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(model(),poses,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR);}
 @Override public void render(IndustrialBoilerBlockEntity heater,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){LegacyContactShadows.render(heater,model(),new org.joml.Matrix4f().translation(.5F,0,.5F),poses,buffers);poses.pushPose();poses.translate(.5,0,.5);model(poses,buffers,light,overlay);poses.popPose();}
 @Override public boolean shouldRenderOffScreen(IndustrialBoilerBlockEntity heater){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(IndustrialBoilerBlockEntity heater){var pos=heater.getBlockPos();return new AABB(pos.getX()-1,pos.getY(),pos.getZ()-1,pos.getX()+2,pos.getY()+5,pos.getZ()+2);}
}
