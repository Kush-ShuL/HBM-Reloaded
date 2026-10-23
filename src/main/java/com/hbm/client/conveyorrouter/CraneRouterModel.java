package com.hbm.client.conveyorrouter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.*;
import java.util.function.Function;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;

/** Original RenderBlockMultipass: all base faces, six same-plane tinted overlay passes; inventory is base-only. */
public final class CraneRouterModel extends BakedModelWrapper<BakedModel> {
    private final List<BakedQuad> world,item;
    public CraneRouterModel(BakedModel original,Function<Material,TextureAtlasSprite> getter){
        super(original);var base=getter.apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.parse("hbm:block/crane_in")));var overlay=getter.apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.parse("hbm:block/crane_router_overlay")));
        var world=new ArrayList<BakedQuad>(12);var item=new ArrayList<BakedQuad>(6);
        for(var face:Direction.values()){world.add(quad(base,face,false,-1));item.add(quad(base,face,true,-1));}
        for(var face:Direction.values())world.add(quad(overlay,face,false,face.get3DDataValue()));
        this.world=List.copyOf(world);this.item=List.copyOf(item);
    }
    @Override public boolean isCustomRenderer(){return false;}@Override public boolean isGui3d(){return true;}@Override public boolean useAmbientOcclusion(){return true;}
    @Override public List<BakedModel> getRenderPasses(ItemStack stack,boolean fabulous){return List.of(this);}
    @Override public List<RenderType> getRenderTypes(ItemStack stack,boolean fabulous){return List.of(RenderType.entityCutout(TextureAtlas.LOCATION_BLOCKS));}
    @Override public ChunkRenderTypeSet getRenderTypes(BlockState state,RandomSource random,ModelData data){return ChunkRenderTypeSet.of(RenderType.cutout());}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random){return getQuads(state,side,random,ModelData.EMPTY,null);}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random,ModelData data,RenderType layer){return side==null?(state==null?item:world):List.of();}
    @Override public BakedModel applyTransform(ItemDisplayContext context,PoseStack poses,boolean left){
        int hand=context==ItemDisplayContext.FIRST_PERSON_LEFT_HAND||context==ItemDisplayContext.THIRD_PERSON_LEFT_HAND?-1:1;
        if(context.firstPerson()){poses.mulPose(Axis.YP.rotationDegrees(hand*45));poses.scale(.4F,.4F,.4F);}
        else if(context==ItemDisplayContext.THIRD_PERSON_LEFT_HAND||context==ItemDisplayContext.THIRD_PERSON_RIGHT_HAND){poses.translate(-hand/16D,-.125,.625);poses.mulPose(Axis.YP.rotationDegrees(-180));poses.mulPose(Axis.XP.rotationDegrees(90));poses.translate(-hand/16D,.4375,.0625);poses.translate(0,.1875,-.3125);poses.mulPose(Axis.XP.rotationDegrees(20));poses.mulPose(Axis.YP.rotationDegrees(hand*45));poses.scale(-.375F,-.375F,.375F);}
        else getTransforms().getTransform(context).apply(left,poses);return this;
    }
    private static BakedQuad quad(TextureAtlasSprite sprite,Direction face,boolean inventory,int tint){
        var normal=inventory?switch(face){case NORTH->Direction.WEST;case SOUTH->Direction.EAST;case WEST->Direction.SOUTH;case EAST->Direction.NORTH;default->face;}:face;
        var output=new QuadBakingVertexConsumer();output.setSprite(sprite);output.setDirection(normal);output.setShade(true);output.setHasAmbientOcclusion(!inventory);output.setTintIndex(tint);
        for(var vertex:vertices(face)){double x=vertex[0],y=vertex[1],z=vertex[2];if(inventory){double old=x;x=z;z=1-old;}output.addVertex((float)x,(float)y,(float)z).setColor(255,255,255,255).setUv(sprite.getU((float)vertex[3]),sprite.getV((float)vertex[4])).setLight(0).setNormal(normal.getStepX(),normal.getStepY(),normal.getStepZ());}
        return output.bakeQuad();
    }
    /** Actual full-bound RenderBlocks UVs; no invented polygon offset, translucency or face culling. */
    private static double[][] vertices(Direction side){return switch(side){
        case DOWN->new double[][]{{0,0,1,0,1},{0,0,0,0,0},{1,0,0,1,0},{1,0,1,1,1}};
        case UP->new double[][]{{1,1,1,1,1},{1,1,0,1,0},{0,1,0,0,0},{0,1,1,0,1}};
        case NORTH->new double[][]{{0,1,0,1,0},{1,1,0,0,0},{1,0,0,0,1},{0,0,0,1,1}};
        case SOUTH->new double[][]{{0,1,1,0,0},{0,0,1,0,1},{1,0,1,1,1},{1,1,1,1,0}};
        case WEST->new double[][]{{0,1,1,1,0},{0,1,0,0,0},{0,0,0,0,1},{0,0,1,1,1}};
        case EAST->new double[][]{{1,0,1,0,1},{1,0,0,1,1},{1,1,0,1,0},{1,1,1,0,0}};
    };}
}
