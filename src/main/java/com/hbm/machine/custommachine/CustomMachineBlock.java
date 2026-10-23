package com.hbm.machine.custommachine;
import com.hbm.hazard.HbmHazards;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import java.util.*;
/** Actual standalone terminal cube. Structure is config-defined existing blocks, not Dummyable proxy occupancy. */
public final class CustomMachineBlock extends BaseEntityBlock {
    public CustomMachineBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).lightLevel(state->15).sound(SoundType.STONE).requiresCorrectToolForDrops());registerDefaultState(stateDefinition.any().setValue(CMComponentBlock.META,0));}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return MapCodec.unit(this);}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(CMComponentBlock.META);}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){return defaultBlockState().setValue(CMComponentBlock.META,context.getHorizontalDirection().getOpposite().get3DDataValue());}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity player,ItemStack stack){if(level.getBlockEntity(pos)instanceof CustomMachineEntity machine){var config=CustomMachinesConfig.byMetadata(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0));if(config!=null){machine.machineType=config.unlocalizedName;machine.init(config);machine.setChanged();}}}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new CustomMachineEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return level.isClientSide?null:createTickerHelper(type,CustomMachineContent.ENTITY.get(),(world,pos,block,machine)->machine.tickServer());}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){if(level.isClientSide)return InteractionResult.SUCCESS;if(player.isShiftKeyDown())return InteractionResult.PASS;if(level.getBlockEntity(pos)instanceof CustomMachineEntity machine){if(machine.checkStructure()){if(player instanceof ServerPlayer server)server.openMenu(machine);}else if(BuiltInRegistries.ITEM.getKey(player.getMainHandItem().getItem()).toString().equals("hbm:wand_s"))machine.buildStructure();}return InteractionResult.CONSUME;}
    private ItemStack item(Level level,BlockPos pos){if(level.getBlockEntity(pos)instanceof CustomMachineEntity machine)return CustomMachineContent.controller(CustomMachinesConfig.niceList.indexOf(machine.config)+100);return ItemStack.EMPTY;}
    @Override public ItemStack getCloneItemStack(LevelReader level,BlockPos pos,BlockState state){if(level.getBlockEntity(pos)instanceof CustomMachineEntity machine&&machine.machineType!=null&&!machine.machineType.isEmpty())return CustomMachineContent.controller(CustomMachinesConfig.niceList.indexOf(machine.config)+100);return new ItemStack(this);}
    @Override public BlockState playerWillDestroy(Level level,BlockPos pos,BlockState state,Player player){if(!level.isClientSide&&!player.getAbilities().instabuild)popResource(level,pos,item(level,pos));return super.playerWillDestroy(level,pos,state,player);}
    @Override public void playerDestroy(Level level,Player player,BlockPos pos,BlockState state,BlockEntity entity,ItemStack tool){player.awardStat(Stats.BLOCK_MINED.get(this));player.causeFoodExhaustion(.025F);}
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder parameters){var entity=parameters.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.BLOCK_ENTITY);return entity instanceof CustomMachineEntity machine?List.of(CustomMachineContent.controller(CustomMachinesConfig.niceList.indexOf(machine.config)+100)):List.of();}
    @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moving){if(!next.is(this)&&level.getBlockEntity(pos)instanceof CustomMachineEntity machine){var random=level.random;for(int i=0;i<22;i++){if(i>=10&&i<=15)continue;var stack=machine.getItem(i);if(stack.isEmpty())continue;float x=random.nextFloat()*.8F+.1F,y=random.nextFloat()*.8F+.1F,z=random.nextFloat()*.8F+.1F;while(!stack.isEmpty()){int amount=Math.min(random.nextInt(21)+10,stack.getCount());var drop=stack.copyWithCount(amount);stack.shrink(amount);var entity=new ItemEntity(level,pos.getX()+x,pos.getY()+y,pos.getZ()+z,drop);entity.setDeltaMovement((float)random.nextGaussian()*.05F,(float)random.nextGaussian()*.05F+.2F,(float)random.nextGaussian()*.05F);level.addFreshEntity(entity);}}level.updateNeighbourForOutputSignal(pos,this);}super.onRemove(state,level,pos,next,moving);}
}
