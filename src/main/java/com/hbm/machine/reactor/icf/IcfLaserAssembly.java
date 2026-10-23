package com.hbm.machine.reactor.icf;
import com.hbm.machine.reactor.pwr.PwrPackets;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
/** Real source floodfill: six-connected internal parts, casing/ports are terminal boundary leaves;1024 limit. */
public final class IcfLaserAssembly{
 private final Level level;private final BlockPos core;private final ServerPlayer player;private final Map<BlockPos,Integer> assembly=new LinkedHashMap<>();private final List<Set<BlockPos>> groups=new ArrayList<>();private boolean errored;
 private IcfLaserAssembly(Level level,BlockPos core,ServerPlayer player){this.level=level;this.core=core;this.player=player;for(int i=0;i<6;i++)groups.add(new HashSet<>());assembly.put(core,0);}
 public static void assemble(Level level,BlockPos core,ServerPlayer player){if(!(level.getBlockEntity(core) instanceof IcfLaserEntity controller))return;var work=new IcfLaserAssembly(level,core,player);Direction back=Direction.from3DDataValue(controller.getBlockState().getValue(IcfStructBlock.META)).getOpposite();work.flood(core.relative(back));work.assembly.remove(core);if(!work.errored){for(var entry:work.assembly.entrySet()){var pos=entry.getKey();level.setBlock(pos,IcfReactorContent.FORMED.get().defaultBlockState().setValue(IcfFormedBlock.PORT,work.groups.get(1).contains(pos)),3);if(level.getBlockEntity(pos) instanceof IcfFormedEntity formed){formed.original=IcfReactorContent.LASER_COMPONENT.get().defaultBlockState().setValue(IcfMaterialBlock.META,entry.getValue());formed.controller=core;formed.setChanged();}}controller.setup(work.groups.get(1),work.groups.get(2),work.groups.get(3),work.groups.get(4),work.groups.get(5));controller.setChanged();}controller.assembled=!work.errored;}
 private void flood(BlockPos raw){BlockPos pos=raw.immutable();if(assembly.containsKey(pos))return;if(assembly.size()>=1024){error(pos,"gui.hbm.icf.max_size");return;}var state=level.getBlockState(pos);if(state.is(IcfReactorContent.LASER_COMPONENT.get())){int part=state.getValue(IcfMaterialBlock.META);if(part>=0&&part<6){groups.get(part).add(pos);assembly.put(pos,part);if(part<2)return;flood(pos.east());flood(pos.west());flood(pos.above());flood(pos.below());flood(pos.south());flood(pos.north());return;}}error(pos,"gui.hbm.icf.not_laser");}
 private void error(BlockPos pos,String message){errored=true;PwrPackets.marker(player,pos,message);}private IcfLaserAssembly(){throw new AssertionError();}
}
