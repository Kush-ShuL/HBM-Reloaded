package com.hbm.machine.massstorage;

import com.hbm.item.lock.PadlockItem;
import com.hbm.machine.storage.*;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;

/** Source BlockMassStorage's sixteen tier/facing states and distinct shell/contents drop rules. */
public final class MassStorageBlock extends BaseEntityBlock {
    public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
    public MassStorageBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.METAL).requiresCorrectToolForDrops());registerDefaultState(stateDefinition.any().setValue(META,0));}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return MapCodec.unit(this);}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    public BlockState fromLegacy(int meta){return defaultBlockState().setValue(META,meta&15);}
    public static int tier(BlockState state){return state.getValue(META)%4;}
    public static int capacity(int meta){return switch(Math.abs(meta%4)){case 3->100;case 1->100_000;case 2->1_000_000;default->10_000;};}
    public static Direction facing(BlockState state){return Direction.from3DDataValue(state.getValue(META)/4+2);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){return fromLegacy(MassStorageItem.tier(context.getItemInHand())+(context.getHorizontalDirection().getOpposite().get3DDataValue()-2)*4);}
    @Override protected BlockState rotate(BlockState state,Rotation rotation){return state.setValue(META,tier(state)+(rotation.rotate(facing(state)).get3DDataValue()-2)*4);}
    @Override protected BlockState mirror(BlockState state,Mirror mirror){return rotate(state,mirror.getRotation(facing(state)));}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new MassStorageBlockEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return level.isClientSide?null:createTickerHelper(type,MassStorageContent.ENTITY.get(),(world,pos,current,storage)->storage.tick());}
    private InteractionResult open(Level world,BlockPos pos,Player player){
        if(world.isClientSide)return InteractionResult.SUCCESS;if(player.isShiftKeyDown())return InteractionResult.PASS;
        if(world.getBlockEntity(pos) instanceof MassStorageBlockEntity storage&&storage.canAccess(player)&&player instanceof ServerPlayer server)server.openMenu(storage,buffer->buffer.writeBlockPos(pos));
        return InteractionResult.CONSUME;
    }
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return open(level,pos,player);}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){
        if(stack.getItem() instanceof PadlockItem||LockableBlockEntity.isItem(stack,"key_kit"))return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        return open(level,pos,player)==InteractionResult.PASS?ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION:ItemInteractionResult.sidedSuccess(level.isClientSide);
    }
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){
        if(level.getBlockEntity(pos) instanceof MassStorageBlockEntity storage&&CrateStackData.hasSourceData(stack)){
            var tag=CrateStackData.data(stack);CrateStackData.loadSlots(tag,storage,level.registryAccess());if(tag.contains("lock")){storage.setPins(tag.getInt("lock"));storage.setMod(tag.getDouble("lockMod"));storage.lock();}storage.setStockpile(tag.getInt("stack"));
        }
        int yaw=net.minecraft.util.Mth.floor(placer.getYRot()*4/360+.5)&3;int direction=switch(yaw){case 0->0;case 1->3;case 2->1;default->2;};level.setBlock(pos,state.setValue(META,MassStorageItem.tier(stack)+direction*4),2);
    }
    @Override public boolean onDestroyedByPlayer(BlockState state,Level level,BlockPos pos,Player player,boolean harvest,net.minecraft.world.level.material.FluidState fluid){
        var storage=level.getBlockEntity(pos) instanceof MassStorageBlockEntity found?found:null;
        if(!level.isClientSide&&!player.getAbilities().instabuild&&harvest){
            var drop=MassStorageContent.ITEM.get().stack(tier(state));var tag=new net.minecraft.nbt.CompoundTag();
            if(storage!=null){CrateStackData.saveSlots(tag,storage,level.registryAccess());if(storage.isLocked()){tag.putInt("lock",storage.getPins());tag.putDouble("lockMod",storage.getMod());}if(!tag.isEmpty())tag.putInt("stack",storage.getStockpile());}
            if(!tag.isEmpty())CrateStackData.data(drop,tag);level.addFreshEntity(new ItemEntity(level,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,drop));
        }
        if(storage!=null)storage.suppressDropOnRemoval=true;try{return level.removeBlock(pos,false);}finally{if(storage!=null)storage.suppressDropOnRemoval=false;}
    }
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder params){return List.of();}
    @Override public ItemStack getCloneItemStack(LevelReader level,BlockPos pos,BlockState state){return MassStorageContent.ITEM.get().stack(tier(state));}
    @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moved){
        if(!state.is(next.getBlock())&&level.getBlockEntity(pos) instanceof MassStorageBlockEntity storage){
            if(!storage.suppressDropOnRemoval){var actual=new SimpleContainer(storage.getItem(0),storage.getItem(2));CrateContents.ejectWorld(level,pos.getX(),pos.getY(),pos.getZ(),actual);}level.updateNeighbourForOutputSignal(pos,this);
        }
        super.onRemove(state,level,pos,next,moved);
    }
    @Override protected boolean hasAnalogOutputSignal(BlockState state){return true;}
    @Override protected int getAnalogOutputSignal(BlockState state,Level level,BlockPos pos){return level.getBlockEntity(pos) instanceof MassStorageBlockEntity storage?storage.redstone:0;}
}
