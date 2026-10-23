package com.hbm.client.structuretandem;

import com.google.gson.Gson;
import com.hbm.item.structuretandem.TandemBlock;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;

/** The actual RenderBlocks face corners/UVs, compiled from the official old renderer by this module's writer. */
public final class TandemModel extends BakedModelWrapper<BakedModel> {
    private static final double[][][][] CORNERS=readCorners();
    private final boolean inventory;
    private final TextureAtlasSprite[] sprites;
    private final java.util.function.ToIntBiFunction<BlockState,Direction> sourceTexture,sourceRotation;
    private final Map<Integer,List<BakedQuad>> faces=new java.util.concurrent.ConcurrentHashMap<>();
    TandemModel(BakedModel parent,boolean inventory,TextureAtlasSprite[] sprites){this(parent,inventory,sprites,null,null);}
    private TandemModel(BakedModel parent,boolean inventory,TextureAtlasSprite[] sprites,java.util.function.ToIntBiFunction<BlockState,Direction> texture,java.util.function.ToIntBiFunction<BlockState,Direction> rotation){super(parent);this.inventory=inventory;this.sprites=sprites.clone();sourceTexture=texture;sourceRotation=rotation;}
    /** Other genuine IBlockSideRotation hosts select materials/flags while keeping this ONE source face kernel. */
    public static BakedModel sourceFaces(BakedModel parent,boolean inventory,TextureAtlasSprite[] sprites,java.util.function.ToIntBiFunction<BlockState,Direction> texture,java.util.function.ToIntBiFunction<BlockState,Direction> rotation){return new TandemModel(parent,inventory,sprites,java.util.Objects.requireNonNull(texture),java.util.Objects.requireNonNull(rotation));}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random){return getQuads(state,side,random,ModelData.EMPTY,null);}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random,ModelData data,RenderType layer){
        if(side==null)return List.of();
        if(sourceTexture!=null){int texture=sourceTexture.applyAsInt(state,side),flag=inventory?0:sourceRotation.applyAsInt(state,side);return faces.computeIfAbsent((side.get3DDataValue()*4+flag)*sprites.length+texture,key->List.of(quad(side,texture,flag)));}
        int facing=inventory||state==null?0:state.getValue(TandemBlock.FACING).get3DDataValue(),face=side.get3DDataValue();
        return faces.computeIfAbsent(facing*6+face,key->List.of(quad(facing,side)));
    }
    private BakedQuad quad(int facing,Direction side){
        int face=side.get3DDataValue(),opposite=Direction.from3DDataValue(facing).getOpposite().get3DDataValue();
        int texture=face==facing?0:face==opposite?3:face<=1||face>3&&facing<=1?1:2;
        // Source inventory renderer never set the world's six uvRotate fields.
        return quad(side,texture,inventory?0:rotation(facing,face));
    }
    private BakedQuad quad(Direction side,int texture,int flag){
        var points=CORNERS[side.get3DDataValue()][flag];var sprite=sprites[texture];
        int stride=DefaultVertexFormat.BLOCK.getVertexSize()/4;int[] vertices=new int[stride*4];
        int normal=(side.getStepX()*127&255)|((side.getStepY()*127&255)<<8)|((side.getStepZ()*127&255)<<16);
        for(int i=0;i<4;i++){int at=i*stride;var point=points[i];
            vertices[at]=Float.floatToRawIntBits((float)point[0]);vertices[at+1]=Float.floatToRawIntBits((float)point[1]);vertices[at+2]=Float.floatToRawIntBits((float)point[2]);
            vertices[at+3]=-1;vertices[at+4]=Float.floatToRawIntBits(sprite.getU((float)point[3]));vertices[at+5]=Float.floatToRawIntBits(sprite.getV((float)point[4]));vertices[at+6]=0;vertices[at+7]=normal;
        }
        return new BakedQuad(vertices,-1,side,sprite,true);
    }
    private static int rotation(int facing,int side){
        if(side==0){int top=rotation(facing,1);return top==1?2:top==2?1:top;}
        if(side==facing||side==Direction.from3DDataValue(facing).getOpposite().get3DDataValue())return 0;
        if(facing==0)return 0;if(facing==1)return 3;
        if(side==1)return switch(facing){case 2->3;case 3->0;case 4->1;default->2;};
        return switch(facing){case 2->side==4?2:1;case 3->side==4?1:2;case 4->side==2?1:2;case 5->side==2?2:1;default->0;};
    }
    private static double[][][][] readCorners(){
        try(var input=TandemModel.class.getResourceAsStream("/hbm/tandem-side-uv335.json")){
            if(input==null)throw new FileNotFoundException("Actual source tandem face table");
            return new Gson().fromJson(new InputStreamReader(input,StandardCharsets.UTF_8),double[][][][].class);
        }catch(IOException failure){throw new UncheckedIOException(failure);}
    }
}
