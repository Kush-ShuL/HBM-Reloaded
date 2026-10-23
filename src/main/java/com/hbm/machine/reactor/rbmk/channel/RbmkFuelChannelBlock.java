package com.hbm.machine.reactor.rbmk.channel;
import com.hbm.machine.reactor.rbmk.column.RbmkColumnBlock;
import com.hbm.machine.reactor.rbmk.fuel.RbmkFuelRodItem;
import com.hbm.machine.reactor.rbmk.residue.RbmkLidItem;
import com.hbm.registry.HbmSounds;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;
/** Source RBMKRod/RBMKRodReaSim: actual direct one-rod insertion, hot-break failure and inventory-only top proxy. */
public final class RbmkFuelChannelBlock extends RbmkColumnBlock {
    public final RbmkChannelKind kind;
    public RbmkFuelChannelBlock(RbmkChannelKind kind) { this.kind = kind; }
    @Override protected MapCodec<? extends RbmkFuelChannelBlock> codec() { return MapCodec.unit(this); }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return isCore(state) ? new RbmkFuelChannelEntity(pos, state) : isPort(state) ? new RbmkChannelPort(pos, state) : null; }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) { return isCore(state) ? createTickerHelper(type, RbmkFuelChannels.ENTITY.get(), RbmkFuelChannelEntity::tick) : null; }
    @Override protected int getLightBlock(BlockState state, BlockGetter world, BlockPos pos) { return 0; }
    @Override protected boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) { return true; }
    @Override protected float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) { return 1F; }
    @Override protected VoxelShape getOcclusionShape(BlockState state, BlockGetter world, BlockPos pos) { return Shapes.empty(); }
    private static void mark(Player player) {
        if (!player.level().isClientSide) { var root = player.getPersistentData(); var persisted = root.getCompound(Player.PERSISTED_NBT_TAG); persisted.putLong("fbiMark", player.level().getGameTime() + 20 * 60 * 20); root.put(Player.PERSISTED_NBT_TAG, persisted); }
    }
    @Override protected ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level world, BlockPos pos, Player player, net.minecraft.world.InteractionHand hand, BlockHitResult hit) {
        mark(player); var machine = core(world, pos);
        if (!(machine instanceof RbmkFuelChannelEntity channel)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (held.getItem() instanceof RbmkLidItem && !channel.hasLid()) return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        if (held.getItem() instanceof RbmkFuelRodItem && channel.isEmpty()) {
            if (!world.isClientSide) { channel.setItem(0, held.copyWithCount(1)); if (!player.getAbilities().instabuild) held.shrink(1); world.playSound(null, pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,HbmSounds.get("item.upgradePlug"),SoundSource.BLOCKS,1,1); }
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }
        return super.useItemOn(held, state, world, pos, player, hand, hit);
    }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) { mark(player); return super.useWithoutItem(state, world, pos, player, hit); }
    @Override protected void onRemove(BlockState state, Level world, BlockPos pos, BlockState replacement, boolean moving) {
        if (!world.isClientSide && !replacement.is(this) && isCore(state) && world.getBlockEntity(pos) instanceof RbmkFuelChannelEntity channel) channel.broken();
        super.onRemove(state, world, pos, replacement, moving);
    }
}
