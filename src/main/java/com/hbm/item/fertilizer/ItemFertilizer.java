package com.hbm.item.fertilizer;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;

/** X5846 ItemFertilizer; no water-plant spread or extra growth backend. */
public final class ItemFertilizer extends Item {
    public ItemFertilizer() { super(new Properties()); }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        BlockPos center = context.getClickedPos();
        ItemStack stack = context.getItemInHand();
        if (player == null || !player.mayUseItemAt(center, context.getClickedFace(), stack)) {
            return InteractionResult.PASS;
        }
        Level level = context.getLevel();
        // Original explicitly disables capture and leaves it disabled.
        level.captureBlockSnapshots = false;
        boolean didSomething = fertilizeCube(stack, level, center, player);
        if (didSomething && !player.getAbilities().instabuild) stack.shrink(1);
        // The original returns false even after successful growth/consumption.
        return InteractionResult.PASS;
    }

    /** Keep the actual source spelling for dispenser/other real source consumers. */
    public static boolean useFertillizer(ItemStack stack, Level level, int x, int y, int z) {
        if (!(level instanceof ServerLevel server)) return false;
        boolean didSomething = fertilizeCube(stack, level, new BlockPos(x, y, z), FakePlayerFactory.getMinecraft(server));
        if (didSomething) stack.shrink(1);
        return didSomething;
    }

    private static boolean fertilizeCube(ItemStack stack, Level level, BlockPos center, Player player) {
        boolean didSomething = false;
        // Source order is X, then Y, then Z; never short circuit later cells.
        for (int x = center.getX() - 1; x <= center.getX() + 1; x++) {
            for (int y = center.getY() - 1; y <= center.getY() + 1; y++) {
                for (int z = center.getZ() - 1; z <= center.getZ() + 1; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    boolean success = fertilize(level, pos, player, pos.equals(center), stack);
                    didSomething = didSomething || success;
                    if (success && !level.isClientSide) level.levelEvent(2005, pos, 0);
                }
            }
        }
        return didSomething;
    }

    /** Original public helper, used independently by EntityPigeon as well. */
    public static boolean fertilize(Level level, int x, int y, int z, Player player, boolean force) {
        return fertilize(level, new BlockPos(x, y, z), player, force,
            new ItemStack(FertilizerContent.FERTILIZER.get()));
    }

    private static boolean fertilize(Level level, BlockPos pos, Player player, boolean force, ItemStack eventStack) {
        BlockState state = level.getBlockState(pos);
        BonemealEvent event = NeoForge.EVENT_BUS.post(new BonemealEvent(player, level, pos, state, eventStack));
        // NeoForge's successful override is a canceled event with success=true;
        // this is the modern equivalent of the source Result.ALLOW branch.
        if (event.isCanceled() || event.isSuccessful()) return event.isSuccessful();
        if (state.getBlock() instanceof BonemealableBlock growable && event.isValidBonemealTarget()) {
            if (level instanceof ServerLevel server
                && (force || growable.isBonemealSuccess(level, level.random, pos, state))) {
                growable.performBonemeal(server, level.random, pos, state);
            }
            // Valid targets count as success even when their random roll fails.
            return true;
        }
        return false;
    }
}
