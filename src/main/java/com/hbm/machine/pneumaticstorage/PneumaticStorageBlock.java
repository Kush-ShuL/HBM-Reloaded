package com.hbm.machine.pneumaticstorage;

import com.hbm.machine.storage.*;
import com.mojang.serialization.MapCodec;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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

/** Five real source cubes, including distinct inventory-preserving harvest and ghost-filter break paths. */
public final class PneumaticStorageBlock extends BaseEntityBlock {
    public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
    public final PneumaticKind kind;
    public PneumaticStorageBlock(PneumaticKind kind){super(Properties.of().mapColor(MapColor.METAL).strength(5,10).sound(com.hbm.block.pipe.PipeSounds.TYPE));this.kind=kind;registerDefaultState(stateDefinition.any().setValue(META,0));}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return MapCodec.unit(this);}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){return defaultBlockState().setValue(META,kind==PneumaticKind.ACCESS?sourceOrientation(context):0);}
    private static int sourceOrientation(BlockPlaceContext context){var player=context.getPlayer();if(player==null)return 0;var pos=context.getClickedPos();if(Math.abs((float)player.getX()-pos.getX())<2F&&Math.abs((float)player.getZ()-pos.getZ())<2F){double eyes=player.getY()+1.82D;if(eyes-pos.getY()>2D)return 1;if(pos.getY()-eyes>0D)return 0;}return switch(net.minecraft.util.Mth.floor(player.getYRot()*4F/360F+.5D)&3){case 0->2;case 1->5;case 2->3;default->4;};}
    @Override protected BlockState rotate(BlockState state,Rotation rotation){int meta=state.getValue(META);return kind==PneumaticKind.ACCESS&&meta<6?state.setValue(META,rotation.rotate(Direction.from3DDataValue(meta)).get3DDataValue()):state;}
    @Override protected BlockState mirror(BlockState state,Mirror mirror){int meta=state.getValue(META);return kind==PneumaticKind.ACCESS&&meta<6?rotate(state,mirror.getRotation(Direction.from3DDataValue(meta))):state;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return PneumaticStorageContent.create(kind,pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return level.isClientSide?null:createTickerHelper(type,PneumaticStorageContent.entity(kind),PneumaticInventory::tick);}
    private InteractionResult open(Level level,BlockPos pos,Player player){if(player.isShiftKeyDown())return InteractionResult.PASS;if(!level.isClientSide&&player instanceof ServerPlayer server&&level.getBlockEntity(pos) instanceof PneumaticInventory machine)server.openMenu(machine,buffer->buffer.writeBlockPos(pos));return InteractionResult.sidedSuccess(level.isClientSide);}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return open(level,pos,player);}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){return open(level,pos,player)==InteractionResult.PASS?ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION:ItemInteractionResult.sidedSuccess(level.isClientSide);}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){if(level.getBlockEntity(pos) instanceof PneumaticInventory machine){if((kind==PneumaticKind.MONO||kind==PneumaticKind.CLUTTER)&&CrateStackData.hasSourceData(stack)){var tag=CrateStackData.data(stack);CrateStackData.loadSlots(tag,machine,level.registryAccess());if(machine instanceof PneumaticMono mono)for(int i=0;i<3;i++)mono.amounts[i]=tag.getInt("amount"+i);if(kind==PneumaticKind.CLUTTER&&stack.has(DataComponents.CUSTOM_NAME))machine.setCustomName(stack.get(DataComponents.CUSTOM_NAME));}else if((kind==PneumaticKind.IMPORTER||kind==PneumaticKind.EXPORTER)&&stack.has(DataComponents.CUSTOM_NAME))machine.setCustomName(stack.get(DataComponents.CUSTOM_NAME));}}
    @Override public boolean onDestroyedByPlayer(BlockState state,Level level,BlockPos pos,Player player,boolean willHarvest,net.minecraft.world.level.material.FluidState fluid){
        if(kind!=PneumaticKind.CLUTTER&&kind!=PneumaticKind.MONO)return super.onDestroyedByPlayer(state,level,pos,player,willHarvest,fluid);
        var machine=level.getBlockEntity(pos) instanceof PneumaticInventory value?value:null;boolean suppress=true;
        if(!level.isClientSide){
            if(kind==PneumaticKind.CLUTTER&&!StorageCrateConfig.keepContents()){if(!player.getAbilities().instabuild)spawn(level,pos,new ItemStack(this));suppress=false;}
            else if(!player.getAbilities().instabuild&&willHarvest){var drop=new ItemStack(this);var tag=new CompoundTag();if(machine!=null){CrateStackData.saveSlots(tag,machine,level.registryAccess());if(machine instanceof PneumaticMono mono)for(int i=0;i<3;i++)if(!mono.slots[i].isEmpty())tag.putInt("amount"+i,mono.amounts[i]);if(kind==PneumaticKind.CLUTTER&&machine.customName()!=null)drop.set(DataComponents.CUSTOM_NAME,machine.customName());}if(!tag.isEmpty())CrateStackData.data(drop,tag);
                if(kind==PneumaticKind.CLUTTER)try{if(CrateStackData.compressedSize(drop,level.registryAccess())>6000){player.displayClientMessage(Component.translatable("message.hbm.storage_crate.overflow").withStyle(ChatFormatting.RED),false);drop=new ItemStack(this);suppress=false;}}catch(java.io.IOException ignored){}spawn(level,pos,drop);
            }
        }
        if(machine!=null)machine.suppressDropOnRemoval=suppress;try{return level.removeBlock(pos,false);}finally{if(machine!=null)machine.suppressDropOnRemoval=false;}
    }
    private static void spawn(Level level,BlockPos pos,ItemStack stack){level.addFreshEntity(new ItemEntity(level,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,stack));}
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder params){return kind==PneumaticKind.CLUTTER||kind==PneumaticKind.MONO?List.of():super.getDrops(state,params);}
    @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moved){if(!state.is(next.getBlock())&&level.getBlockEntity(pos) instanceof PneumaticInventory machine&&!machine.suppressDropOnRemoval){if(kind==PneumaticKind.CLUTTER||kind==PneumaticKind.IMPORTER)CrateContents.ejectWorld(level,pos.getX(),pos.getY(),pos.getZ(),machine);else if(kind==PneumaticKind.EXPORTER){var outputs=new SimpleContainer(9);for(int i=0;i<9;i++)outputs.setItem(i,machine.slots[i+9]);CrateContents.ejectWorld(level,pos.getX(),pos.getY(),pos.getZ(),outputs);}level.updateNeighbourForOutputSignal(pos,this);}super.onRemove(state,level,pos,next,moved);}
}
