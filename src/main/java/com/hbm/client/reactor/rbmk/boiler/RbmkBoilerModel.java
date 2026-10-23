package com.hbm.client.reactor.rbmk.boiler;
import com.hbm.client.reactor.rbmk.column.RbmkPassiveModel;
import com.hbm.machine.reactor.rbmk.boiler.RbmkBoilerBlock;
import com.hbm.multiblock.DummyableBlock;
import java.util.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.*;
/** Source covered boilers suppress all four top pipes; cover geometry reuses the genuine common quarter-block kernel. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class RbmkBoilerModel extends BakedModelWrapper<BakedModel> {
    private static final ModelProperty<Boolean> COVERED=new ModelProperty<>();private final RbmkPassiveModel covered;
    private RbmkBoilerModel(BakedModel uncovered,RbmkPassiveModel covered){super(uncovered);this.covered=covered;}
    @SubscribeEvent public static void bake(ModelEvent.ModifyBakingResult event){var id=ResourceLocation.parse("hbm:rbmk_boiler");var base=event.getModels().get(new ModelResourceLocation(id,"legacy_meta=12"));if(base==null)return;var covered=new RbmkPassiveModel(base,event.getTextureGetter(),"rbmk_boiler");event.getModels().replaceAll((key,model)->key.id().equals(id)&&!key.getVariant().equals("inventory")?new RbmkBoilerModel(model,covered):model);}
    @Override public ModelData getModelData(BlockAndTintGetter world,BlockPos pos,BlockState state,ModelData data){boolean lid=false;int meta=state.getValue(DummyableBlock.LEGACY_META);if(meta>=6&&meta<12&&state.getBlock() instanceof RbmkBoilerBlock block){var core=block.core(world,pos);lid=core!=null&&core.hasLid();}return covered.getModelData(world,pos,state,data).derive().with(COVERED,lid).build();}
    @Override public ChunkRenderTypeSet getRenderTypes(BlockState state,RandomSource random,ModelData data){return Boolean.TRUE.equals(data.get(COVERED))?covered.getRenderTypes(state,random,data):originalModel.getRenderTypes(state,random,data);}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random){return getQuads(state,side,random,ModelData.EMPTY,null);}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random,ModelData data,RenderType layer){return Boolean.TRUE.equals(data.get(COVERED))?covered.getQuads(state,side,random,data,layer):originalModel.getQuads(state,side,random,data,layer);}
}
