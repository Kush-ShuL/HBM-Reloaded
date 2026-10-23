package com.hbm.world.structure.loot;
import com.hbm.block.crate.CrateContent;
import com.hbm.registry.HbmSounds;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.*;
import net.minecraft.world.phys.BlockHitResult;
/** Actual stationary BlockSupplyCrate; normal harvest retains payload, crowbar opens its real list. */
public final class SupplyCrateBlock extends BaseEntityBlock {
    public static final MapCodec<SupplyCrateBlock> CODEC=simpleCodec(properties->new SupplyCrateBlock());
    public SupplyCrateBlock(){super(Properties.of().mapColor(MapColor.WOOD).strength(1,2.5F).sound(SoundType.WOOD).noOcclusion());}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return CODEC;}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new SupplyCrateBlockEntity(pos,state);}
    @Override public boolean onDestroyedByPlayer(BlockState state,Level level,BlockPos pos,Player player,boolean willHarvest,FluidState fluid){if(!level.isClientSide&&!player.getAbilities().instabuild&&willHarvest){var drop=new ItemStack(this);var data=new CompoundTag();if(level.getBlockEntity(pos) instanceof SupplyCrateBlockEntity crate){for(int i=0;i<crate.items.size();i++)data.put("slot"+i,crate.items.get(i).saveOptional(level.registryAccess()));data.putInt("amount",crate.items.size());}if(!data.isEmpty())drop.set(DataComponents.CUSTOM_DATA,CustomData.of(data));level.addFreshEntity(new ItemEntity(level,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,drop));}return level.removeBlock(pos,false);}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){if(level.getBlockEntity(pos) instanceof SupplyCrateBlockEntity crate&&stack.has(DataComponents.CUSTOM_DATA)){var tag=stack.get(DataComponents.CUSTOM_DATA).copyTag();for(int i=0;i<tag.getInt("amount");i++)crate.items.add(ItemStack.parseOptional(level.registryAccess(),tag.getCompound("slot"+i)));crate.setChanged();}super.setPlacedBy(level,pos,state,placer,stack);}
    @Override protected ItemInteractionResult useItemOn(ItemStack held,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){if(!held.is(CrateContent.CROWBAR.get()))return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;if(!level.isClientSide){if(level.getBlockEntity(pos) instanceof SupplyCrateBlockEntity crate)for(var stack:crate.items)Block.popResource(level,pos,stack.copy());level.destroyBlock(pos,false);level.playSound(null,pos.getX(),pos.getY(),pos.getZ(),HbmSounds.get("block.crateBreak"),SoundSource.BLOCKS,.5F,1);}return ItemInteractionResult.sidedSuccess(level.isClientSide);}
}
