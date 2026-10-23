package com.hbm.block;

import com.hbm.particle.LegacyParticles;
import com.hbm.registry.HbmContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** The nine non-ore BlockOutgas registrations retain their actual gas and break behavior. */
public final class LegacyOutgassingBlock extends Block {
    private static final Direction[] DIRECTIONS = {Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
    private final String symbol;
    private final boolean randomTick;
    private final int rate;
    private final boolean onBreak;
    private final boolean onNeighbour;

    public LegacyOutgassingBlock(Properties properties, String symbol, boolean randomTick, int rate, boolean onBreak, boolean onNeighbour) {
        super(randomTick ? properties.randomTicks() : properties);
        this.symbol = symbol;
        this.randomTick = randomTick;
        this.rate = rate;
        this.onBreak = onBreak;
        this.onNeighbour = onNeighbour;
    }

    public int legacyTickRate() { return rate; }
    private BlockState gas() {
        String id = symbol.equals("block_corium_cobble") ? "gas_radon"
                : symbol.equals("ancient_scrap") ? "gas_radon_tomb" : "gas_asbestos";
        return HbmContent.block(id).defaultBlockState();
    }

    @Override protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockPos target = pos.relative(DIRECTIONS[random.nextInt(6)]);
        if (level.getBlockState(target).is(Blocks.AIR)) level.setBlock(target, gas(), 3);
    }
    @Override protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) { tick(state, level, pos, random); }

    @Override protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbor, BlockPos neighborPos, boolean moving) {
        if (onNeighbour && level.random.nextInt(3) == 0) {
            for (Direction direction : DIRECTIONS) {
                BlockPos target = pos.relative(direction);
                if (level.getBlockState(target).is(Blocks.AIR)) level.setBlock(target, gas(), 3);
            }
        }
    }

    @Override public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!randomTick || symbol.equals("block_corium_cobble") || symbol.equals("ancient_scrap")) return;
        if (level.getBlockState(pos.above()).is(Blocks.AIR)) {
            if (level.random.nextInt(10) == 0) level.setBlock(pos.above(), gas(), 3);
            for (int i = 0; i < 5; i++) level.addParticle(LegacyParticles.AURA.get(), pos.getX() + level.random.nextFloat(), pos.getY() + 1.1, pos.getZ() + level.random.nextFloat(), 0, 0, 0);
        }
    }

    @Override protected void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack tool, boolean experience) {
        // Vanilla's original silk branch bypasses dropBlockAsItemWithChance altogether.
        int silk = EnchantmentHelper.getItemEnchantmentLevel(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH), tool);
        if (onBreak && silk == 0) level.setBlock(pos, gas(), 3);
        super.spawnAfterBreak(state, level, pos, tool, experience);
    }

    @Override protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState replacement, boolean moving) {
        super.onRemove(state, level, pos, replacement, moving);
        if (replacement.is(this) || !symbol.equals("ancient_scrap")) return;
        for (int x = -2; x <= 2; x++) for (int y = -2; y <= 2; y++) for (int z = -2; z <= 2; z++) {
            // The archive tests the absolute signed sum, not Manhattan distance.
            int sum = Math.abs(x + y + z);
            BlockPos target = pos.offset(x, y, z);
            if (sum < 5 && sum > 0 && level.getBlockState(target).is(Blocks.AIR)) level.setBlock(target, gas(), 3);
        }
    }
}
