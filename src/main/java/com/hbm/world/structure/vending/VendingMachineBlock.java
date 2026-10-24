package com.hbm.world.structure.vending;

import com.hbm.multiblock.DummyableBlock;
import com.hbm.registry.HbmSounds;
import com.hbm.world.structure.loot.LegacyStructureLoot;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;

/** X5846 BlockVendingMachine: two-cell token-operated ItemPool dispenser, not an inventory machine. */
public final class VendingMachineBlock extends DummyableBlock {
    public VendingMachineBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return MapCodec.unit(this);}
    @Override public int[] dimensions(){return new int[]{1,0,0,0,0,0};}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new VendingMachineBlockEntity(pos,state):null;}
    public int variant(BlockGetter level,BlockPos core){var above=level.getBlockState(core.above());return above.hasProperty(LEGACY_META)&&above.getValue(LEGACY_META)>=6?1:0;}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){
        super.setPlacedBy(level,pos,state,placer,stack);
        if(!level.isClientSide&&VendingMachineItem.variant(stack)==1){var above=pos.above();var top=level.getBlockState(above);if(top.is(this)&&top.getValue(LEGACY_META)<=5)safely(()->level.setBlock(above,top.setValue(LEGACY_META,top.getValue(LEGACY_META)+6),UPDATE_ALL));}
    }
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){
        if(!BuiltInRegistries.ITEM.getKey(stack.getItem()).equals(VendingMachineContent.id("coin_token")))return super.useItemOn(stack,state,level,pos,player,hand,hit);
        // Source spends the coin before its remote/core checks, including creative mode.
        stack.shrink(1);
        if(level.isClientSide)return ItemInteractionResult.SUCCESS;
        var core=findCore(level,pos);
        if(core!=null){
            var drop=LegacyStructureLoot.sample((ServerLevel)level,variant(level,core)==0?"POOL_SODA":"POOL_SNACKS",level.random);
            if(!drop.isEmpty()){var direction=facing(level.getBlockState(core));level.addFreshEntity(new ItemEntity(level,pos.getX()+.5+direction.getStepX()*.75,core.getY()+.25,pos.getZ()+.5+direction.getStepZ()*.75,drop));}
            level.playSound(null,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,HbmSounds.get("hbm:weapon.reload.boltOpen"),SoundSource.BLOCKS,1,.75F);
        }
        return ItemInteractionResult.CONSUME;
    }
    @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moved){
        // Modern player destruction evaluates block loot after removal. Preserve only that call's source metadata observation.
        if(!next.is(this)&&isCore(state)&&level.getBlockEntity(pos)instanceof VendingMachineBlockEntity machine)machine.removedVariant=variant(level,pos);
        super.onRemove(state,level,pos,next,moved);
    }
    private int droppedVariant(BlockState state,LootParams.Builder context){
        var origin=context.getOptionalParameter(LootContextParams.ORIGIN);
        if(origin!=null){var core=findCore(context.getLevel(),BlockPos.containing(origin));if(core!=null)return variant(context.getLevel(),core);}
        if(context.getOptionalParameter(LootContextParams.BLOCK_ENTITY)instanceof VendingMachineBlockEntity machine&&machine.removedVariant>=0)return machine.removedVariant;
        return isPort(state)?1:0;
    }
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){return List.of(VendingMachineItem.stack(droppedVariant(state,context)));}
    @Override public ItemStack getCloneItemStack(LevelReader level,BlockPos pos,BlockState state){var core=findCore(level,pos);return VendingMachineItem.stack(core==null?(isPort(state)?1:0):variant(level,core));}
}
