package com.hbm.client.reactor.rbmk.thermal;
import com.google.gson.*;
import com.hbm.client.reactor.rbmk.column.RbmkPassiveModel;
import com.hbm.client.reactor.rbmk.control.RbmkPipedGeometry;
import com.hbm.machine.reactor.rbmk.column.RbmkColumnBlock;
import com.hbm.multiblock.DummyableBlock;
import java.util.*;
import java.util.function.Function;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.*;
import net.neoforged.neoforge.client.model.geometry.*;
/** Original heater renders its four pipes only without a lid, otherwise its actual glass/steel quarter-cube cover. */
public record RbmkHeaterGeometry(boolean inventory,boolean top)implements IUnbakedGeometry<RbmkHeaterGeometry> {
    public static final IGeometryLoader<RbmkHeaterGeometry> LOADER=(JsonObject json,JsonDeserializationContext context)->new RbmkHeaterGeometry(json.has("inventory")&&json.get("inventory").getAsBoolean(),json.has("top")&&json.get("top").getAsBoolean());
    @Override public BakedModel bake(IGeometryBakingContext context,ModelBaker baker,Function<Material,TextureAtlasSprite> sprites,ModelState state,ItemOverrides overrides){var open=new RbmkPipedGeometry(inventory,top,false).bake(context,baker,sprites,state,overrides);if(inventory||!top)return open;var closed=new RbmkPassiveModel(new RbmkPipedGeometry(false,false,false).bake(context,baker,sprites,state,overrides),sprites,"rbmk_heater");return new CoverChoice(open,closed);}
    private static final class CoverChoice extends BakedModelWrapper<BakedModel>{private static final ModelProperty<Boolean> CLOSED=new ModelProperty<>();private final BakedModel closed;CoverChoice(BakedModel open,BakedModel closed){super(open);this.closed=closed;}
        @Override public ModelData getModelData(BlockAndTintGetter world,BlockPos pos,BlockState state,ModelData data){boolean hasLid=false;if(state.getBlock() instanceof RbmkColumnBlock block){var core=block.core(world,pos);hasLid=core!=null&&core.getBlockState().getValue(DummyableBlock.LEGACY_META)!=12;}return closed.getModelData(world,pos,state,data).derive().with(CLOSED,hasLid).build();}
        @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random){return getQuads(state,side,random,ModelData.EMPTY,null);}
        @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random,ModelData data,RenderType layer){return Boolean.TRUE.equals(data.get(CLOSED))?closed.getQuads(state,side,random,data,layer):originalModel.getQuads(state,side,random,data,layer);}
    }
}
