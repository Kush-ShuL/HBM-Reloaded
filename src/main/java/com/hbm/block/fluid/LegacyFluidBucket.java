package com.hbm.block.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

/** Original ItemModBucket placement uses Material.isSolid, not modern fluid replacement rules. */
public final class LegacyFluidBucket extends Item {
    private final LegacyWorldFluid fluid;
    public LegacyFluidBucket(LegacyWorldFluid fluid) { super(new Properties().stacksTo(1).craftRemainder(Items.BUCKET)); this.fluid = fluid; }
    public LegacyWorldFluid nativeWorldFluid() { return fluid; }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        BlockHitResult hit = getPlayerPOVHitResult(level, player, net.minecraft.world.level.ClipContext.Fluid.NONE);
        if (hit.getType() != net.minecraft.world.phys.HitResult.Type.BLOCK) return InteractionResultHolder.pass(held);
        BlockPos pos = hit.getBlockPos().relative(hit.getDirection());
        if (!level.mayInteract(player, hit.getBlockPos()) || !player.mayUseItemAt(pos, hit.getDirection(), held)) return InteractionResultHolder.fail(held);
        if (!place(level, pos)) return InteractionResultHolder.fail(held);
        level.playSound(player, pos, net.minecraft.sounds.SoundEvents.BUCKET_EMPTY, net.minecraft.sounds.SoundSource.BLOCKS, 1F, 1F);
        level.gameEvent(player, net.minecraft.world.level.gameevent.GameEvent.FLUID_PLACE, pos);
        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) net.minecraft.advancements.CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, pos, held);
        player.awardStat(net.minecraft.stats.Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(player.hasInfiniteMaterials() ? held : new ItemStack(Items.BUCKET), level.isClientSide);
    }
    private boolean place(Level level, BlockPos pos) {
        var state = level.getBlockState(pos);
        LegacyMaterial material = LegacyMaterials.of(state);
        if (!state.isAir() && material.isSolid()) return false;
        if (!level.isClientSide && !material.isSolid() && !material.isLiquid()) level.destroyBlock(pos, true);
        level.setBlock(pos, fluid.defaultFluidState().createLegacyBlock().setValue(LegacyFluidBlock.LEVEL, 0), 3);
        return true;
    }
}
