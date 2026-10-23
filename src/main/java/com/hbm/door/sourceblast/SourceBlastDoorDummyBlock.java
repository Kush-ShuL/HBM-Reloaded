package com.hbm.door.sourceblast;

import com.hbm.interfaces.IBomb;
import com.hbm.item.lock.PadlockItem;
import com.hbm.machine.storage.LockableBlockEntity;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;

/** Actual DummyBlockBlast: invisible full collision, no own drops, parent destruction unless guarded source opening. */
public final class SourceBlastDoorDummyBlock extends BaseEntityBlock implements IBomb {
    private static final ThreadLocal<Integer> SAFE_BREAK=ThreadLocal.withInitial(()->0);
    public SourceBlastDoorDummyBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(10,6000).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return MapCodec.unit(this);}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.INVISIBLE;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new SourceBlastDoorDummy(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,SourceBlastDoorContent.DUMMY_ENTITY.get(),SourceBlastDoorDummy::tick);}
    public static void safely(Runnable action){int depth=SAFE_BREAK.get();SAFE_BREAK.set(depth+1);try{action.run();}finally{if(depth==0)SAFE_BREAK.remove();else SAFE_BREAK.set(depth);}}
    @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState replacement,boolean moving){
        if(!replacement.is(this)&&!level.isClientSide&&SAFE_BREAK.get()==0&&level.getBlockEntity(pos)instanceof SourceBlastDoorDummy dummy){
            if(SourceBlastDoorContent.neighborsReady(level,dummy.parent()))level.destroyBlock(dummy.parent(),true);
            else SourceBlastDoorContent.deferParentBreak(level,dummy.parent());
        }
        super.onRemove(state,level,pos,replacement,moving);
    }
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder builder){return List.of();}
    @Override public ItemStack getCloneItemStack(LevelReader level,BlockPos pos,BlockState state){return new ItemStack(SourceBlastDoorContent.ITEM.get());}
    private InteractionResult activate(Level level,BlockPos pos,Player player){
        if(level.isClientSide)return InteractionResult.SUCCESS;
        if(SourceBlastDoorBlock.yieldsToItem(player))return InteractionResult.PASS;
        if(!player.isShiftKeyDown()&&level.getBlockEntity(pos)instanceof SourceBlastDoorDummy dummy){var door=dummy.door();if(door!=null&&door.canAccess(player))door.tryToggle();}
        return InteractionResult.CONSUME; // Original dummy returns true even while sneaking.
    }
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return activate(level,pos,player);}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){if(stack.getItem()instanceof PadlockItem||LockableBlockEntity.isItem(stack,"key_kit"))return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;return activate(level,pos,player).consumesAction()?ItemInteractionResult.sidedSuccess(level.isClientSide):ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;}
    @Override public BombReturnCode explode(Level level,int x,int y,int z){if(level.isClientSide)return BombReturnCode.UNDEFINED;if(level.getBlockEntity(new BlockPos(x,y,z))instanceof SourceBlastDoorDummy dummy){var door=dummy.door();if(door!=null&&!door.isLocked()){door.tryToggle();return BombReturnCode.TRIGGERED;}}return BombReturnCode.ERROR_INCOMPATIBLE;}
    @Override protected int getLightBlock(BlockState state,BlockGetter level,BlockPos pos){return 0;}
    @Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter level,BlockPos pos){return true;}
    @Override protected VoxelShape getOcclusionShape(BlockState state,BlockGetter level,BlockPos pos){return Shapes.empty();}
}
