package com.hbm.machine.barrel;

import com.hbm.item.fluid.FluidIdentifier;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;

public final class BarrelBlock extends BaseEntityBlock {
    public final BarrelContent.Variant variant;
    private static final VoxelShape SHAPE=Block.box(2,0,2,14,16,14);
    public BarrelBlock(BarrelContent.Variant variant){super(Properties.of().mapColor(MapColor.METAL).strength(2,5).sound(variant.sound).requiresCorrectToolForDrops().noOcclusion());this.variant=variant;}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return MapCodec.unit(this);}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return SHAPE;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new BarrelBlockEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,BarrelContent.ENTITY.get(),BarrelBlockEntity::tick);}
    private InteractionResult interact(Level level,BlockPos pos,Player player,ItemStack stack){
        if(variant==BarrelContent.Variant.CORRODED)return InteractionResult.PASS;
        if(!level.isClientSide&&level.getBlockEntity(pos) instanceof BarrelBlockEntity machine){
            if(!player.isShiftKeyDown()){if(player instanceof ServerPlayer server)server.openMenu(machine,buf->buf.writeBlockPos(pos));}
            else if(stack.getItem() instanceof FluidIdentifier identifier){var type=identifier.getType(level,pos,stack);machine.tank.setTankType(type);machine.setChanged();player.displayClientMessage(Component.translatable("message.hbm.barrel.changed_type",type.displayName()).withStyle(ChatFormatting.YELLOW),false);}
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return interact(level,pos,player,ItemStack.EMPTY);}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){return interact(level,pos,player,stack)==InteractionResult.PASS?ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION:ItemInteractionResult.sidedSuccess(level.isClientSide);}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(level.getBlockEntity(pos) instanceof BarrelBlockEntity machine){machine.restoreItem(stack);if(stack.has(net.minecraft.core.component.DataComponents.CUSTOM_NAME))machine.setCustomName(stack.getHoverName());}}
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder params){var machine=params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);return machine instanceof BarrelBlockEntity barrel?List.of(barrel.dropStack()):List.of();}
    @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moved){
        if(!state.is(next.getBlock())&&level.getBlockEntity(pos) instanceof BarrelBlockEntity machine){if(variant!=BarrelContent.Variant.CORRODED&&!level.isClientSide)dropInventory(level,pos,machine);level.updateNeighbourForOutputSignal(pos,this);}
        super.onRemove(state,level,pos,next,moved);
    }
    private static void dropInventory(Level level,BlockPos pos,BarrelBlockEntity machine){
        var random=level.random;for(int slot=0;slot<machine.getContainerSize();slot++){
            var stack=machine.getItem(slot);if(stack.isEmpty())continue;
            float x=random.nextFloat()*.8F+.1F,y=random.nextFloat()*.8F+.1F,z=random.nextFloat()*.8F+.1F;
            while(!stack.isEmpty()){var dropped=stack.split(Math.min(stack.getCount(),random.nextInt(21)+10));var item=new ItemEntity(level,pos.getX()+x,pos.getY()+y,pos.getZ()+z,dropped);item.setDeltaMovement((float)random.nextGaussian()*.05F,(float)random.nextGaussian()*.05F+.2F,(float)random.nextGaussian()*.05F);level.addFreshEntity(item);}
        }
    }
    @Override protected boolean hasAnalogOutputSignal(BlockState state){return true;}
    @Override protected int getAnalogOutputSignal(BlockState state,Level level,BlockPos pos){return level.getBlockEntity(pos) instanceof BarrelBlockEntity machine?machine.comparatorPower():0;}
}
