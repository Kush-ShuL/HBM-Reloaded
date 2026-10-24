package com.hbm.block.fluid;

import java.util.function.Supplier;
import com.hbm.exposure.HbmExposure;
import com.hbm.registry.HbmContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** Original VolcanicBlock and RadBlock; RNG evaluation order and metadata are retained. */
public final class VolcanicFluidBlock extends LegacyClassicFluidBlock {
    private final boolean radioactive;
    public VolcanicFluidBlock(WorldFluidSpec spec, Supplier<LegacyWorldFluid> fluid) { super(spec, fluid); radioactive = spec.originalClass().equals("RadBlock"); }
    private Block basalt() { return radioactive ? HbmWorldFluids.block("sellafield_slaked") : HbmContent.block("basalt"); }
    private Block reaction(LegacyFluidWorld world, BlockPos pos) {
        BlockState state = world.access.getBlockState(pos);
        Block block = state.getBlock();
        String id = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block).toString();
        if (LegacyMaterials.of(state) == LegacyMaterial.water) return Blocks.STONE;
        // Modern flattened members of the exact two old log/leaves and one planks block.
        if (id.startsWith("minecraft:") && java.util.Set.of("oak_log", "spruce_log", "birch_log", "jungle_log", "acacia_log", "dark_oak_log", "oak_wood", "spruce_wood", "birch_wood", "jungle_wood", "acacia_wood", "dark_oak_wood").contains(id.substring(10))) return HbmWorldFluids.block("waste_log");
        if (id.startsWith("minecraft:") && java.util.Set.of("oak_planks", "spruce_planks", "birch_planks", "jungle_planks", "acacia_planks", "dark_oak_planks").contains(id.substring(10))) return HbmWorldFluids.block("waste_planks");
        if (id.startsWith("minecraft:") && java.util.Set.of("oak_leaves", "spruce_leaves", "birch_leaves", "jungle_leaves", "acacia_leaves", "dark_oak_leaves").contains(id.substring(10))) return Blocks.FIRE;
        if (block == Blocks.DIAMOND_ORE) return radioactive ? HbmContent.block("ore_sellafield_radgem") : com.hbm.registry.HbmOres.resourceBlock("ore_basalt", 3);
        if (radioactive && (block == HbmContent.block("ore_uranium") || block == HbmContent.block("ore_gneiss_uranium")))
            return HbmContent.block(world.level().random.nextInt(5) == 0 ? "ore_sellafield_schrabidium" : "ore_sellafield_uranium_scorched");
        return null;
    }
    @Override protected void neighborReaction(LegacyFluidWorld world, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            BlockPos target = pos.relative(direction); Block product = reaction(world, target);
            if (product != null) world.setBlock(target.getX(), target.getY(), target.getZ(), product, 0, 3);
        }
    }
    @Override public void updateTick(LegacyFluidWorld world, int x, int y, int z, RandomSource random) {
        super.updateTick(world, x, y, z, random);
        int lava = 0, stone = 0;
        for (Direction direction : Direction.values()) {
            BlockPos pos = new BlockPos(x, y, z).relative(direction);
            Block block = world.getBlock(pos.getX(), pos.getY(), pos.getZ());
            if (block == this) lava++;
            if (block == basalt()) stone++;
        }
        if (!world.level().isClientSide && ((!isSourceBlock(world, x, y, z) && lava < 2) || random.nextInt(5) == 0 && lava < 5) && world.getBlock(x, y - 1, z) != this)
            solidify(world, x, y, z, lava, stone, random);
    }
    private void solidify(LegacyFluidWorld world, int x, int y, int z, int lava, int stone, RandomSource random) {
        int roll = random.nextInt(radioactive ? 400 : 200);
        Block above = world.getBlock(x, y + 10, z);
        boolean gem = lava + stone == 6 && lava < 3 && (above == basalt() || above == this);
        int metadata = radioactive ? 5 + random.nextInt(3) : 0;
        Block product;
        if (radioactive) product = roll < 2 ? HbmContent.block("ore_sellafield_diamond") : roll == 2 ? HbmContent.block("ore_sellafield_emerald")
                : roll < 20 && gem ? HbmContent.block("ore_sellafield_radgem") : basalt();
        else {
            product = null; // Select the fixed source mineral after the unchanged roll gates.
            if (roll < 2) metadata = 0;
            else if (roll == 2) metadata = 1;
            else if (roll == 3) metadata = 2;
            else if (roll == 4) metadata = 4;
            else if (roll < 15 && gem) metadata = 3;
            else product = basalt();
            if (product == null) product = com.hbm.registry.HbmOres.resourceBlock("ore_basalt", metadata);
        }
        world.setBlock(x, y, z, product, metadata, 3);
    }
    @Override public boolean canDisplace(LegacyFluidWorld world, int x, int y, int z) {
        var state = world.state(x, y, z); BlockPos pos = new BlockPos(x, y, z);
        for (Direction direction : Direction.values()) if (state.getFlammability(world.access, pos, direction) > 0) return true;
        if (LegacyMaterials.of(state).isReplaceable()) return true;
        return super.canDisplace(world, x, y, z);
    }
    @Override public boolean displaceIfPossible(LegacyFluidWorld world, int x, int y, int z) { return super.displaceIfPossible(world, x, y, z) || canDisplace(world, x, y, z); }
    @Override protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (radioactive && entity instanceof LivingEntity living) HbmExposure.contaminate(living, HbmExposure.Contamination.CREATIVE, 5F);
    }
    @Override public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (level.getBlockState(pos.above()).isAir() && !level.getBlockState(pos.above()).isSolidRender(level, pos.above())) {
            if (random.nextInt(100) == 0) {
                double x = (double) ((float) pos.getX() + random.nextFloat()), y = pos.getY() + 1D, z = (double) ((float) pos.getZ() + random.nextFloat());
                level.addParticle(ParticleTypes.LAVA, x, y, z, 0, 0, 0);
                level.playLocalSound(x, y, z, SoundEvents.LAVA_POP, SoundSource.BLOCKS, .2F + random.nextFloat() * .2F, .9F + random.nextFloat() * .15F, false);
            }
            if (random.nextInt(200) == 0) level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.LAVA_AMBIENT, SoundSource.BLOCKS, .2F + random.nextFloat() * .2F, .9F + random.nextFloat() * .15F, false);
        }
        if (random.nextInt(10) == 0 && level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP) && !LegacyMaterials.of(level.getBlockState(pos.below(2))).blocksMovement())
            level.addParticle(ParticleTypes.DRIPPING_LAVA, (double) ((float) pos.getX() + random.nextFloat()), pos.getY() - 1.05D, (double) ((float) pos.getZ() + random.nextFloat()), 0, 0, 0);
    }
}
