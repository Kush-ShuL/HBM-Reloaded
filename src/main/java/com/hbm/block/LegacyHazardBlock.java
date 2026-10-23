package com.hbm.block;

import com.hbm.hazard.HazardType;
import com.hbm.hazard.HbmHazards;
import com.hbm.particle.LegacyParticles;
import com.hbm.radiation.HbmRadiation;
import com.hbm.registry.HbmContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

/** X5846 BlockHazard, BlockHotHazard and BlockNuclearWaste share a scheduled radiation source. */
public final class LegacyHazardBlock extends Block {
    private static final Direction[] DIRECTIONS = {Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
    private final String effect;
    private final boolean hot;
    private final boolean nuclearWaste;
    // The old block stores this on its shared block instance, not in a tile or blockstate.
    private float radiation;

    public LegacyHazardBlock(Properties properties, String effect, boolean hot, boolean nuclearWaste) {
        super(properties);
        this.effect = effect;
        this.hot = hot;
        this.nuclearWaste = nuclearWaste;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState previous, boolean moving) {
        super.onPlace(state, level, pos, previous, moving);
        if (previous.is(this)) return;
        radiation = (float) HbmHazards.registry().evaluate(new ItemStack(this)).firstLevel(HazardType.RADIATION)
                .orElseThrow(() -> new IllegalStateException("Unresolved placed-block radiation: " + this)) * .1F;
        if (radiation > 0 && !level.isClientSide) level.scheduleTick(pos, this, 20);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (nuclearWaste) {
            Direction direction = DIRECTIONS[random.nextInt(6)];
            BlockPos gas = pos.relative(direction);
            if (random.nextInt(2) == 0 && level.getBlockState(gas).is(Blocks.AIR)) {
                level.setBlock(gas, HbmContent.block("gas_radon_dense").defaultBlockState(), 3);
            }
        }
        if (radiation > 0) {
            HbmRadiation.incrementRad(level, pos.getX(), pos.getY(), pos.getZ(), radiation);
            level.scheduleTick(pos, this, 20);
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (effect != null) {
            switch (effect) {
                case "RADFOG", "SCHRAB", "FLAMES" -> particles(level, pos, random);
                case "LAVAPOP" -> level.addParticle(ParticleTypes.LAVA, pos.getX() + random.nextFloat(), pos.getY() + 1.1F, pos.getZ() + random.nextFloat(), 0, 0, 0);
                // SPARKS is an intentionally empty branch in the supplied source.
                default -> { }
            }
        }
        if (!hot) return;
        if (level.isRainingAt(pos.above())) {
            float x = random.nextFloat(), z = random.nextFloat();
            level.addParticle(ParticleTypes.CLOUD, pos.getX() + x, pos.getY() + 1, pos.getZ() + z, 0, 0, 0);
        }
        for (Direction direction : DIRECTIONS) {
            if (direction == Direction.DOWN || !level.getFluidState(pos.relative(direction)).is(Fluids.WATER)
                    && !level.getFluidState(pos.relative(direction)).is(Fluids.FLOWING_WATER)) continue;
            double x = pos.getX() + .5F + direction.getStepX() + random.nextDouble() - .5D;
            double y = pos.getY() + .5F + direction.getStepY() + random.nextDouble() - .5D;
            double z = pos.getZ() + .5F + direction.getStepZ() + random.nextDouble() - .5D;
            if (direction.getStepX() != 0) x = pos.getX() + .5F + direction.getStepX() * .5 + random.nextDouble() * .125 * direction.getStepX();
            if (direction.getStepY() != 0) y = pos.getY() + .5F + direction.getStepY() * .5 + random.nextDouble() * .125 * direction.getStepY();
            if (direction.getStepZ() != 0) z = pos.getZ() + .5F + direction.getStepZ() * .5 + random.nextDouble() * .125 * direction.getStepZ();
            level.addParticle(ParticleTypes.CLOUD, x, y, z, 0, 0, 0);
        }
    }

    private void particles(Level level, BlockPos pos, RandomSource random) {
        for (Direction direction : DIRECTIONS) {
            if (direction == Direction.DOWN && effect.equals("FLAMES")) continue;
            if (!level.getBlockState(pos.relative(direction)).isAir()) continue;
            double x = pos.getX() + .5F + direction.getStepX() + random.nextDouble() * 3 - 1.5D;
            double y = pos.getY() + .5F + direction.getStepY() + random.nextDouble() * 3 - 1.5D;
            double z = pos.getZ() + .5F + direction.getStepZ() + random.nextDouble() * 3 - 1.5D;
            if (direction.getStepX() != 0) x = pos.getX() + .5F + direction.getStepX() * .5 + random.nextDouble() * direction.getStepX();
            if (direction.getStepY() != 0) y = pos.getY() + .5F + direction.getStepY() * .5 + random.nextDouble() * direction.getStepY();
            if (direction.getStepZ() != 0) z = pos.getZ() + .5F + direction.getStepZ() * .5 + random.nextDouble() * direction.getStepZ();
            if (effect.equals("RADFOG")) level.addParticle(LegacyParticles.AURA.get(), x, y, z, 0, 0, 0);
            if (effect.equals("SCHRAB")) level.addParticle(LegacyParticles.SCHRAB_AURA.get(), x, y, z, 0, 0, 0);
            if (effect.equals("FLAMES")) {
                level.addParticle(ParticleTypes.FLAME, x, y, z, 0, 0, 0);
                level.addParticle(ParticleTypes.SMOKE, x, y, z, 0, 0, 0);
                level.addParticle(ParticleTypes.SMOKE, x, y, z, 0, .1, 0);
            }
        }
    }
}
