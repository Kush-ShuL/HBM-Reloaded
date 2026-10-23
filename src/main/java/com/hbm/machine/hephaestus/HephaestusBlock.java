package com.hbm.machine.hephaestus;
import com.hbm.item.fluid.FluidIdentifier;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
/** Actual 3x12x3 Dummyable, with fluid-only proxy cells at both ends. */
public final class HephaestusBlock extends DummyableBlock {
    public HephaestusBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(10,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
    @Override protected MapCodec<? extends HephaestusBlock> codec(){return MapCodec.unit(this);}
    @Override public int[] dimensions(){return new int[]{11,0,1,1,1,1};}
    @Override public int offset(){return 1;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new HephaestusEntity(pos,state):isPort(state)?new HephaestusPort(pos,state):null;}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,HephaestusContent.ENTITY.get(),HephaestusEntity::tick):null;}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){
        super.setPlacedBy(level,pos,state,placer,stack);
        if(!level.isClientSide){var core=pos.relative(placementFacing(state),-offset());for(var c:HephaestusEntity.connections(core))setPort(level,c.origin(),true);}
    }
    private InteractionResult interact(Level level,BlockPos pos,Player player,ItemStack stack){
        if(level.isClientSide||player.isShiftKeyDown())return InteractionResult.SUCCESS;
        if(!(stack.getItem() instanceof FluidIdentifier identifier))return InteractionResult.PASS;
        var p=findCore(level,pos);
        if(p==null||!(level.getBlockEntity(p) instanceof HephaestusEntity machine))return InteractionResult.PASS;
        var type=identifier.getType(level,p,stack);
        // No new whitelist here: original setupTanks clears invalid types on the following tick.
        machine.input.setTankType(type);machine.setChanged();
        player.displayClientMessage(Component.translatable("message.hbm.hephaestus.changed_type",type.displayName()).withStyle(ChatFormatting.YELLOW),false);
        return InteractionResult.CONSUME;
    }
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return interact(level,pos,player,ItemStack.EMPTY);}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){
        var tool=super.useItemOn(stack,state,level,pos,player,hand,hit);
        if(tool!=ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION)return tool;
        return interact(level,pos,player,stack)==InteractionResult.PASS?ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION:ItemInteractionResult.sidedSuccess(level.isClientSide);
    }
}
