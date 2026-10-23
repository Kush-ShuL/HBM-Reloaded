package com.hbm.client.conveyor.crane;

import com.hbm.conveyor.crane.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.*;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;

/** Actual19-icon BlockCraneBase/RenderBlockSideRotation cube, with source per-face UV rotation and inventory RY90. */
public final class CraneBakedModel extends BakedModelWrapper<BakedModel> {
    private static final ModelProperty<Integer> OUTPUT=new ModelProperty<>();
    private record Key(int metadata,int output,boolean inventory){}
    private final CraneBaseBlock crane;private final TextureAtlasSprite[] textures;private final Map<Key,List<BakedQuad>> cache=new ConcurrentHashMap<>();
    public CraneBakedModel(BakedModel original,CraneBaseBlock crane,Function<Material,TextureAtlasSprite> getter){super(original);this.crane=crane;var names=crane.sourceTextures();textures=new TextureAtlasSprite[names.length];for(int i=0;i<names.length;i++)textures[i]=getter.apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.parse("hbm:block/"+names[i])));}
    /** Source RenderBlockSideRotation returns true for 3D items: old block-item caller, distinct from ItemRenderBase. */
    @Override public BakedModel applyTransform(net.minecraft.world.item.ItemDisplayContext context,com.mojang.blaze3d.vertex.PoseStack poses,boolean left){
        int hand=context==net.minecraft.world.item.ItemDisplayContext.FIRST_PERSON_LEFT_HAND||context==net.minecraft.world.item.ItemDisplayContext.THIRD_PERSON_LEFT_HAND?-1:1;
        if(context.firstPerson()){poses.mulPose(com.mojang.math.Axis.YP.rotationDegrees(hand*45));poses.scale(.4F,.4F,.4F);}
        else if(context==net.minecraft.world.item.ItemDisplayContext.THIRD_PERSON_LEFT_HAND||context==net.minecraft.world.item.ItemDisplayContext.THIRD_PERSON_RIGHT_HAND){
            poses.translate(-hand/16D,-.125,.625);poses.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-180));poses.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90));
            poses.translate(-hand/16D,.4375,.0625);poses.translate(0,.1875,-.3125);poses.mulPose(com.mojang.math.Axis.XP.rotationDegrees(20));
            poses.mulPose(com.mojang.math.Axis.YP.rotationDegrees(hand*45));poses.scale(-.375F,-.375F,.375F);
        }else getTransforms().getTransform(context).apply(left,poses);
        return this;
    }
    @Override public boolean isCustomRenderer(){return false;}@Override public boolean isGui3d(){return true;}@Override public boolean useAmbientOcclusion(){return true;}
    @Override public List<BakedModel> getRenderPasses(ItemStack stack,boolean fabulous){return List.of(this);}@Override public List<RenderType> getRenderTypes(ItemStack stack,boolean fabulous){return List.of(RenderType.entitySolid(TextureAtlas.LOCATION_BLOCKS));}@Override public ChunkRenderTypeSet getRenderTypes(BlockState state,RandomSource random,ModelData data){return ChunkRenderTypeSet.of(RenderType.solid());}
    @Override public ModelData getModelData(BlockAndTintGetter world,BlockPos pos,BlockState state,ModelData data){var output=world.getBlockEntity(pos)instanceof CraneBaseEntity entity?entity.getOutputOverride():null;return data.derive().with(OUTPUT,output==null?6:output.get3DDataValue()).build();}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random){return getQuads(state,side,random,ModelData.EMPTY,null);}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random,ModelData data,RenderType layer){boolean inventory=state==null;if(!inventory&&side==null)return List.of();int meta=inventory?0:state.getValue(CraneBaseBlock.META),output=data.has(OUTPUT)?data.get(OUTPUT):6;var quads=cache.computeIfAbsent(new Key(meta,output,inventory),this::bake);if(inventory)return side==null?quads:List.of();return quads.stream().filter(quad->quad.getDirection()==side).toList();}
    private List<BakedQuad> bake(Key key){var out=new ArrayList<BakedQuad>(6);for(var side:Direction.values()){
        int index=key.inventory?crane.inventoryIconIndex(side.get3DDataValue(),key.metadata):crane.worldIconIndex(key.metadata,key.output,side.get3DDataValue());int rotation=key.inventory?0:crane.sourceRotation(key.metadata,key.output,side.get3DDataValue());var sprite=textures[index];var vertices=vertices(side);Direction normal=key.inventory?switch(side){case NORTH->Direction.WEST;case SOUTH->Direction.EAST;case WEST->Direction.SOUTH;case EAST->Direction.NORTH;default->side;}:side;var quad=new QuadBakingVertexConsumer();quad.setSprite(sprite);quad.setDirection(normal);quad.setShade(true);quad.setHasAmbientOcclusion(!key.inventory);
        for(var vertex:vertices){double x=vertex[0],y=vertex[1],z=vertex[2],u=vertex[3],v=vertex[4];if(rotation==1){double old=u;u=v;v=1-old;}else if(rotation==2){double old=u;u=1-v;v=old;}else if(rotation==3){u=1-u;v=1-v;}if(key.inventory){double old=x;x=z;z=1-old;}quad.addVertex((float)x,(float)y,(float)z).setColor(255,255,255,255).setUv(sprite.getU((float)u),sprite.getV((float)v)).setLight(0).setNormal(normal.getStepX(),normal.getStepY(),normal.getStepZ());}out.add(quad.bakeQuad());
    }return List.copyOf(out);}
    /** Mojang1.7.10 RenderBlocks full bounds; world here is vanilla RenderBlocks, not RenderBlocksNT's separate east fix. */
    private static double[][] vertices(Direction side){return switch(side){
        case DOWN->new double[][]{{0,0,1,0,1},{0,0,0,0,0},{1,0,0,1,0},{1,0,1,1,1}};
        case UP->new double[][]{{1,1,1,1,1},{1,1,0,1,0},{0,1,0,0,0},{0,1,1,0,1}};
        case NORTH->new double[][]{{0,1,0,1,0},{1,1,0,0,0},{1,0,0,0,1},{0,0,0,1,1}};
        case SOUTH->new double[][]{{0,1,1,0,0},{0,0,1,0,1},{1,0,1,1,1},{1,1,1,1,0}};
        case WEST->new double[][]{{0,1,1,1,0},{0,1,0,0,0},{0,0,0,0,1},{0,0,1,1,1}};
        case EAST->new double[][]{{1,0,1,0,1},{1,0,0,1,1},{1,1,0,1,0},{1,1,1,0,0}};
    };}
}
