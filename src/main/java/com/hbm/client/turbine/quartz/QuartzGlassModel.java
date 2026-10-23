package com.hbm.client.turbine.quartz;
import com.hbm.machine.turbine.quartz.QuartzGlassBlock;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

/** Original CTContext and IconCT, including exact face orientation and 20 atlas fragments. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class QuartzGlassModel extends BakedModelWrapper<BakedModel> {
    private static final ModelProperty<int[]> CONTEXT = new ModelProperty<>();
    private static final Direction[][] ORIENTATION = {
        {Direction.SOUTH,Direction.WEST},{Direction.NORTH,Direction.WEST},
        {Direction.UP,Direction.EAST},{Direction.UP,Direction.WEST},
        {Direction.UP,Direction.NORTH},{Direction.UP,Direction.SOUTH}};
    private final TextureAtlasSprite full, connected;
    private final Predicate<BlockState> connection;
    private final Map<Integer,List<BakedQuad>> faceCache = new ConcurrentHashMap<>();
    public QuartzGlassModel(BakedModel parent,TextureAtlasSprite full,TextureAtlasSprite connected) { this(parent,full,connected,null); }
    /** Original IBlockCT consumers may connect to more than the same block, without duplicating CTContext/IconCT. */
    public QuartzGlassModel(BakedModel parent,TextureAtlasSprite full,TextureAtlasSprite connected,Predicate<BlockState> connection) { super(parent);this.full=full;this.connected=connected;this.connection=connection; }
    @SubscribeEvent public static void bake(ModelEvent.ModifyBakingResult event) {
        Map<String,QuartzGlassModel> models = new java.util.HashMap<>();
        event.getModels().replaceAll((key,model)->{
            String name=key.id().getPath();
            if(!key.id().getNamespace().equals("hbm") || !name.equals("glass_quartz") || key.getVariant().equals("inventory"))return model;
            return models.computeIfAbsent(name,ignored->new QuartzGlassModel(model,
                event.getTextureGetter().apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.fromNamespaceAndPath("hbm","block/"+name))),
                event.getTextureGetter().apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.fromNamespaceAndPath("hbm","block/"+name+"_ct")))));
        });
    }
    @Override public ModelData getModelData(BlockAndTintGetter world,BlockPos pos,BlockState state,ModelData data) {
        int[] faces=new int[24];
        var block=state.getBlock();
        Predicate<BlockState> matches=connection==null?candidate->candidate.is(block):connection;
        for(Direction side:Direction.values()) {
            Direction up=ORIENTATION[side.ordinal()][0],left=ORIENTATION[side.ordinal()][1];
            boolean t=matches.test(world.getBlockState(pos.relative(up))),b=matches.test(world.getBlockState(pos.relative(up.getOpposite())));
            boolean l=matches.test(world.getBlockState(pos.relative(left))),r=matches.test(world.getBlockState(pos.relative(left.getOpposite())));
            int at=side.ordinal()*4;
            faces[at]=type(l,matches.test(world.getBlockState(pos.relative(up).relative(left))),t);
            faces[at+1]=1|type(r,matches.test(world.getBlockState(pos.relative(up).relative(left.getOpposite()))),t);
            faces[at+2]=2|type(l,matches.test(world.getBlockState(pos.relative(up.getOpposite()).relative(left))),b);
            faces[at+3]=3|type(r,matches.test(world.getBlockState(pos.relative(up.getOpposite()).relative(left.getOpposite()))),b);
        }
        return data.derive().with(CONTEXT,faces).build();
    }
    private static int type(boolean horizontal,boolean corner,boolean vertical) { return vertical?(horizontal?(corner?4:8):16):(horizontal?12:0); }
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random) { return getQuads(state,side,random,ModelData.EMPTY,null); }
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random,ModelData data,RenderType layer) {
        if(side==null)return List.of();
        int[] context=data.get(CONTEXT);int at=side.ordinal()*4;
        int key=side.ordinal();
        for(int i=0;i<4;i++)key=(key<<5)|(context==null?i:context[at+i]);
        return faceCache.computeIfAbsent(key,ignored->{
            List<BakedQuad> quads=new ArrayList<>(4);
            for(int quadrant=0;quadrant<4;quadrant++)quads.add(quad(side,quadrant,context==null?quadrant:context[at+quadrant]));
            return List.copyOf(quads);
        });
    }
    private BakedQuad quad(Direction side,int quadrant,int type) {
        Direction up=ORIENTATION[side.ordinal()][0],left=ORIENTATION[side.ordinal()][1];
        float du=(type<4?.5F:.25F),u=(type&1)*du+((type>=16||type>=8&&type<12)?2*du:0),v=((type&2)>>1)*du+((type>=8&&type<16)?2*du:0);
        float qx=(quadrant&1)*.5F,qy=((quadrant&2)>>1)*.5F;
        int stride=DefaultVertexFormat.BLOCK.getVertexSize()/4;int[] vertices=new int[4*stride];
        // TL, BL, BR, TR is outward-facing on all six original CTContext faces.
        float[][] points={{qx,qy},{qx,qy+.5F},{qx+.5F,qy+.5F},{qx+.5F,qy}};
        int normal=(side.getStepX()*127&255)|((side.getStepY()*127&255)<<8)|((side.getStepZ()*127&255)<<16);
        TextureAtlasSprite sprite=type<4?full:connected;
        for(int i=0;i<4;i++) {
            float a=points[i][0],b=points[i][1];int at=i*stride;
            vertices[at]=Float.floatToRawIntBits(.5F+side.getStepX()*.5F+left.getStepX()*(.5F-a)+up.getStepX()*(.5F-b));
            vertices[at+1]=Float.floatToRawIntBits(.5F+side.getStepY()*.5F+left.getStepY()*(.5F-a)+up.getStepY()*(.5F-b));
            vertices[at+2]=Float.floatToRawIntBits(.5F+side.getStepZ()*.5F+left.getStepZ()*(.5F-a)+up.getStepZ()*(.5F-b));
            vertices[at+3]=-1;vertices[at+4]=Float.floatToRawIntBits(sprite.getU(u+(i>=2?du:0)));vertices[at+5]=Float.floatToRawIntBits(sprite.getV(v+(i==1||i==2?du:0)));vertices[at+6]=0;vertices[at+7]=normal;
        }
        return new BakedQuad(vertices,-1,side,sprite,true);
    }
}
