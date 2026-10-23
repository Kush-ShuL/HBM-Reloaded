package com.hbm.machine.pedestal;
import com.hbm.hazard.HbmHazards;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.shapes.*;
/** Original BlockChain's ceiling/wall support priority, no collision, ladder behavior and end selection box. */
public final class SourceDungeonChainBlock extends Block {
    public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,5);
    public static final BooleanProperty END=BooleanProperty.create("chain_end");
    public SourceDungeonChainBlock(){super(Properties.of().strength(.25F,1.2F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops().dynamicShape());registerDefaultState(stateDefinition.any().setValue(META,0).setValue(END,false));}
    @Override protected MapCodec<? extends Block> codec(){return MapCodec.unit(this);}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META,END);}
    public BlockState fromLegacy(int metadata){return defaultBlockState().setValue(META,Math.floorMod(metadata,6));}
    private boolean solid(BlockGetter level,BlockPos pos,Direction face){return level.getBlockState(pos).isFaceSturdy(level,pos,face);}
    public boolean end(BlockState state,BlockGetter level,BlockPos pos){var below=level.getBlockState(pos.below());return !solid(level,pos.below(),Direction.UP)&&!(below.is(this)&&below.getValue(META).equals(state.getValue(META)));}
    private BlockState refresh(BlockState state,BlockGetter level,BlockPos pos){return state.setValue(END,end(state,level,pos));}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){
        var level=context.getLevel();var pos=context.getClickedPos();int metadata=context.getItemInHand().getOrDefault(HbmHazards.LEGACY_METADATA.get(),0),side=context.getClickedFace().get3DDataValue();
        if(side==2&&solid(level,pos.south(),Direction.NORTH))metadata=2;
        if(side==3&&solid(level,pos.north(),Direction.SOUTH))metadata=3;
        if(side==4&&solid(level,pos.east(),Direction.WEST))metadata=4;
        if(side==5&&solid(level,pos.west(),Direction.EAST))metadata=5;
        if(metadata==0){var above=level.getBlockState(pos.above());if(above.is(this))return refresh(fromLegacy(above.getValue(META)),level,pos);if(solid(level,pos.above(),Direction.DOWN))return refresh(fromLegacy(0),level,pos);}
        if(metadata==0){if(solid(level,pos.south(),Direction.NORTH))metadata=2;if(solid(level,pos.north(),Direction.SOUTH))metadata=3;if(solid(level,pos.east(),Direction.WEST))metadata=4;if(solid(level,pos.west(),Direction.EAST))metadata=5;}
        var state=refresh(fromLegacy(metadata),level,pos);return canSurvive(state,level,pos)?state:null;
    }
    @Override protected boolean canSurvive(BlockState state,LevelReader level,BlockPos pos){return solid(level,pos.above(),Direction.DOWN)||level.getBlockState(pos.above()).is(this)||solid(level,pos.west(),Direction.EAST)||solid(level,pos.east(),Direction.WEST)||solid(level,pos.north(),Direction.SOUTH)||solid(level,pos.south(),Direction.NORTH);}
    private boolean supported(BlockState state,BlockGetter level,BlockPos pos){var above=level.getBlockState(pos.above());int meta=state.getValue(META);if(above.is(this)&&above.getValue(META)==meta||meta==0&&solid(level,pos.above(),Direction.DOWN))return true;return switch(meta){case 2->solid(level,pos.south(),Direction.NORTH);case 3->solid(level,pos.north(),Direction.SOUTH);case 4->solid(level,pos.east(),Direction.WEST);case 5->solid(level,pos.west(),Direction.EAST);default->false;};}
    @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block neighbor,BlockPos from,boolean moving){if(!supported(state,level,pos))level.destroyBlock(pos,true);else{var updated=refresh(state,level,pos);if(updated!=state)level.setBlock(pos,updated,2);}}
    @Override protected BlockState updateShape(BlockState state,Direction side,BlockState neighbor,LevelAccessor level,BlockPos pos,BlockPos from){return refresh(state,level,pos);}
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return Shapes.empty();}
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){double y=end(state,level,pos)?.25:0;return switch(state.getValue(META)){case 0->Shapes.box(.375,y,.375,.625,1,.625);case 2->Shapes.box(.375,y,.875,.625,1,1);case 3->Shapes.box(.375,y,0,.625,1,.125);case 4->Shapes.box(.875,y,.375,1,1,.625);case 5->Shapes.box(0,y,.375,.125,1,.625);default->Shapes.box(0,y,0,1,1,1);};}
    @Override public boolean isLadder(BlockState state,LevelReader level,BlockPos pos,LivingEntity entity){return true;}
    @Override protected int getLightBlock(BlockState state,BlockGetter level,BlockPos pos){return 0;}
    @Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter level,BlockPos pos){return true;}
    @Override protected BlockState rotate(BlockState state,Rotation rotation){int meta=state.getValue(META);return meta<2?state:state.setValue(META,rotation.rotate(Direction.from3DDataValue(meta)).get3DDataValue());}
    @Override protected BlockState mirror(BlockState state,Mirror mirror){int meta=state.getValue(META);return meta<2?state:state.setValue(META,mirror.mirror(Direction.from3DDataValue(meta)).get3DDataValue());}
}
