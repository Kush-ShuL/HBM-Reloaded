package com.hbm.machine.reactor.rbmk.console;
import com.hbm.api.tool.ToolableBlock;
import com.hbm.item.guidebook.*;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.*;
/** True four-high five-wide console with separate one-row foot extension and original guide-book hotspot. */
public final class RbmkConsoleBlock extends DummyableBlock implements ToolableBlock {
    public RbmkConsoleBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(3,18).sound(SoundType.STONE).requiresCorrectToolForDrops());}
    @Override protected MapCodec<? extends RbmkConsoleBlock> codec(){return MapCodec.unit(this);}@Override public int[] dimensions(){return new int[]{3,0,0,0,2,2};}@Override public int offset(){return 1;}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){var state=super.getStateForPlacement(context);if(state==null)return null;var core=coreForPlacement(context);var d=rotate(new int[]{0,0,0,1,2,2},context.getHorizontalDirection().getOpposite());for(var p:BlockPos.betweenClosed(core.offset(-d[4],0,-d[2]),core.offset(d[5],0,d[3])))if(!context.getLevel().isInWorldBounds(p)||!context.getLevel().hasChunkAt(p)||!p.equals(context.getClickedPos())&&!context.getLevel().getBlockState(p).canBeReplaced())return null;return state;}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity entity,ItemStack stack){super.setPlacedBy(level,pos,state,entity,stack);if(level.isClientSide)return;var facing=placementFacing(state);var core=pos.relative(facing,-offset());var d=rotate(new int[]{0,0,0,1,2,2},facing);safely(()->{for(var raw:BlockPos.betweenClosed(core.offset(-d[4],0,-d[2]),core.offset(d[5],0,d[3]))){var p=raw.immutable();if(p.equals(core))continue;var away=p.getX()<core.getX()?Direction.WEST:p.getX()>core.getX()?Direction.EAST:p.getZ()<core.getZ()?Direction.NORTH:Direction.SOUTH;level.setBlock(p,defaultBlockState().setValue(LEGACY_META,away.get3DDataValue()),UPDATE_ALL);}});}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new RbmkConsoleEntity(pos,state):null;}@Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,RbmkConsoleContent.ENTITY.get(),RbmkConsoleEntity::tick):null;}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){if(stack.getItem() instanceof RbmkLinkerItem)return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;return super.useItemOn(stack,state,level,pos,player,hand,hit);}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){if(player.isShiftKeyDown())return InteractionResult.sidedSuccess(level.isClientSide);var core=findCore(level,pos);if(core==null||!(level.getBlockEntity(core) instanceof RbmkConsoleEntity console))return InteractionResult.sidedSuccess(level.isClientSide);if(!level.isClientSide){var root=player.getPersistentData();var data=root.getCompound(Player.PERSISTED_NBT_TAG);data.putLong("fbiMark",level.getGameTime()+20*60*20);root.put(Player.PERSISTED_NBT_TAG,data);}
        var guide=GuideBookContent.BOOK.get().stack(GuideBooks.BookType.RBMK);if(hit.getDirection()==Direction.UP){float angle=switch(facing(console.getBlockState())){case NORTH->90;case SOUTH->270;case WEST->180;default->0;};var offset=new Vec3(1.375,0,.75).yRot((float)Math.toRadians(angle));boolean has=false;for(int i=0;i<player.getInventory().getContainerSize();i++)if(ItemStack.isSameItemSameComponents(guide,player.getInventory().getItem(i))){has=true;break;}var p=hit.getLocation();if(Math.abs(p.x-(core.getX()+.5+offset.x))<.1875&&Math.abs(p.z-(core.getZ()+.5+offset.z))<.1875&&!has){if(!level.isClientSide){player.getInventory().add(guide);player.inventoryMenu.broadcastChanges();}return InteractionResult.sidedSuccess(level.isClientSide);}}
        if(level.isClientSide)RbmkConsoleContent.OPEN.accept(core);return InteractionResult.sidedSuccess(level.isClientSide);
    }
    @Override public boolean onScrew(Level level,Player player,BlockPos pos,Direction side,float x,float y,float z,ToolType tool){if(tool!=ToolType.SCREWDRIVER)return false;var core=findCore(level,pos);if(!level.isClientSide&&core!=null&&level.getBlockEntity(core) instanceof RbmkConsoleEntity console)console.rotate();return true;}
}
