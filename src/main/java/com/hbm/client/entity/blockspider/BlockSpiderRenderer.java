package com.hbm.client.entity.blockspider;

import com.hbm.entity.mob.blockspider.EntityBlockSpider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;

public final class BlockSpiderRenderer extends MobRenderer<EntityBlockSpider,BlockSpiderModel> {
    private static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/entity/blockspider.png");
    public BlockSpiderRenderer(EntityRendererProvider.Context context){super(context,new BlockSpiderModel(),1F);}
    @Override public ResourceLocation getTextureLocation(EntityBlockSpider spider){return TEXTURE;}
    @Override protected void scale(EntityBlockSpider spider,PoseStack poses,float partial){poses.translate(0,-1.5078125D-(double)-1.501F,0);}
    @Override public void render(EntityBlockSpider spider,float yaw,float partial,PoseStack poses,MultiBufferSource buffers,int light){model.begin(buffers);try{super.render(spider,yaw,partial,poses,buffers,light);}finally{model.end();}}
}
