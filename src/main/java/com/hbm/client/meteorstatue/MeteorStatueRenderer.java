package com.hbm.client.meteorstatue;
import com.hbm.block.meteorstatue.*;
import com.hbm.client.model.LegacyContactShadows;
import com.hbm.client.model.legacy.generated.*;
import com.hbm.registry.HbmContent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;

/** Complete original ModelStatue + real watch + ModelGun, original source transform sequence. */
public final class MeteorStatueRenderer implements BlockEntityRenderer<MeteorStatueBlockEntity> {
 private static final ResourceLocation STATUE_TEXTURE=ResourceLocation.parse("hbm:textures/models/modelstatue.png"),GUN_TEXTURE=ResourceLocation.parse("hbm:textures/models/modelgun.png");
 private final ModelStatue statue=new ModelStatue();private final ModelGun gun=new ModelGun();private final MeteorStatueContactMesh contact=new MeteorStatueContactMesh();
 public MeteorStatueRenderer(BlockEntityRendererProvider.Context context){}
 @Override public void render(MeteorStatueBlockEntity tile,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){int meta=tile.getBlockState().getValue(MeteorStatueBlock.META);ItemStack watch=new ItemStack(HbmContent.item("watch"));PoseStack local=new PoseStack();MeteorStatueTransforms.body(local,meta);LegacyContactShadows.render(tile,contact.get(statue,gun,watch,tile.getLevel()),new Matrix4f(local.last().pose()),pose,buffers);pose.pushPose();MeteorStatueTransforms.body(pose,meta);statue.renderToBuffer(pose,buffers.getBuffer(RenderType.entityCutoutNoCull(STATUE_TEXTURE)),light,overlay,-1,.0625F);MeteorStatueTransforms.toWatch(pose);pose.pushPose();MeteorStatueTransforms.watchItem(pose);Minecraft.getInstance().getItemRenderer().renderStatic(watch,ItemDisplayContext.FIXED,light,overlay,pose,buffers,tile.getLevel(),0);pose.popPose();MeteorStatueTransforms.toGun(pose);gun.renderToBuffer(pose,buffers.getBuffer(RenderType.entityCutoutNoCull(GUN_TEXTURE)),light,overlay,-1,.0625F);pose.popPose();}
 @Override public boolean shouldRenderOffScreen(MeteorStatueBlockEntity tile){return true;}
 @Override public AABB getRenderBoundingBox(MeteorStatueBlockEntity tile){return AABB.INFINITE;}
 @Override public int getViewDistance(){return 256;}
}
