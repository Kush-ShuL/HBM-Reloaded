package com.hbm.block.ore;

import com.hbm.registry.HbmContent;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Original BlockOutgas and BlockNetherCoal; their registered ores never enable neighbor outgassing. */
public final class OutgassingOreBlock extends LegacyOreBlock {
    private static final Direction[] DIRECTIONS = {Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
    private final MapCodec<OutgassingOreBlock> outgasCodec;
    public OutgassingOreBlock(OreSpec spec) { this(spec, spec.properties()); }
    public OutgassingOreBlock(OreSpec spec, Properties properties) { super(spec, properties); outgasCodec = simpleCodec(ignored -> new OutgassingOreBlock(spec)); }
    @Override protected MapCodec<? extends LegacyOreBlock> codec() { return outgasCodec; }

    public String gasSymbol() {
        if (spec.id().equals("ore_nether_coal")) return "gas_monoxide";
        return spec.id().contains("asbestos") ? "gas_asbestos" : "gas_radon";
    }
    public int legacyTickRate() { return 5; }
    @Override protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockPos destination = pos.relative(DIRECTIONS[random.nextInt(6)]);
        if (level.getBlockState(destination).is(Blocks.AIR)) level.setBlock(destination, HbmContent.block(gasSymbol()).defaultBlockState(), 3);
    }
    @Override protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbor, BlockPos neighborPos, boolean moving) {
        // All nine archive ore constructors pass onNeighbour=false; do not inherit oil's neighbor conversion.
    }
    private boolean silk(Level level, ItemStack tool) {
        return EnchantmentHelper.getItemEnchantmentLevel(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH), tool) > 0;
    }
    @Override public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, BlockEntity entity, ItemStack tool) {
        if (!level.isClientSide && !silk(level, tool)) level.setBlock(pos, HbmContent.block(gasSymbol()).defaultBlockState(), 3);
        super.playerDestroy(level, player, pos, state, entity, tool);
    }
    @Override protected void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack tool, boolean experience) {
        // dropBlockAsItemWithChance is bypassed by the original silk-harvest branch.
        if (!silk(level, tool)) level.setBlock(pos, HbmContent.block(gasSymbol()).defaultBlockState(), 3);
        super.spawnAfterBreak(state, level, pos, tool, experience);
    }
    @Override public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (spec.id().equals("ore_nether_coal")) entity.igniteForSeconds(3);
        else if (gasSymbol().equals("gas_asbestos")) asbestosWalk(level, pos);
    }
    static void asbestosWalk(Level level, BlockPos pos) {
        if (!level.getBlockState(pos.above()).is(Blocks.AIR)) return;
        if (level.random.nextInt(10) == 0) level.setBlock(pos.above(), HbmContent.block("gas_asbestos").defaultBlockState(), 3);
        for (int i = 0; i < 5; i++) level.addParticle(com.hbm.particle.LegacyParticles.AURA.get(), pos.getX() + level.random.nextFloat(), pos.getY() + 1.1, pos.getZ() + level.random.nextFloat(), 0, 0, 0);
    }
    @Override public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!spec.id().equals("ore_nether_coal")) return;
        for (Direction direction : DIRECTIONS) {
            if (direction == Direction.DOWN || !level.getBlockState(pos.relative(direction)).isAir()) continue;
            double x = pos.getX() + .5F + direction.getStepX() + random.nextDouble() - .5D;
            double y = pos.getY() + .5F + direction.getStepY() + random.nextDouble() - .5D;
            double z = pos.getZ() + .5F + direction.getStepZ() + random.nextDouble() - .5D;
            if (direction.getStepX() != 0) x = pos.getX() + .5F + direction.getStepX() * .5 + random.nextDouble() * .125 * direction.getStepX();
            if (direction.getStepY() != 0) y = pos.getY() + .5F + direction.getStepY() * .5 + random.nextDouble() * .125 * direction.getStepY();
            if (direction.getStepZ() != 0) z = pos.getZ() + .5F + direction.getStepZ() * .5 + random.nextDouble() * .125 * direction.getStepZ();
            level.addParticle(ParticleTypes.FLAME, x, y, z, 0, 0, 0);
            level.addParticle(ParticleTypes.SMOKE, x, y, z, 0, 0, 0);
            level.addParticle(ParticleTypes.SMOKE, x, y, z, 0, .1, 0);
        }
    }
}
