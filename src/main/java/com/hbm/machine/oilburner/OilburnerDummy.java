package com.hbm.machine.oilburner;
import com.hbm.api.machine.MachineView;
import com.hbm.multiblock.DummyableBlock;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
/** View-only ordinary dummy; specialized source fluid and thermal extras inherit this resolver. */
public class OilburnerDummy extends BlockEntity implements MachineView {
 public OilburnerDummy(BlockPos pos,BlockState state){this(OilburnerContent.DUMMY.get(),pos,state);}protected OilburnerDummy(BlockEntityType<?> type,BlockPos pos,BlockState state){super(type,pos,state);}
 public OilburnerBlockEntity core(){if(level==null||!(getBlockState().getBlock() instanceof OilburnerBlock block))return null;var pos=block.findCore(level,worldPosition);return pos!=null&&level.getBlockEntity(pos) instanceof OilburnerBlockEntity core?core:null;}
 @Override public boolean isValidBlockState(BlockState state){return super.isValidBlockState(state)&&!DummyableBlock.isCore(state);}
 @Override public long energyStored(){return 0;}@Override public long energyCapacity(){return 0;}@Override public List<TankView> tanks(){var core=core();return core==null?List.of():core.tanks();}@Override public List<ProgressView> progressBars(){var core=core();return core==null?List.of():core.progressBars();}
}
