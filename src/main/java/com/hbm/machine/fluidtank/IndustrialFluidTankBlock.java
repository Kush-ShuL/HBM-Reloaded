package com.hbm.machine.fluidtank;

import com.hbm.api.machine.IRepairable;
import com.hbm.api.tool.ToolableBlock;
import com.hbm.item.fluid.FluidIdentifier;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;

public final class IndustrialFluidTankBlock extends DummyableBlock implements ToolableBlock {
    private static final MapCodec<IndustrialFluidTankBlock> CODEC=simpleCodec(ignored->new IndustrialFluidTankBlock());
    public IndustrialFluidTankBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,20).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
    @Override protected MapCodec<? extends DummyableBlock> codec(){return CODEC;}
    @Override public int[] dimensions(){return new int[]{2,0,1,1,2,2};}
    @Override public int offset(){return 1;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new IndustrialFluidTankBlockEntity(pos,state):isPort(state)?new IndustrialFluidTankPort(pos,state):null;}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,IndustrialFluidTankContent.ENTITY.get(),IndustrialFluidTankBlockEntity::tick):null;}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(level.isClientSide)return;var core=pos.relative(placementFacing(state),-offset());for(var port:IndustrialFluidTankBlockEntity.portPositions(core))setPort(level,port,true);if(level.getBlockEntity(core) instanceof IndustrialFluidTankBlockEntity machine)machine.restoreItem(stack);}
    private IndustrialFluidTankBlockEntity core(BlockGetter level,BlockPos pos){var position=findCore(level,pos);return position!=null&&level.getBlockEntity(position) instanceof IndustrialFluidTankBlockEntity machine?machine:null;}
    private InteractionResult interact(Level level,BlockPos pos,Player player,ItemStack stack){if(level.isClientSide)return InteractionResult.SUCCESS;var machine=core(level,pos);if(machine==null||machine.hasExploded)return InteractionResult.PASS;if(!player.isShiftKeyDown()){if(player instanceof ServerPlayer server)server.openMenu(machine,buf->buf.writeBlockPos(machine.getBlockPos()));}else if(stack.getItem() instanceof FluidIdentifier identifier){var type=identifier.getType(level,machine.getBlockPos(),stack);machine.tank.setTankType(type);machine.setChanged();player.displayClientMessage(Component.translatable("message.hbm.barrel.changed_type",type.displayName()).withStyle(ChatFormatting.YELLOW),false);}return InteractionResult.CONSUME;}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return interact(level,pos,player,ItemStack.EMPTY);}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){var toolResult=super.useItemOn(stack,state,level,pos,player,hand,hit);if(toolResult!=ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION)return toolResult;return interact(level,pos,player,stack)==InteractionResult.PASS?ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION:ItemInteractionResult.sidedSuccess(level.isClientSide);}
    @Override public boolean onDestroyedByPlayer(BlockState state,Level level,BlockPos pos,Player player,boolean willHarvest,net.minecraft.world.level.material.FluidState fluid){
        // Modern harvesting removes the clicked proxy before playerDestroy. Removing
        // its parent chain here would erase the one authoritative persistent core.
        // Keep it alive until the normal harvest/drop hook has consumed its data.
        if(!level.isClientSide&&willHarvest&&core(level,pos)!=null)return true;
        return super.onDestroyedByPlayer(state,level,pos,player,willHarvest,fluid);
    }
    @Override public void playerDestroy(Level level,Player player,BlockPos pos,BlockState state,BlockEntity original,ItemStack tool){
        var machine=core(level,pos);
        try{
            // The actual core supplies the same persistent item for ordinary and silk harvesting.
            super.playerDestroy(level,player,pos,state,machine!=null?machine:original,tool);
        }finally{
            // Do not remove a replacement placed by a harvest event. No duplicate tank
            // or temporary NBT cache is needed, and the core inventory still drops once.
            if(machine!=null&&core(level,pos)==machine)level.removeBlock(pos,false);
        }
    }
    @Override public ItemStack getCloneItemStack(LevelReader level,BlockPos pos,BlockState state){
        var machine=core(level,pos);return machine==null?super.getCloneItemStack(level,pos,state):machine.dropStack();
    }
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder params){if(params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof IndustrialFluidTankBlockEntity machine)return List.of(machine.dropStack());var origin=params.getOptionalParameter(LootContextParams.ORIGIN);if(origin!=null){var machine=core(params.getLevel(),BlockPos.containing(origin));if(machine!=null)return List.of(machine.dropStack());}return List.of();}
    @Override public boolean canDropFromExplosion(BlockState state,BlockGetter level,BlockPos pos,Explosion explosion){return false;}
    @Override public void onBlockExploded(BlockState state,Level level,BlockPos pos,Explosion explosion){var machine=core(level,pos);if(machine==null||machine.lastExplosion==explosion)return;machine.lastExplosion=explosion;if(!machine.hasExploded){machine.explode();if(machine.tank.getTankType().hasTrait("flammable"))com.hbm.advancement.HbmAdvancements.zetaIgnited(level,pos,explosion);}else level.removeBlock(machine.getBlockPos(),false);}
    @Override protected boolean hasAnalogOutputSignal(BlockState state){return true;}
    @Override protected int getAnalogOutputSignal(BlockState state,Level level,BlockPos pos){var machine=state.getValue(LEGACY_META)>=6?core(level,pos):null;return machine==null?0:machine.comparatorPower();}
    @Override public boolean onScrew(Level level,Player player,BlockPos pos,Direction side,float x,float y,float z,ToolType tool){return tool==ToolType.TORCH&&IRepairable.tryRepairMultiblock(level,pos,this,player);}
}
