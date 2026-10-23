package com.hbm.machine.electrolyser;
import com.hbm.multiblock.*;
import com.mojang.serialization.MapCodec;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
/** Original thirteen real structural boxes/offset5. All proxies are created at their final location once. */
public final class ElectrolyserBlock extends DummyableBlock {
    private static final int[][] REGIONS={{0,0,5,5,1,3},{2,-1,5,5,1,1},{3,-3,5,5,0,0},{3,-1,4,-4,-3,3},{3,-1,2,-2,-3,3},{3,-1,0,0,-3,3},{3,-1,-2,2,-3,3},{3,-1,-4,4,-3,3}};
    public ElectrolyserBlock(){super(Properties.of().strength(10,12).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
    @Override protected MapCodec<? extends DummyableBlock> codec(){return MapCodec.unit(this);}@Override public int[] dimensions(){return REGIONS[0].clone();}@Override public int offset(){return 5;}
    public static Set<BlockPos> parts(BlockPos core,Direction facing){var out=new LinkedHashSet<BlockPos>();for(var region:REGIONS)add(out,core,region,facing);for(int step:new int[]{4,2,0,-2,-4})add(out,core.relative(facing,step).above(3),new int[]{0,0,0,0,-1,2},facing);return out;}
    private static void add(Set<BlockPos> out,BlockPos origin,int[] region,Direction facing){var d=rotate(region,facing);BlockPos.betweenClosed(origin.offset(-d[4],-d[1],-d[2]),origin.offset(d[5],d[0],d[3])).forEach(p->out.add(p.immutable()));}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){var state=super.getStateForPlacement(context);if(state==null)return null;for(var part:parts(coreForPlacement(context),placementFacing(state)))if(!context.getLevel().isInWorldBounds(part)||!context.getLevel().hasChunkAt(part)||!part.equals(context.getClickedPos())&&!context.getLevel().getBlockState(part).canBeReplaced())return null;return state;}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity player,ItemStack stack){if(level.isClientSide)return;var facing=placementFacing(state);var core=pos.relative(facing,-offset());var parts=parts(core,facing);safely(()->{if(!pos.equals(core))level.removeBlock(pos,false);level.setBlock(core,defaultBlockState().setValue(LEGACY_META,facing.get3DDataValue()+10),UPDATE_ALL);var done=new HashSet<BlockPos>();var queue=new ArrayDeque<BlockPos>();done.add(core);queue.add(core);while(!queue.isEmpty()){var parent=queue.removeFirst();for(var away:Direction.values()){var child=parent.relative(away);if(parts.contains(child)&&done.add(child)){level.setBlock(child,defaultBlockState().setValue(LEGACY_META,away.get3DDataValue()),UPDATE_ALL);queue.addLast(child);}}}if(done.size()!=parts.size())throw new IllegalStateException("Disconnected real electrolyser assembly");});
        // Only the eleven original physical extra proxies; sixteen real TilePort definitions are separate virtual network positions.
        var rot=facing.getClockWise();for(int end:new int[]{-5,5})for(int side=-1;side<=1;side++)setPort(level,core.relative(facing,end).relative(rot,side),true);for(int step:new int[]{-4,-2,0,2,4})setPort(level,core.relative(facing,step).relative(rot),true);
        if(stack.has(net.minecraft.core.component.DataComponents.CUSTOM_NAME)&&level.getBlockEntity(core) instanceof NamedMultiblock named)named.setCustomName(stack.getHoverName());
    }
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new ElectrolyserBlockEntity(pos,state):isPort(state)?new ElectrolyserPort(pos,state):null;}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,ElectrolyserContent.ENTITY.get(),ElectrolyserBlockEntity::tick):null;}
}
