package com.hbm.machine.storage;

import com.hbm.item.lock.PadlockItem;
import com.hbm.registry.HbmSounds;
import com.mojang.serialization.MapCodec;
import java.io.IOException;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;

/** Source BlockStorageCrate's player/config/explosion drop paths; inventory exists only in its real BE. */
public final class StorageCrateBlock extends BaseEntityBlock {
    public static final IntegerProperty LEGACY_META=IntegerProperty.create("legacy_meta",0,15);
    public final StorageCrateKind kind;
    public StorageCrateBlock(StorageCrateKind kind){super(Properties.of().mapColor(MapColor.METAL).strength(kind.hardness,kind.resistance).sound(SoundType.METAL).requiresCorrectToolForDrops());this.kind=kind;registerDefaultState(stateDefinition.any().setValue(LEGACY_META,0));}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return MapCodec.unit(this);}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(LEGACY_META);}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    public BlockState fromLegacy(int metadata){return defaultBlockState().setValue(LEGACY_META,metadata&15);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){return fromLegacy(context.getHorizontalDirection().getOpposite().get3DDataValue());}
    @Override protected BlockState rotate(BlockState state,Rotation rotation){int metadata=state.getValue(LEGACY_META);return metadata>=2&&metadata<=5?state.setValue(LEGACY_META,rotation.rotate(Direction.from3DDataValue(metadata)).get3DDataValue()):state;}
    @Override protected BlockState mirror(BlockState state,Mirror mirror){int metadata=state.getValue(LEGACY_META);return metadata>=2&&metadata<=5?rotate(state,mirror.getRotation(Direction.from3DDataValue(metadata))):state;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return StorageCrateContent.createEntity(kind,pos,state);}
    @Override public <T extends BlockEntity> net.minecraft.world.level.block.entity.BlockEntityTicker<T> getTicker(Level level,BlockState state,net.minecraft.world.level.block.entity.BlockEntityType<T> type){
        return kind==StorageCrateKind.TUNGSTEN?createTickerHelper(type,StorageCrateContent.ENTITY.get(kind).get(),com.hbm.machine.tungstencrate.TungstenCrateBlockEntity::tick):null;
    }
    private InteractionResult open(Level level,BlockPos pos,Player player){
        if(level.isClientSide)return InteractionResult.SUCCESS;if(player.isShiftKeyDown())return InteractionResult.PASS;
        if(level.getBlockEntity(pos) instanceof StorageCrateBlockEntity crate&&crate.canAccess(player)&&player instanceof ServerPlayer server){server.openMenu(crate);crate.spawnSpiders(player);}
        return InteractionResult.CONSUME;
    }
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return open(level,pos,player);}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){
        if(stack.getItem() instanceof PadlockItem||LockableBlockEntity.isItem(stack,"key_kit"))return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        return open(level,pos,player)==InteractionResult.PASS?ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION:ItemInteractionResult.sidedSuccess(level.isClientSide);
    }
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){
        if(level.getBlockEntity(pos) instanceof StorageCrateBlockEntity crate&&CrateStackData.hasSourceData(stack)){var tag=CrateStackData.data(stack);CrateStackData.loadSlots(tag,crate,level.registryAccess());if(tag.contains("lock")){crate.setPins(tag.getInt("lock"));crate.setMod(tag.getDouble("lockMod"));crate.lock();}crate.hasSpiders=tag.getBoolean("spiders");if(stack.has(DataComponents.CUSTOM_NAME))crate.setCustomName(stack.get(DataComponents.CUSTOM_NAME));}
        //Source also writes facing metadata for the visually symmetrical ordinary crates.
        int yaw=net.minecraft.util.Mth.floor(placer.getYRot()*4/360+.5)&3;int metadata=switch(yaw){case 0->2;case 1->5;case 2->3;default->4;};level.setBlock(pos,state.setValue(LEGACY_META,metadata),2);
    }
    @Override public boolean onDestroyedByPlayer(BlockState state,Level level,BlockPos pos,Player player,boolean willHarvest,net.minecraft.world.level.material.FluidState fluid){
        var crate=level.getBlockEntity(pos) instanceof StorageCrateBlockEntity found?found:null;
        if(!level.isClientSide&&!StorageCrateConfig.keepContents()){
            if(!player.getAbilities().instabuild)spawnShell(level,pos,new ItemStack(this));return remove(level,pos,crate,crate!=null&&crate.isLocked());
        }
        if(!level.isClientSide&&!player.getAbilities().instabuild&&willHarvest){
            var drop=new ItemStack(this);var tag=new net.minecraft.nbt.CompoundTag();
            if(crate!=null){CrateStackData.saveSlots(tag,crate,level.registryAccess());if(crate.isLocked()){tag.putInt("lock",crate.getPins());tag.putDouble("lockMod",crate.getMod());}if(crate.hasSpiders)tag.putBoolean("spiders",true);}
            if(!tag.isEmpty()){tag.putLong("stacklock",level.random.nextLong());CrateStackData.data(drop,tag);}
            if(crate!=null&&crate.hasCustomName())drop.set(DataComponents.CUSTOM_NAME,crate.getCustomName());
            if(drop.has(DataComponents.CUSTOM_DATA)||drop.has(DataComponents.CUSTOM_NAME))try{if(CrateStackData.compressedSize(drop,level.registryAccess())>6000){player.displayClientMessage(Component.translatable("message.hbm.storage_crate.overflow").withStyle(ChatFormatting.RED),false);spawnShell(level,pos,new ItemStack(this));return remove(level,pos,crate,false);}}catch(IOException ignored){}
            spawnShell(level,pos,drop);
        }
        return remove(level,pos,crate,true);
    }
    private static boolean remove(Level level,BlockPos pos,StorageCrateBlockEntity crate,boolean suppress){if(crate!=null)crate.suppressDropOnRemoval=suppress;try{return level.removeBlock(pos,false);}finally{if(crate!=null)crate.suppressDropOnRemoval=false;}}
    private static void spawnShell(Level level,BlockPos pos,ItemStack stack){level.addFreshEntity(new ItemEntity(level,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,stack));}
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder params){return List.of();}
    @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moved){
        if(!state.is(next.getBlock())&&level.getBlockEntity(pos) instanceof StorageCrateBlockEntity crate){if(!crate.suppressDropOnRemoval)CrateContents.ejectWorld(level,pos.getX(),pos.getY(),pos.getZ(),crate);level.updateNeighbourForOutputSignal(pos,this);}
        super.onRemove(state,level,pos,next,moved);
    }
    @Override protected boolean hasAnalogOutputSignal(BlockState state){return true;}
    @Override protected int getAnalogOutputSignal(BlockState state,Level level,BlockPos pos){return level.getBlockEntity(pos) instanceof StorageCrateBlockEntity crate?AbstractContainerMenu.getRedstoneSignalFromContainer(crate):0;}
}
