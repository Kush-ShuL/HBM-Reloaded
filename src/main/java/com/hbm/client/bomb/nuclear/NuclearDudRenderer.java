package com.hbm.client.bomb.nuclear;

import com.hbm.bomb.nuclear.NuclearDudBlock;
import com.hbm.bomb.nuclear.NuclearDudBlockEntity;
import com.hbm.client.model.LegacyObjModels;
import com.hbm.client.model.LegacyObjRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Locale;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

/** Source position identity hash and its four ordered random transforms, with the original authored meshes. */
public final class NuclearDudRenderer implements BlockEntityRenderer<NuclearDudBlockEntity> {
 public NuclearDudRenderer(BlockEntityRendererProvider.Context context){}
 static void draw(NuclearDudBlock.Type type,PoseStack poses,MultiBufferSource buffers,int light,int overlay){String name="dud_"+type.name().toLowerCase(Locale.ROOT);var model=ResourceLocation.fromNamespaceAndPath("hbm","models/bombs/"+name+".obj");var texture=ResourceLocation.fromNamespaceAndPath("hbm","textures/models/bombs/"+name+".png");LegacyObjRenderer.renderAll(LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),model),poses,buffers.getBuffer(RenderType.entityCutout(texture)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR);}
 @Override public void render(NuclearDudBlockEntity entity,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){var pos=entity.getBlockPos();var random=new Random((pos.getY()+pos.getZ()*27644437)*27644437+pos.getX());double yaw=random.nextDouble()*360,pitch=random.nextDouble()*45+45,roll=random.nextDouble()*360,offset=random.nextDouble()*2-1;poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotation((float)Math.toRadians(yaw)));poses.mulPose(Axis.XP.rotation((float)Math.toRadians(pitch)));poses.mulPose(Axis.ZP.rotation((float)Math.toRadians(roll)));poses.translate(0,0,-offset);var type=NuclearDudBlock.type(entity.getBlockState());if(type==NuclearDudBlock.Type.NUKE)poses.translate(0,0,1.25);if(type==NuclearDudBlock.Type.SALTED)poses.translate(0,0,.5);draw(type,poses,buffers,light,overlay);poses.popPose();}
 @Override public boolean shouldRenderOffScreen(NuclearDudBlockEntity entity){return true;}
 @Override public int getViewDistance(){return 256;}
 @Override public AABB getRenderBoundingBox(NuclearDudBlockEntity entity){return AABB.INFINITE;}
}
