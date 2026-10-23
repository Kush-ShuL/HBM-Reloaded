package com.hbm.machine.foundry.client;
import com.hbm.machine.foundry.FoundryContent.Kind;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.*;
import java.util.function.Function;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
/** Source renderInventoryBlock faces, including four-arm channel and open internal tank walls. */
public final class FoundryInventoryModel extends BakedModelWrapper<BakedModel>{
 private final List<BakedQuad> quads;
 public FoundryInventoryModel(BakedModel base,Kind kind,Function<Material,TextureAtlasSprite> sprites){super(base);var built=new ArrayList<BakedQuad>();for(var face:FoundryGeometry.build(kind,null,net.minecraft.core.BlockPos.ZERO,0,true)){var sprite=sprites.apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.fromNamespaceAndPath("hbm","block/"+face.texture())));int[] vertices=new int[32];int normal=((byte)(face.nx()*127)&255)|((byte)(face.ny()*127)&255)<<8|((byte)(face.nz()*127)&255)<<16;for(int i=0;i<4;i++){var point=face.vertices().get(i);int offset=i*8;vertices[offset]=Float.floatToRawIntBits(point.x());vertices[offset+1]=Float.floatToRawIntBits(point.y());vertices[offset+2]=Float.floatToRawIntBits(point.z());vertices[offset+3]=0xFFFFFFFF;vertices[offset+4]=Float.floatToRawIntBits(sprite.getU(point.u()));vertices[offset+5]=Float.floatToRawIntBits(sprite.getV(point.v()));vertices[offset+6]=0;vertices[offset+7]=normal;}built.add(new BakedQuad(vertices,-1,Direction.getNearest(face.nx(),face.ny(),face.nz()),sprite,false));}quads=List.copyOf(built);}
 @Override public boolean useAmbientOcclusion(){return false;}@Override public boolean isGui3d(){return true;}@Override public boolean isCustomRenderer(){return false;}
 @Override public BakedModel applyTransform(ItemDisplayContext context,PoseStack poses,boolean left){getTransforms().getTransform(context).apply(left,poses);return this;}
 @Override public List<BakedModel> getRenderPasses(ItemStack stack,boolean fabulous){return List.of(this);}
 @Override public List<RenderType> getRenderTypes(ItemStack stack,boolean fabulous){return List.of(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS));}
 @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random){return side==null?quads:List.of();}
 @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random,ModelData data,RenderType type){return getQuads(state,side,random);}
}
