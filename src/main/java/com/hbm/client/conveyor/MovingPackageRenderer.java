package com.hbm.client.conveyor;

import com.hbm.client.excavator.MovingItemRenderer;
import com.hbm.conveyor.MovingPackage;
import com.hbm.world.structure.loot.StructureLootContent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/** RenderMovingPackage: actual source crate item, y-.0125 and scale2, exact shared RenderItem.renderInFrame kernel. */
public final class MovingPackageRenderer extends EntityRenderer<MovingPackage> {
    private ItemStack crate;
    public MovingPackageRenderer(EntityRendererProvider.Context context){super(context);}
    @Override public void render(MovingPackage entity,float yaw,float partial,PoseStack poses,MultiBufferSource buffers,int light){
        if(crate==null)crate=new ItemStack(StructureLootContent.CRATE_ITEM.get());
        poses.pushPose();
        try{
            poses.translate(0,-.0125,0);poses.scale(2,2,2);
            var model=Minecraft.getInstance().getItemRenderer().getModel(crate,entity.level(),null,0);
            MovingItemRenderer.renderSourceFrame(entity,crate,model,poses,buffers,light);
        }finally{poses.popPose();}
    }
    @Override public ResourceLocation getTextureLocation(MovingPackage entity){return TextureAtlas.LOCATION_BLOCKS;}
}
