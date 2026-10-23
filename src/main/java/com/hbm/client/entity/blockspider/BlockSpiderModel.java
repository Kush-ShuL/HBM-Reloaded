package com.hbm.client.entity.blockspider;

import com.hbm.client.model.LegacyObjModels;
import com.hbm.client.model.LegacyObjRenderer;
import com.hbm.entity.mob.blockspider.EntityBlockSpider;
import com.hbm.hazard.HbmHazards;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.RenderShape;

/** Actual eight OBJ leg groups and a real block-item shell; not a vanilla spider or a fixed stone cube. */
public final class BlockSpiderModel extends EntityModel<EntityBlockSpider> {
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/mobs/blockspider.obj");
    private EntityBlockSpider entity;private float limb,amount;private MultiBufferSource buffers;
    public BlockSpiderModel(){super(RenderType::entityCutoutNoCull);}
    void begin(MultiBufferSource source){buffers=source;}
    void end(){buffers=null;entity=null;}
    @Override public void setupAnim(EntityBlockSpider mob,float limb,float amount,float age,float yaw,float pitch){entity=mob;this.limb=limb;this.amount=amount;}
    @Override public void renderToBuffer(PoseStack poses,VertexConsumer output,int light,int overlay,int color){
        if(entity==null||buffers==null)return;
        float rotation=-(Mth.cos(limb*.6662F*2F+0F)*.4F)*amount*57.3F;
        var minecraft=Minecraft.getInstance();var mesh=LegacyObjModels.INSTANCE.get(minecraft.getResourceManager(),MODEL);
        poses.pushPose();
        try{
            poses.mulPose(Axis.YP.rotationDegrees(-90));poses.mulPose(Axis.ZP.rotationDegrees(180));poses.translate(0,-1.5F,0);
            poses.pushPose();try{poses.translate(0,rotation*.005,0);poses.mulPose(Axis.YP.rotationDegrees(rotation));for(String name:new String[]{"Leg1","Leg3","Leg5","Leg7"})LegacyObjRenderer.renderOnly(mesh,poses,output,light,overlay,color,LegacyObjRenderer.Convention.FORGE,name);}finally{poses.popPose();}
            poses.pushPose();try{poses.translate(0,rotation*-.005,0);poses.mulPose(Axis.YP.rotationDegrees(-rotation));for(String name:new String[]{"Leg2","Leg4","Leg6","Leg8"})LegacyObjRenderer.renderOnly(mesh,poses,output,light,overlay,color,LegacyObjRenderer.Convention.FORGE,name);}finally{poses.popPose();}
            poses.pushPose();
            try{
                poses.translate(0,.75,0);
                var state=entity.blockState();
                // RenderBlocks.renderBlockAsItem's actual Y90/center transforms; native state renderer keeps real atlas/tints/shape.
                poses.mulPose(Axis.YP.rotationDegrees(90));
                if(state.getRenderShape()==RenderShape.ENTITYBLOCK_ANIMATED){
                    var stack=new ItemStack(state.getBlock());stack.set(HbmHazards.LEGACY_METADATA.get(),entity.blockMetadata());
                    minecraft.getItemRenderer().renderStatic(stack,ItemDisplayContext.NONE,light,overlay,poses,buffers,entity.level(),entity.getId());
                }else{poses.translate(-.5,-.5,-.5);minecraft.getBlockRenderer().renderSingleBlock(state,poses,buffers,light,overlay);}
            }finally{poses.popPose();}
        }finally{poses.popPose();}
    }
}
