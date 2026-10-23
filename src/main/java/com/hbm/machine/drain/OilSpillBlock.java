package com.hbm.machine.drain;
import com.hbm.block.fluid.LegacyMaterials;
import com.hbm.machine.reactor.rbmk.residue.RbmkResidueBlock;
import com.hbm.machine.reactor.zirnox.ZirnoxDestroyedBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.*;
/** The actual source BlockLayering oil variant; height(meta&7)+1, collision(meta&7), no drops or melting. */
public class OilSpillBlock extends Block {
 public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
 public OilSpillBlock(){this(Properties.of().strength(.1F).sound(SoundType.SNOW).noOcclusion().replaceable().noLootTable().dynamicShape());}
 protected OilSpillBlock(Properties properties){super(properties);registerDefaultState(stateDefinition.any().setValue(META,0));}
 @Override protected MapCodec<? extends Block> codec(){return MapCodec.unit(this);}
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
 @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return Block.box(0,0,0,16,((state.getValue(META)&7)+1)*2,16);}
 @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){int height=(state.getValue(META)&7)*2;return height==0?Shapes.empty():Block.box(0,0,0,16,height,16);}
 @Override protected boolean canSurvive(BlockState state,LevelReader level,BlockPos pos){var below=pos.below();var support=level.getBlockState(below);if(support.getBlock() instanceof RbmkResidueBlock||support.getBlock() instanceof ZirnoxDestroyedBlock)return true;if(support.is(Blocks.ICE)||support.is(Blocks.PACKED_ICE))return false;if(support.is(BlockTags.LEAVES)||support.is(this)&&(support.getValue(META)&7)==7)return true;return support.canOcclude()&&support.isSolidRender(level,below)&&LegacyMaterials.of(support).blocksMovement();}
 @Override protected BlockState updateShape(BlockState state,Direction side,BlockState neighbor,LevelAccessor level,BlockPos pos,BlockPos next){return state.canSurvive(level,pos)?state:Blocks.AIR.defaultBlockState();}
 @Override protected boolean skipRendering(BlockState state,BlockState neighbor,Direction side){return side==Direction.UP?false:super.skipRendering(state,neighbor,side);}
 @Override protected int getLightBlock(BlockState state,BlockGetter level,BlockPos pos){return 0;}
}
