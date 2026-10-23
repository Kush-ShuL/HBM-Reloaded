package com.hbm.client.railway.track;

import com.hbm.client.model.LegacyObjModels;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.railway.track.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.*;
import net.neoforged.neoforge.common.util.TriState;

/** Original RenderRail/ObjUtil authored mesh, atlas UVs, source normals and per-rail inventory matrices. */
final class SourceRailBakedModel extends BakedModelWrapper<BakedModel> {
    private static final ModelProperty<Boolean> SWITCHED=new ModelProperty<>();
    private record Key(int metadata,boolean switched){}
    private final SourceRailKind kind;private final boolean inventory;
    private final TextureAtlasSprite rail,sign;private final Map<Key,List<BakedQuad>> cache=new ConcurrentHashMap<>();
    SourceRailBakedModel(BakedModel original,SourceRailKind kind,boolean inventory,Function<Material,TextureAtlasSprite> textures){super(original);this.kind=kind;this.inventory=inventory;rail=textures.apply(new Material(TextureAtlas.LOCATION_BLOCKS,SourceRailContent.id("block/"+kind.texture)));sign=kind.isSwitch()?textures.apply(new Material(TextureAtlas.LOCATION_BLOCKS,SourceRailContent.id("block/rail_switch_sign"+(kind==SourceRailKind.SWITCH_FLIPPED?"_flipped":"")))):rail;}
    @Override public boolean isCustomRenderer(){return false;}
    @Override public boolean isGui3d(){return true;}
    @Override public boolean useAmbientOcclusion(){return false;}
    @Override public TriState useAmbientOcclusion(BlockState state,ModelData data,RenderType type){return TriState.FALSE;}
    @Override public List<BakedModel> getRenderPasses(ItemStack stack,boolean fabulous){return List.of(this);}
    @Override public List<RenderType> getRenderTypes(ItemStack stack,boolean fabulous){return List.of(RenderType.entityCutout(TextureAtlas.LOCATION_BLOCKS));}
    @Override public ChunkRenderTypeSet getRenderTypes(BlockState state,RandomSource random,ModelData data){return ChunkRenderTypeSet.of(RenderType.cutout());}
    @Override public BakedModel applyTransform(ItemDisplayContext context,PoseStack pose,boolean left){int hand=context==ItemDisplayContext.FIRST_PERSON_LEFT_HAND||context==ItemDisplayContext.THIRD_PERSON_LEFT_HAND?-1:1;
        // Source RenderRail is a 3D block inventory renderer; it is not ItemRenderBase.
        if(context.firstPerson()){pose.mulPose(Axis.YP.rotationDegrees(hand*45));pose.scale(.4F,.4F,.4F);}
        else if(context==ItemDisplayContext.THIRD_PERSON_LEFT_HAND||context==ItemDisplayContext.THIRD_PERSON_RIGHT_HAND){pose.translate(-hand/16D,-.125,.625);pose.mulPose(Axis.YP.rotationDegrees(-180));pose.mulPose(Axis.XP.rotationDegrees(90));pose.translate(-hand/16D,.4375,.0625);pose.translate(0,.1875,-.3125);pose.mulPose(Axis.XP.rotationDegrees(20));pose.mulPose(Axis.YP.rotationDegrees(hand*45));pose.scale(-.375F,-.375F,.375F);}
        else getTransforms().getTransform(context).apply(left,pose);return this;
    }
    @Override public ModelData getModelData(BlockAndTintGetter level,BlockPos pos,BlockState state,ModelData data){return kind.isSwitch()?data.derive().with(SWITCHED,level.getBlockEntity(pos) instanceof RailSwitchEntity sw&&sw.isSwitched).build():data;}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random){return getQuads(state,side,random,ModelData.EMPTY,null);}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random,ModelData data,RenderType layer){if(side!=null)return List.of();int meta=inventory||state==null?-1:state.getValue(DummyableBlock.LEGACY_META);return cache.computeIfAbsent(new Key(meta,Boolean.TRUE.equals(data.get(SWITCHED))),this::build);}
    private List<BakedQuad> build(Key key){int meta=key.metadata;if(meta>=0&&meta<12&&kind!=SourceRailKind.NARROW_STRAIGHT)return List.of(); // NarrowStraight really did render at every dummy, source bug retained.
        boolean item=meta<0;var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),SourceRailContent.id("models/blocks/"+kind.model+".obj"));var pose=new PoseStack();pose.translate(.5,item?.5:0,.5);
        float angle=0;
        if(item)SourceRailItemMatrices.apply(kind,pose);
        else {
            if(kind==SourceRailKind.NARROW_STRAIGHT){if(meta==2||meta==3||meta==12||meta==13)angle=90F/180F*(float)Math.PI;}
            else if(kind==SourceRailKind.NARROW_CURVE){if(meta==12)angle=90F/180F*(float)Math.PI;if(meta==14)angle=180F/180F*(float)Math.PI;if(meta==13)angle=270F/180F*(float)Math.PI;}
            else if(kind==SourceRailKind.STRAIGHT||kind==SourceRailKind.SHORT){if(meta==14||meta==15)angle=90F/180F*(float)Math.PI;}
            else {if(meta==15)angle=90F/180F*(float)Math.PI;if(meta==12)angle=180F/180F*(float)Math.PI;if(meta==14)angle=270F/180F*(float)Math.PI;}
            if(kind==SourceRailKind.STRAIGHT||kind==SourceRailKind.SHORT||kind==SourceRailKind.RAMP||kind==SourceRailKind.BUFFER||kind.isSwitch()){if(meta==12)pose.translate(.5,0,0);if(meta==13)pose.translate(-.5,0,0);if(meta==14)pose.translate(0,0,-.5);if(meta==15)pose.translate(0,0,.5);}
        }
        float cos=Mth.cos(angle),sin=Mth.sin(angle);var result=new ArrayList<BakedQuad>();
        for(var triangle:mesh.triangles()){
            var face=mesh.faces().get(triangle.face());boolean signFace=false;
            if(kind.isSwitch()){String part=face.legacyPart();if(!part.equals("Rail")){if(item||!part.equals(key.switched?"SignTurn":"SignStraight"))continue;signFace=true;}}
            var sprite=signFace?sign:rail;var n=face.normal();float nx=n.x()*cos+n.z()*sin,ny=n.y(),nz=n.z()*cos-n.x()*sin;
            float brightness=item?1F:kind.isSwitch()?Math.max(.45F,ny*.3F+.7F-Math.abs(nx)*.1F+Math.abs(nz)*.1F):Math.max(.45F,(ny+.7F)*.9F-Math.abs(nx)*.1F+Math.abs(nz)*.1F);
            int shade=Math.clamp((int)(brightness*255),0,255),argb=0xff000000|shade<<16|shade<<8|shade;
            var normal=new org.joml.Vector3f(nx,ny,nz).mul(pose.last().normal());normal.normalize();int packed=((byte)(normal.x*127)&255)|((byte)(normal.y*127)&255)<<8|((byte)(normal.z*127)&255)<<16;
            int[] vertices=new int[32],corners={triangle.first(),triangle.second(),triangle.third(),triangle.third()};
            for(int i=0;i<4;i++){var corner=face.corners().get(corners[i]);var source=mesh.positions().get(corner.position());var uv=mesh.textureCoordinates().get(corner.texture());var vertex=new org.joml.Vector3f(source.x()*cos+source.z()*sin,source.y(),source.z()*cos-source.x()*sin).mulPosition(pose.last().pose());int j=i*8;vertices[j]=Float.floatToRawIntBits(vertex.x);vertices[j+1]=Float.floatToRawIntBits(vertex.y);vertices[j+2]=Float.floatToRawIntBits(vertex.z);vertices[j+3]=argb;vertices[j+4]=Float.floatToRawIntBits(sprite.getU(uv.u()));vertices[j+5]=Float.floatToRawIntBits(sprite.getV(1F-uv.v()));vertices[j+6]=0;vertices[j+7]=packed;}
            result.add(new BakedQuad(vertices,-1,Direction.getNearest(normal.x,normal.y,normal.z),sprite,false));
        }return List.copyOf(result);
    }
}
