package com.hbm.client.capacitors;
import com.hbm.bomb.nuclear.LegacyVector;
import com.hbm.client.model.LegacyObjModels;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.*;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
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
import net.neoforged.neoforge.common.util.TriState;
/** RenderCapacitor/ObjUtil.renderPartWithIcon, five original independent sprites and exact Z-flip/Y-yaw matrices. */
final class CapacitorModel extends BakedModelWrapper<BakedModel> {
 private final TextureAtlasSprite sprite;private final List<BakedQuad> quads;
 CapacitorModel(BakedModel original,String metal,int metadata,boolean inventory,Function<Material,TextureAtlasSprite> textures){super(original);String[] parts={"Top","Side","Bottom","InnerTop","InnerSide"},suffix={"top","side","bottom","inner_top","inner_side"};var sprites=new HashMap<String,TextureAtlasSprite>();for(int i=0;i<parts.length;i++)sprites.put(parts[i],textures.apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.parse("hbm:block/capacitor_"+metal+"_"+suffix[i]))));sprite=textures.apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.parse(metal.equals("gold")?"minecraft:block/gold_block":"hbm:block/block_"+metal)));var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.parse("hbm:models/blocks/capacitor.obj"));float yaw=0,flip=0;if(!inventory){if(metadata==0)flip=(float)Math.PI;if(metadata==2)yaw=(float)Math.PI/2;if(metadata==3)yaw=(float)Math.PI*1.5F;if(metadata==4)yaw=(float)Math.PI;if(yaw!=0||metadata==5)flip=(float)Math.PI/2;}var output=new ArrayList<BakedQuad>();
  for(var face:mesh.faces()){var icon=sprites.get(face.legacyPart());if(icon==null)continue;if(face.corners().size()!=3&&face.corners().size()!=4)throw new IllegalStateException("Source capacitor requires triangle/quad face at "+face.sourceLine());var n=face.normal();var normal=LegacyVector.createVectorHelper(n.x(),n.y(),n.z());normal.rotateAroundZ(flip);normal.rotateAroundY(yaw);float nx=(float)normal.xCoord,ny=(float)normal.yCoord,nz=(float)normal.zCoord;float brightness=inventory?1:Math.max(.45F,ny*.3F+.7F-(float)Math.abs(normal.xCoord)*.1F+(float)Math.abs(normal.zCoord)*.1F);int gray=Math.clamp((int)(brightness*255F),0,255),color=0xff000000|gray<<16|gray<<8|gray,packed=((byte)(nx*127)&255)|((byte)(ny*127)&255)<<8|((byte)(nz*127)&255)<<16;int[] data=new int[32];for(int i=0;i<4;i++){var corner=face.corners().get(Math.min(i,face.corners().size()-1));var p=mesh.positions().get(corner.position());var uv=mesh.textureCoordinates().get(corner.texture());var point=LegacyVector.createVectorHelper(p.x(),p.y(),p.z());point.rotateAroundZ(flip);point.rotateAroundY(yaw);int k=i*8;data[k]=Float.floatToRawIntBits((float)point.xCoord+.5F);data[k+1]=Float.floatToRawIntBits((float)point.yCoord+.5F);data[k+2]=Float.floatToRawIntBits((float)point.zCoord+.5F);data[k+3]=color;data[k+4]=Float.floatToRawIntBits(icon.getU(uv.u()));data[k+5]=Float.floatToRawIntBits(icon.getV(1F-uv.v()));data[k+6]=0;data[k+7]=packed;}output.add(new BakedQuad(data,-1,Direction.getNearest(nx,ny,nz),icon,false));}quads=List.copyOf(output);
 }
 @Override public ItemOverrides getOverrides(){return ItemOverrides.EMPTY;}
    @Override public boolean useAmbientOcclusion() { return false; }
    @Override public TriState useAmbientOcclusion(BlockState state, ModelData data, RenderType type) { return TriState.FALSE; }
    @Override public boolean isGui3d() { return true; }
    @Override public boolean isCustomRenderer() { return false; }
    @Override public TextureAtlasSprite getParticleIcon() { return sprite; }
    @Override public TextureAtlasSprite getParticleIcon(ModelData data) { return sprite; }
    @Override public BakedModel applyTransform(ItemDisplayContext context, PoseStack poses, boolean left) {
        int hand = context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ? -1 : 1;
        if (context.firstPerson()) { poses.mulPose(com.mojang.math.Axis.YP.rotationDegrees(hand * 45)); poses.scale(.4F, .4F, .4F); }
        else if (context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            // Source custom ISBRH returns true from shouldRender3DInInventory, using the actual block-item caller.
            poses.translate(-hand / 16D, -.125, .625); poses.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-180)); poses.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90));
            poses.translate(-hand / 16D, .4375, .0625); poses.translate(0, .1875, -.3125); poses.mulPose(com.mojang.math.Axis.XP.rotationDegrees(20));
            poses.mulPose(com.mojang.math.Axis.YP.rotationDegrees(hand * 45)); poses.scale(-.375F, -.375F, .375F);
        } else getTransforms().getTransform(context).apply(left, poses);
        return this;
    }
    @Override public List<BakedModel> getRenderPasses(ItemStack stack, boolean fabulous) { return List.of(this); }
    @Override public List<RenderType> getRenderTypes(ItemStack stack, boolean fabulous) { return List.of(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS)); }
    @Override public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource random, ModelData data) { return ChunkRenderTypeSet.of(RenderType.cutout()); }
    @Override public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random) { return side == null ? quads : List.of(); }
    @Override public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random, ModelData data, RenderType type) { return getQuads(state, side, random); }
}
