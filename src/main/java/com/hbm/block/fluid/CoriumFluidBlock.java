package com.hbm.block.fluid;

import java.util.function.Supplier;
import com.hbm.damage.HbmDamageSources;
import com.hbm.exposure.HbmExposure;
import com.hbm.registry.HbmContent;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/** Original active CoriumFinite class; enabled only once both real cooling products exist. */
public final class CoriumFluidBlock extends LegacyFiniteFluidBlock {
    public CoriumFluidBlock(WorldFluidSpec spec, Supplier<LegacyWorldFluid> fluid) { super(spec, fluid); }
    @Override public boolean canDisplace(LegacyFluidWorld world, int x, int y, int z) {
        float resistance = (float) (Math.sqrt(world.getBlock(x, y, z).getExplosionResistance()) * 3);
        if (resistance < 1) return true;
        java.util.Random random = new java.util.Random();
        return world.material(x, y, z).isLiquid() || random.nextInt((int) resistance) == 0;
    }
    @Override public boolean displaceIfPossible(LegacyFluidWorld world, int x, int y, int z) { return !world.material(x, y, z).isLiquid() && canDisplace(world, x, y, z); }
    @Override protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        entity.makeStuckInBlock(state, new Vec3(.25D, .05D, .25D)); entity.igniteForSeconds(3);
        entity.hurt(HbmDamageSources.source(level, HbmDamageSources.radiation), 2F);
        if (entity instanceof LivingEntity living) HbmExposure.contaminate(living, HbmExposure.Contamination.CREATIVE, 1F);
    }
    @Override public void updateTick(LegacyFluidWorld world, int x, int y, int z, RandomSource random) {
        try { super.updateTick(world, x, y, z, random); }
        catch (ClassCastException error) { if (!world.level().isClientSide) world.setBlock(x, y, z, Blocks.AIR); }
        if (!world.level().isClientSide && random.nextInt(10) == 0 && world.getBlock(x, y - 1, z) != this)
            world.setBlock(x, y, z, HbmContent.block(random.nextInt(3) == 0 ? "block_corium" : "block_corium_cobble"));
    }
}
