package com.hbm.client.pedestal;
import com.hbm.machine.pedestal.PedestalBlockEntity;
import com.hbm.client.excavator.MovingItemRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.util.Mth;
import net.minecraft.world.item.*;
/** Original RenderPedestalTile full item, player-facing2D branch and exact scaled vertical bob. */
public final class PedestalRenderer implements BlockEntityRenderer<PedestalBlockEntity> {
    public PedestalRenderer(BlockEntityRendererProvider.Context context){}
    @Override public void render(PedestalBlockEntity pedestal,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        if(pedestal.item().isEmpty())return;var mc=Minecraft.getInstance();if(mc.player==null)return;var stack=pedestal.item().copy();
        var model=mc.getItemRenderer().getModel(stack,pedestal.getLevel(),null,0);
        boolean block=stack.getItem() instanceof BlockItem&&model.isGui3d();
        poses.pushPose();poses.translate(.5,1,.5);poses.scale(1.5F,1.5F,1.5F);
        double bob=Math.sin((mc.player.tickCount+partial)*.1)*.0625;
        if(!block){poses.translate(0,.125,0);poses.mulPose(Axis.YP.rotationDegrees(-(Mth.lerp(partial,mc.player.yRotO,mc.player.getYRot())+180)));poses.translate(0,bob,0);}
        else poses.translate(0,bob+.0625,0);
        // Same-world player supplies only level/id context; the existing frame
        // kernel draws the source frozen age0/hover0 item, not the player.
        // It retains the original .1Y and distinct sprite/block matrices/counts.
        MovingItemRenderer.renderSourceFrame(mc.player,stack,model,poses,buffers,light);poses.popPose();
    }
}
