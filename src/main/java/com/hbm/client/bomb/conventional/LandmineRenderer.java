package com.hbm.client.bomb.conventional;

import com.hbm.bomb.conventional.LandmineBlock;
import com.hbm.bomb.conventional.LandmineBlockEntity;
import com.hbm.client.model.LegacyObjModels;
import com.hbm.client.model.LegacyObjRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;

/** Original RenderLandmine camouflage selection and authored OBJ transforms. */
public final class LandmineRenderer implements BlockEntityRenderer<LandmineBlockEntity> {
    public LandmineRenderer(BlockEntityRendererProvider.Context context){}
    static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath("hbm",path.toLowerCase(java.util.Locale.ROOT));}
    @Override public void render(LandmineBlockEntity mine,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        var kind=((LandmineBlock)mine.getBlockState().getBlock()).kind();poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(180));String texture;
        if(kind==LandmineBlock.Kind.AP){poses.scale(.375F,.375F,.375F);poses.translate(0,-.0625*3.5,0);var level=mine.getLevel();var pos=mine.getBlockPos();var biome=level.getBiome(pos).value();int height=level.getHeight(Heightmap.Types.WORLD_SURFACE,pos.getX(),pos.getZ());
            while(height>level.getMinBuildHeight()){var at=new BlockPos(pos.getX(),height-1,pos.getZ());if(level.getBlockState(at).getLightBlock(level,at)>0)break;height--;}
            if(height>pos.getY()+2)texture="mine_ap_stone";else if(biome.hasPrecipitation()&&biome.getBaseTemperature()<=.15F)texture="mine_ap_snow";else if(biome.getBaseTemperature()>=1.5F&&biome.getModifiedClimateSettings().downfall()<=.1F)texture="mine_ap_desert";else texture="mine_ap_grass";
        }else if(kind==LandmineBlock.Kind.SHRAP){poses.scale(.375F,.375F,.375F);poses.translate(0,-.0625*3.5,0);texture="mine_shrapnel";}
        else if(kind==LandmineBlock.Kind.NAVAL){poses.translate(0,.5,0);texture="nmine";}
        else if(kind==LandmineBlock.Kind.FAT){poses.scale(.25F,.25F,.25F);texture="mine_fat";}
        else{poses.mulPose(Axis.YP.rotationDegrees(-90));texture="mine_marelet";}
        mesh(kind,texture,poses,buffers,light,overlay);poses.popPose();
    }
    static void mesh(LandmineBlock.Kind kind,String texture,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        var model=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),id("models/bombs/"+(kind==LandmineBlock.Kind.NAVAL?"naval_mine":kind==LandmineBlock.Kind.HE?"marelet":kind==LandmineBlock.Kind.FAT?"mine_fat":"ap_mine")+".obj"));
        LegacyObjRenderer.renderAll(model,poses,buffers.getBuffer(RenderType.entityCutoutNoCull(id("textures/models/"+(kind==LandmineBlock.Kind.NAVAL?"":"bombs/")+texture+".png"))),light,overlay,0xFFFFFFFF,kind==LandmineBlock.Kind.FAT?LegacyObjRenderer.Convention.FORGE:LegacyObjRenderer.Convention.HFR);
    }
    @Override public int getViewDistance(){return 256;}
    @Override public boolean shouldRenderOffScreen(LandmineBlockEntity mine){return ((LandmineBlock)mine.getBlockState().getBlock()).kind()==LandmineBlock.Kind.NAVAL;}
}
