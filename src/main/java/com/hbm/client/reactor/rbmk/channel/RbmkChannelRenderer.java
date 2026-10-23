package com.hbm.client.reactor.rbmk.channel;
import com.hbm.client.model.*;
import com.hbm.machine.reactor.rbmk.channel.RbmkFuelChannelEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Exact RenderRBMKFuelChannel rod repetition/color and horizontal Cherenkov geometry. */
public final class RbmkChannelRenderer implements BlockEntityRenderer<RbmkFuelChannelEntity> {
    public RbmkChannelRenderer(BlockEntityRendererProvider.Context context) { }
    @Override public void render(RbmkFuelChannelEntity channel,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay) {
        var world=channel.getLevel();if(world==null)return;int offset=1;
        for(int y=1;y<16;y++){var state=world.getBlockState(channel.getBlockPos().above(y));if(!state.is(channel.getBlockState().getBlock()))break;offset=y;int meta=state.getValue(DummyableBlock.LEGACY_META);if(meta>5&&meta<12)break;}
        poses.pushPose();poses.translate(.5,0,.5);
        if(channel.hasRod){var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.parse("hbm:models/rbmk/rbmk_element_rods.obj"));var target=buffers.getBuffer(RenderType.entityCutout(ResourceLocation.parse("hbm:textures/blocks/rbmk/rbmk_element_fuel.png")));poses.pushPose();for(int y=0;y<=offset;y++){LegacyObjRenderer.render(mesh,poses,target,light,overlay,0xFF000000|channel.rodColor&0xFFFFFF,LegacyObjRenderer.Convention.HFR_FLAT,face->face.legacyPart().equals("Rods"));poses.translate(0,1,0);}poses.popPose();}
        if(channel.fluxQuantity>5){poses.translate(0,.75,0);var out=buffers.getBuffer(RbmkChannelRenderTypes.CHERENKOV);var pose=poses.last();for(double y=0;y<=offset;y+=.25){out.addVertex(pose,-.5F,(float)y,-.5F).setColor(.4F,.9F,1F,.1F);out.addVertex(pose,-.5F,(float)y,.5F).setColor(.4F,.9F,1F,.1F);out.addVertex(pose,.5F,(float)y,.5F).setColor(.4F,.9F,1F,.1F);out.addVertex(pose,.5F,(float)y,-.5F).setColor(.4F,.9F,1F,.1F);}}
        poses.popPose();
    }
    @Override public boolean shouldRenderOffScreen(RbmkFuelChannelEntity channel){return true;}
    @Override public AABB getRenderBoundingBox(RbmkFuelChannelEntity channel){return channel.renderBounds();}
}
