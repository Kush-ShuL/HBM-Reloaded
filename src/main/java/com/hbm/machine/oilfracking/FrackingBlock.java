package com.hbm.machine.oilfracking;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
/** All nine actual source filled volumes, including fixed feet and the fixed-WEST upper balcony. */
public final class FrackingBlock extends DummyableBlock {
    private static final MapCodec<FrackingBlock> CODEC=simpleCodec(ignored->new FrackingBlock());
    public FrackingBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,12).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
    @Override protected MapCodec<? extends DummyableBlock> codec(){return CODEC;}
    @Override public int[] dimensions(){return new int[]{3,0,0,0,0,0};}
    private static void volume(Set<BlockPos> cells,BlockPos origin,int[] dims,Direction dir){var d=rotate(dims,dir);for(var pos:BlockPos.betweenClosed(origin.offset(-d[4],-d[1],-d[2]),origin.offset(d[5],d[0],d[3])))cells.add(pos.immutable());}
    public static Set<BlockPos> cells(BlockPos core,Direction dir){var cells=new LinkedHashSet<BlockPos>();volume(cells,core,new int[]{3,0,0,0,0,0},dir);volume(cells,core.above(2),new int[]{1,0,3,3,3,3},dir);for(int x:new int[]{-2,3})for(int z:new int[]{-2,3})volume(cells,core.offset(x,2,z),new int[]{-1,2,0,1,0,1},Direction.NORTH);volume(cells,core,new int[]{10,-4,2,2,2,2},dir);volume(cells,core,new int[]{24,-9,1,1,1,1},dir);volume(cells,core.above(15),new int[]{1,0,1,1,-2,3},Direction.WEST);return cells;}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){var dir=context.getHorizontalDirection().getOpposite();for(var pos:cells(context.getClickedPos(),dir))if(!context.getLevel().isInWorldBounds(pos)||!context.getLevel().hasChunkAt(pos)||!pos.equals(context.getClickedPos())&&!context.getLevel().getBlockState(pos).canBeReplaced())return null;return defaultBlockState().setValue(LEGACY_META,dir.get3DDataValue()+10);}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){if(level.isClientSide)return;var cells=cells(pos,placementFacing(state));safely(()->{var placed=new HashSet<BlockPos>();placed.add(pos);var queue=new ArrayDeque<BlockPos>();queue.add(pos);while(!queue.isEmpty()){var parent=queue.removeFirst();for(var direction:Direction.values()){var next=parent.relative(direction);if(cells.contains(next)&&placed.add(next)){level.setBlock(next,defaultBlockState().setValue(LEGACY_META,direction.get3DDataValue()),3);queue.add(next);}}}});if(level.getBlockEntity(pos) instanceof FrackingEntity machine){if(stack.has(net.minecraft.core.component.DataComponents.CUSTOM_NAME))machine.setCustomName(stack.getHoverName());machine.restore(stack);}}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new FrackingEntity(pos,state):isPort(state)?new FrackingPort(pos,state):null;}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,FrackingContent.ENTITY.get(),FrackingEntity::tick):null;}
    private FrackingEntity core(BlockGetter level,BlockPos pos){var core=findCore(level,pos);return core!=null&&level.getBlockEntity(core) instanceof FrackingEntity machine?machine:null;}
    @Override public boolean onDestroyedByPlayer(BlockState state,Level level,BlockPos pos,Player player,boolean harvest,net.minecraft.world.level.material.FluidState fluid){if(!level.isClientSide&&harvest&&core(level,pos)!=null)return true;return super.onDestroyedByPlayer(state,level,pos,player,harvest,fluid);}
    @Override public void playerDestroy(Level level,Player player,BlockPos pos,BlockState state,BlockEntity original,ItemStack tool){var machine=core(level,pos);try{super.playerDestroy(level,player,pos,state,machine==null?original:machine,tool);}finally{if(machine!=null&&core(level,pos)==machine)level.removeBlock(pos,false);}}
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder params){if(params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof FrackingEntity machine)return List.of(machine.dropStack());var origin=params.getOptionalParameter(LootContextParams.ORIGIN);var machine=origin==null?null:core(params.getLevel(),BlockPos.containing(origin));return machine==null?List.of():List.of(machine.dropStack());}
    @Override public ItemStack getCloneItemStack(LevelReader level,BlockPos pos,BlockState state){var machine=core(level,pos);return machine==null?new ItemStack(this):machine.dropStack();}
}
