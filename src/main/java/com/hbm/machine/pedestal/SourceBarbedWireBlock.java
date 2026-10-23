package com.hbm.machine.pedestal;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.*;
/** Actual ordinary BarbedWire, without registering unported fire/acid/wither/ultradeath variants as shells. */
public class SourceBarbedWireBlock extends Block {
    public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,5);
    public SourceBarbedWireBlock(){super(Properties.of().strength(5,6).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());registerDefaultState(stateDefinition.any().setValue(META,0));}
    @Override protected MapCodec<? extends Block> codec(){return MapCodec.unit(this);}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
    public BlockState fromLegacy(int meta){return defaultBlockState().setValue(META,Math.floorMod(meta,6));}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){return fromLegacy(switch(Mth.floor(context.getRotation()*4/360+.5)&3){case 0->5;case 1->3;case 2->4;default->2;});}
    @Override protected void entityInside(BlockState state,Level level,BlockPos pos,Entity entity){entity.makeStuckInBlock(state,new Vec3(.25,.05,.25));entity.setDeltaMovement(entity.getDeltaMovement().multiply(.15,.1,.15));hurtTouch(entity);}
    protected void hurtTouch(Entity entity){entity.hurt(entity.damageSources().cactus(),2F);}
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return Shapes.empty();}
    @Override protected int getLightBlock(BlockState state,BlockGetter level,BlockPos pos){return 0;}
    @Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter level,BlockPos pos){return true;}
    @Override protected BlockState rotate(BlockState state,Rotation rotation){int meta=state.getValue(META);return meta<2?state:state.setValue(META,rotation.rotate(Direction.from3DDataValue(meta)).get3DDataValue());}
    @Override protected BlockState mirror(BlockState state,Mirror mirror){int meta=state.getValue(META);return meta<2?state:state.setValue(META,mirror.mirror(Direction.from3DDataValue(meta)).get3DDataValue());}
}
