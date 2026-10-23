package com.hbm.machine.reactor.rbmk.residue;

import com.hbm.block.gas.GasContent;
import com.hbm.block.gas.GasKind;
import com.hbm.exposure.HbmExposure;
import com.hbm.hazard.HbmHazards;
import com.hbm.machine.arcfurnace.ArcSandBlock;
import com.hbm.machine.reactor.rbmk.failure.RbmkFailureEffects;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.*;

/** Source RBMKDebris/Burning/Radiating/Digamma: no BE and a full collision box. */
public final class RbmkResidueBlock extends Block {
    public enum Kind {
        NORMAL("pribris"), BURNING("pribris_burning"), RADIATING("pribris_radiating"), DIGAMMA("pribris_digamma");
        public final String id;
        Kind(String id) { this.id = id; }
    }
    public static final IntegerProperty META = IntegerProperty.create("legacy_meta", 0, 15);
    public final Kind kind;
    public RbmkResidueBlock(Kind kind) {
        super(Properties.of().mapColor(MapColor.METAL).strength(50, 360).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());
        this.kind = kind;
        registerDefaultState(stateDefinition.any().setValue(META, 0));
    }
    @Override protected MapCodec<? extends Block> codec() { return MapCodec.unit(this); }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(META); }
    @Override protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return 0; }
    @Override protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) { return true; }
    public BlockState fromLegacy(int meta) { return defaultBlockState().setValue(META, meta & 15); }
    private int tickRate(Level level) { return (kind == Kind.RADIATING ? 20 : 100) + level.random.nextInt(20); }
    @Override protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState old, boolean moving) {
        super.onPlace(state, level, pos, old, moving);
        if (old.is(this) || kind == Kind.NORMAL) return;
        if (kind == Kind.DIGAMMA) { level.scheduleTick(pos, this, 2); return; }
        if (!level.isClientSide && level.random.nextInt(3) == 0) flame(level, pos, level.random, false);
        level.scheduleTick(pos, this, tickRate(level));
    }
    @Override protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (kind == Kind.NORMAL) return;
        if (kind == Kind.DIGAMMA) {
            for (var dir : Direction.values()) {
                var neighbor = pos.relative(dir);
                var block = level.getBlockState(neighbor).getBlock();
                if ((block instanceof RbmkResidueBlock && block != this) || id(block, "corium_block") || id(block, "block_corium"))
                    level.setBlock(neighbor, defaultBlockState(), UPDATE_ALL);
            }
            return; // Source only schedules newly converted blocks, never its own recurring aura.
        }
        if (kind == Kind.RADIATING) radiate(level, pos);
        if (random.nextInt(5) == 0) {
            flame(level, pos, random, kind == Kind.RADIATING);
            level.playSound(null, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS,
                    1F + random.nextFloat(), random.nextFloat() * .7F + .3F);
        }
        // Both legacy ForgeDirection and native Direction use DOWN,UP,NORTH,SOUTH,WEST,EAST.
        var neighbor = pos.relative(Direction.values()[random.nextInt(6)]);
        var beside = level.getBlockState(neighbor);
        if (random.nextInt(10) == 0 && beside.is(Blocks.AIR))
            level.setBlock(neighbor, GasContent.BLOCKS_BY_KIND.get(GasKind.MELTDOWN).get().defaultBlockState(), UPDATE_ALL);
        boolean boron = id(beside.getBlock(), "sand_boron_layer") || beside.getBlock() instanceof ArcSandBlock && beside.getValue(ArcSandBlock.META) == 0;
        boolean foam = id(beside.getBlock(), "foam_layer") || id(beside.getBlock(), "block_foam");
        int chance = kind == Kind.RADIATING ? (boron ? 25 : 1000) : (boron || foam ? 10 : 100);
        if (random.nextInt(chance) == 0) {
            if (kind == Kind.BURNING) level.setBlock(pos, RbmkResidueContent.RESIDUES.get(Kind.NORMAL).get().defaultBlockState(), UPDATE_ALL);
            else if (state.getValue(META) == 15) level.setBlock(pos, RbmkResidueContent.RESIDUES.get(Kind.BURNING).get().defaultBlockState(), UPDATE_ALL);
            else { level.setBlock(pos, state.setValue(META, state.getValue(META) + 1), UPDATE_CLIENTS); level.scheduleTick(pos, this, tickRate(level)); }
        } else level.scheduleTick(pos, this, tickRate(level));
    }
    private static boolean id(Block block, String id) { var key = BuiltInRegistries.BLOCK.getKey(block); return key.getNamespace().equals("hbm") && key.getPath().equals(id); }
    private static void flame(Level level, BlockPos pos, RandomSource random, boolean spread) {
        double x = spread ? random.nextDouble() : .25 + random.nextDouble() * .5;
        double z = spread ? random.nextDouble() : .25 + random.nextDouble() * .5;
        RbmkFailureEffects.flame(level, pos.getX() + x, pos.getY() + 1.75, pos.getZ() + z, 300);
    }
    private static void radiate(ServerLevel level, BlockPos pos) {
        var center = Vec3.atCenterOf(pos);
        for (var living : level.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(100))) {
            var delta = new Vec3(living.getX(), living.getEyeY(), living.getZ()).subtract(center);
            double length = delta.length(); var normal = delta.normalize(); float resistance = 0;
            for (int i = 1; i < length; i++) resistance += level.getBlockState(BlockPos.containing(center.add(normal.scale(i)))).getBlock().getExplosionResistance();
            if (resistance < 1) resistance = 1;
            float dose = 1_000_000F; dose /= resistance; dose /= (float)(length * length);
            HbmExposure.contaminate(living, HbmExposure.Contamination.CREATIVE, dose);
            if (length < 5) living.hurt(level.damageSources().inFire(), 100);
            if (living instanceof Player player && length < 10) {
                var held = player.getMainHandItem();
                if (BuiltInRegistries.ITEM.getKey(held.getItem()).toString().equals("hbm:marshmallow")
                        && held.getOrDefault(HbmHazards.LEGACY_METADATA.get(), 0) != 1 && player.getRandom().nextInt(100) == 0)
                    held.set(HbmHazards.LEGACY_METADATA.get(), 1);
            }
        }
    }
    @Override protected List<ItemStack> getDrops(BlockState state, LootParams.Builder context) {
        var radius = context.getOptionalParameter(LootContextParams.EXPLOSION_RADIUS);
        return radius != null && context.getLevel().random.nextFloat() > 1F / radius ? List.of() : List.of(new ItemStack(asItem()));
    }
}
