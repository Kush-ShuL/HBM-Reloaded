package com.hbm.machine.oilburner;
import com.hbm.api.tool.ToolableBlock;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
public final class OilburnerBlock extends DummyableBlock implements ToolableBlock {
 public OilburnerBlock(){super(Properties.of().strength(5,6).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());}@Override protected MapCodec<? extends DummyableBlock> codec(){return MapCodec.unit(this);}@Override public int[] dimensions(){return new int[]{1,0,1,1,1,1};}@Override public int offset(){return 1;}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){if(isCore(state))return new OilburnerBlockEntity(pos,state);if(isPort(state))return state.getValue(LEGACY_META)%6>1?new OilburnerFluidPort(pos,state):new OilburnerHeatPort(pos,state);return new OilburnerDummy(pos,state);}
 @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,OilburnerContent.ENTITY.get(),OilburnerBlockEntity::tick):null;}
 @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity player,ItemStack stack){super.setPlacedBy(level,pos,state,player,stack);if(level.isClientSide)return;var core=findCore(level,pos);if(core!=null){for(var port:OilburnerBlockEntity.portPositions(core))setPort(level,port,true);setPort(level,OilburnerBlockEntity.heatPort(core,level.getBlockState(core)),true);}}
 @Override public boolean onScrew(Level level,Player player,BlockPos pos,Direction side,float x,float y,float z,ToolType tool){if(tool!=ToolType.SCREWDRIVER)return false;if(level.isClientSide)return true;var core=findCore(level,pos);if(core==null||!(level.getBlockEntity(core) instanceof OilburnerBlockEntity heater))return false;heater.toggleSetting();return true;}
}
