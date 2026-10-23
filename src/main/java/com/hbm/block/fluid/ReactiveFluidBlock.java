package com.hbm.block.fluid;

import java.util.Set;
import java.util.function.Supplier;
import com.hbm.damage.HbmDamageSources;
import com.hbm.exposure.HbmExposure;
import com.hbm.particle.LegacyParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/** X5846 Mud/Acid/Toxic/Schrabidic/Generic fluid subclass behavior. */
public final class ReactiveFluidBlock extends LegacyClassicFluidBlock {
    private static final Direction[] REACTION_ORDER = {Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN, Direction.SOUTH, Direction.NORTH};
    private final java.util.Random reactionRandom = new java.util.Random();
    public ReactiveFluidBlock(WorldFluidSpec spec, Supplier<LegacyWorldFluid> fluid) { super(spec, fluid); }
    private boolean generic() { return spec.originalClass().equals("GenericFluidBlock"); }
    @Override public boolean canDisplace(LegacyFluidWorld world, int x, int y, int z) { return (generic() || !world.material(x, y, z).isLiquid()) && super.canDisplace(world, x, y, z); }
    @Override public boolean displaceIfPossible(LegacyFluidWorld world, int x, int y, int z) { return (generic() || !world.material(x, y, z).isLiquid()) && super.displaceIfPossible(world, x, y, z); }
    @Override protected void neighborReaction(LegacyFluidWorld world, BlockPos pos) {
        Direction[] order = spec.originalClass().equals("SchrabidicBlock") ? Direction.values() : REACTION_ORDER;
        for (Direction direction : order) {
            BlockPos target = pos.relative(direction); BlockState state = world.access.getBlockState(target);
            if (spec.originalClass().equals("AcidBlock")) { if (!state.is(this)) world.setBlock(target.getX(), target.getY(), target.getZ(), Blocks.AIR); }
            else if (state.getBlock() != this && LegacyMaterials.of(state).isLiquid()) {
                if (spec.originalClass().equals("MudBlock")) world.setBlock(target.getX(), target.getY(), target.getZ(), Blocks.AIR);
                else if (!generic()) {
                    world.setBlock(pos.getX(), pos.getY(), pos.getZ(), HbmWorldFluids.block("sellafield_slaked"));
                    if (spec.originalClass().equals("SchrabidicBlock")) break;
                }
            }
        }
    }
    @Override public void updateTick(LegacyFluidWorld world, int x, int y, int z, RandomSource random) {
        super.updateTick(world, x, y, z, random);
        if (spec.originalClass().equals("AcidBlock")) neighborReaction(world, new BlockPos(x, y, z));
        else if (spec.originalClass().equals("MudBlock")) for (Direction direction : REACTION_ORDER) mudReaction(world, new BlockPos(x, y, z).relative(direction));
    }
    private void mudReaction(LegacyFluidWorld world, BlockPos pos) {
        BlockState state = world.access.getBlockState(pos); Block block = state.getBlock();
        if (block == this) return;
        String id = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block).toString();
        Block product = null; int chance = 0;
        if (block == Blocks.STONE || block == Blocks.STONE_BRICK_STAIRS || block == Blocks.STONE_BRICKS || block == Blocks.SMOOTH_STONE_SLAB || block == Blocks.STONE_SLAB || block == Blocks.SANDSTONE_SLAB || block == Blocks.PETRIFIED_OAK_SLAB || block == Blocks.COBBLESTONE_SLAB || block == Blocks.BRICK_SLAB || block == Blocks.STONE_BRICK_SLAB || block == Blocks.NETHER_BRICK_SLAB || block == Blocks.QUARTZ_SLAB) { chance = 20; product = Blocks.COBBLESTONE; }
        else if (block == Blocks.COBBLESTONE) { chance = 15; product = Blocks.GRAVEL; }
        else if (block == Blocks.SANDSTONE || block == Blocks.CHISELED_SANDSTONE || block == Blocks.CUT_SANDSTONE) { chance = 5; product = Blocks.SAND; }
        else if (block == Blocks.TERRACOTTA || id.startsWith("minecraft:") && id.endsWith("_terracotta") && !id.endsWith("_glazed_terracotta")) { chance = 10; product = Blocks.CLAY; }
        if (product != null) { if (reactionRandom.nextInt(chance) == 0) world.setBlock(pos.getX(), pos.getY(), pos.getZ(), product); return; }
        if (Set.of("wood", "cactus", "cake", "circuits", "cloth", "coral", "craftedSnow", "glass", "gourd", "ice", "leaves", "packedIce", "piston", "plants", "portal", "redstoneLight", "snow", "sponge", "vine", "web").contains(LegacyMaterials.of(state).name()) || block.getExplosionResistance() < 1.2F)
            world.setBlock(pos.getX(), pos.getY(), pos.getZ(), Blocks.AIR);
    }
    @Override protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (generic()) { sulfuricCollision(level, entity); return; }
        entity.makeStuckInBlock(state, new Vec3(.25D, .05D, .25D));
        switch (spec.originalClass()) {
            case "MudBlock" -> { if (!(entity instanceof Player player && WorldFluidHooks.checkForHazmat(player))) entity.hurt(HbmDamageSources.source(level, HbmDamageSources.mudPoisoning), 8F); }
            case "AcidBlock" -> entity.hurt(HbmDamageSources.source(level, HbmDamageSources.acid), 10000F);
            default -> { if (entity instanceof LivingEntity living) HbmExposure.contaminate(living, HbmExposure.Contamination.CREATIVE, 1F); }
        }
    }
    private void sulfuricCollision(Level level, Entity entity) {
        if (entity instanceof ItemEntity item) {
            entity.setDeltaMovement(Vec3.ZERO);
            if (entity.tickCount % 20 == 0 && !level.isClientSide) {
                entity.hurt(HbmDamageSources.source(level, HbmDamageSources.acid), .5F);
                if (!entity.isAlive() && item.getItem().is(Items.SLIME_BALL))
                    for (Player player : level.getEntitiesOfClass(Player.class, entity.getBoundingBox().inflate(10, 10, 10))) WorldFluidHooks.sulfuricAchievement.accept(player);
            }
            if (entity.tickCount % 5 == 0) level.addParticle(ParticleTypes.CLOUD, entity.getX(), entity.getY(), entity.getZ(), 0, 0, 0);
        } else {
            Vec3 velocity = entity.getDeltaMovement();
            if (velocity.y < -.2) entity.setDeltaMovement(velocity.x, velocity.y * .5, velocity.z);
            if (!level.isClientSide) entity.hurt(HbmDamageSources.source(level, HbmDamageSources.acid), 5F);
        }
        if (entity.tickCount % 5 == 0) level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, .2F, 1F);
    }
    @Override public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (spec.originalClass().equals("SchrabidicBlock")) level.addParticle(LegacyParticles.SCHRAB_AURA.get(),
                pos.getX() + .5F + random.nextDouble() * 2 - 1D, pos.getY() + .5F + random.nextDouble() * 2 - 1D, pos.getZ() + .5F + random.nextDouble() * 2 - 1D, 0, 0, 0);
    }
}
