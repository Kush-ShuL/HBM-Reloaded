package com.hbm.machine.electricheater;
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
/** Source HeaterElectric4-depth by3-width footprint, offset2 and sole forward extra power connector. */
public final class ElectricHeaterBlock extends DummyableBlock implements ToolableBlock {
 public ElectricHeaterBlock(){super(Properties.of().strength(5,6).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());}@Override protected MapCodec<? extends DummyableBlock> codec(){return MapCodec.unit(this);}@Override public int[] dimensions(){return new int[]{0,0,1,2,1,1};}@Override public int offset(){return 2;}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new ElectricHeaterBlockEntity(pos,state):new ElectricHeaterProxy(pos,state);}@Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,ElectricHeaterContent.ENTITY.get(),ElectricHeaterBlockEntity::tick):null;}
 @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity player,ItemStack stack){super.setPlacedBy(level,pos,state,player,stack);if(level.isClientSide)return;var core=findCore(level,pos);if(core!=null)setPort(level,core.relative(facing(level.getBlockState(core)),2),true);}
 @Override public boolean onScrew(Level level,Player player,BlockPos pos,Direction side,float x,float y,float z,ToolType tool){if(tool!=ToolType.SCREWDRIVER)return false;if(level.isClientSide)return true;var core=findCore(level,pos);if(core==null||!(level.getBlockEntity(core) instanceof ElectricHeaterBlockEntity heater))return false;heater.toggleSetting();return true;}
}
