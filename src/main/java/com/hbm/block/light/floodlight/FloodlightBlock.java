package com.hbm.block.light.floodlight;
import com.hbm.api.tool.ToolableBlock;
import com.hbm.block.light.SpotlightBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.material.MapColor;
/** Source placement face, extra vertical mount state and five-degree screwdriver articulation. */
public final class FloodlightBlock extends BaseEntityBlock implements ToolableBlock {
 public FloodlightBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());registerDefaultState(stateDefinition.any().setValue(SpotlightBlock.META,0));}
 @Override protected MapCodec<? extends BaseEntityBlock> codec(){return MapCodec.unit(this);}@Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(SpotlightBlock.META);}
 public BlockState fromLegacy(int meta){return defaultBlockState().setValue(SpotlightBlock.META,meta&15);}public static int transformMeta(int meta,int turn){int upper=0;while(meta>=6){meta-=6;upper+=6;}return upper+(SpotlightBlock.rotatedMeta(meta<<1,turn)>>1);}
 @Override public BlockState getStateForPlacement(BlockPlaceContext context){return fromLegacy(context.getClickedFace().get3DDataValue());}
 @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity player,ItemStack stack){setAngle(level,pos,player,true);}
 @Override protected BlockState rotate(BlockState state,Rotation rotation){return fromLegacy(transformMeta(state.getValue(SpotlightBlock.META),switch(rotation){case NONE->0;case CLOCKWISE_90->1;case CLOCKWISE_180->2;case COUNTERCLOCKWISE_90->3;}));}
 @Override protected BlockState mirror(BlockState state,Mirror mirror){int meta=state.getValue(SpotlightBlock.META),upper=meta/6*6,side=meta%6;if(mirror==Mirror.LEFT_RIGHT&&(side==2||side==3)||mirror==Mirror.FRONT_BACK&&(side==4||side==5))side^=1;return fromLegacy(upper+side);}
 @Override public boolean onScrew(Level level,Player player,BlockPos pos,Direction side,float x,float y,float z,ToolType tool){if(tool!=ToolType.SCREWDRIVER)return false;setAngle(level,pos,player,false);return true;}
 private static void setAngle(Level level,BlockPos pos,LivingEntity player,boolean updateMeta){if(!(level.getBlockEntity(pos)instanceof FloodlightBlockEntity lamp))return;int yaw=Mth.floor(player.getYRot()*4/360+.5)&3,meta=level.getBlockState(pos).getValue(SpotlightBlock.META)%6;float rotation=player.getXRot();if(meta==0||meta==1){if((yaw==0||yaw==2)&&updateMeta)level.setBlock(pos,level.getBlockState(pos).setValue(SpotlightBlock.META,meta+6),3);if(meta==1&&(yaw==0||yaw==1))rotation=180-rotation;if(meta==0&&(yaw==0||yaw==3))rotation=180-rotation;}lamp.rotation=-Math.round(rotation/5F)*5F;if(lamp.isOn)lamp.destroyLights();lamp.setChanged();lamp.sync();}
 @Override protected int getLightBlock(BlockState state,BlockGetter level,BlockPos pos){return 0;}@Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter level,BlockPos pos){return true;}@Override protected RenderShape getRenderShape(BlockState state){return RenderShape.ENTITYBLOCK_ANIMATED;}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new FloodlightBlockEntity(pos,state);}@Override public <T extends BlockEntity>BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,FloodlightContent.ENTITY.get(),FloodlightBlockEntity::tick);}
}
