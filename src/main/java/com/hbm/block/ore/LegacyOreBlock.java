package com.hbm.block.ore;

import java.util.ArrayList;
import java.util.List;
import com.hbm.hazard.HbmHazards;
import com.hbm.radiation.HbmRadiation;
import com.hbm.registry.HbmContent;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

/** Implements the admitted X5846 BlockOre/Cluster/Depth/Meteor/Generic behaviors. */
public class LegacyOreBlock extends Block {
    public static final IntegerProperty LEGACY_META = IntegerProperty.create("legacy_meta", 0, 15);
    protected final OreSpec spec;
    private final MapCodec<LegacyOreBlock> codec;
    private final java.util.Random experienceRandom = new java.util.Random();

    public LegacyOreBlock(OreSpec spec) {
        this(spec, spec.properties());
    }
    public LegacyOreBlock(OreSpec spec, Properties properties) {
        super(properties);
        this.spec = spec;
        codec = simpleCodec(ignored -> new LegacyOreBlock(spec));
        registerDefaultState(stateDefinition.any().setValue(LEGACY_META, 0));
    }

    public OreSpec spec() { return spec; }
    @Override protected MapCodec<? extends LegacyOreBlock> codec() { return codec; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(LEGACY_META); }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder context) {
        ItemStack tool = context.getOptionalParameter(LootContextParams.TOOL);
        if (tool == null) tool = ItemStack.EMPTY;
        var enchantments = context.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        int silk = EnchantmentHelper.getItemEnchantmentLevel(enchantments.getOrThrow(Enchantments.SILK_TOUCH), tool);
        if (silk > 0 && !spec.id().equals("ore_oil")) {
            ItemStack stack = new ItemStack(this);
            // createStackedBlock retains raw world metadata when ItemBlock has subtypes.
            if (spec.multi()) stack.set(HbmHazards.LEGACY_METADATA.get(), state.getValue(LEGACY_META));
            return List.of(stack);
        }
        List<ItemStack> raw = com.hbm.ore.RawOreContent.minedDrops(state, context, tool);
        if (raw != null) return raw;
        int fortune = EnchantmentHelper.getItemEnchantmentLevel(enchantments.getOrThrow(Enchantments.FORTUNE), tool);
        RandomSource random = context.getLevel().random;
        int count;
        if (spec.fortune().equals("DEPTH")) {
            // Original depthrock evaluates base quantity first, even at fortune zero.
            count = quantity(random) * (Math.max(random.nextInt(fortune + 2) - 1, 0) + 1);
            if (spec.id().equals("ore_alexandrite") && count > 2) count = 2;
        } else if (spec.fortune().equals("ORE") && fortune > 0 && drop(random) != asItem()) {
            // The getItemDropped preflight consumes RNG before quantity, including fire ore.
            int multiplier = random.nextInt(fortune + 2) - 1;
            count = quantity(random) * (Math.max(multiplier, 0) + 1);
        } else count = quantity(random);
        List<ItemStack> result = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            Item item = drop(random);
            ItemStack stack = new ItemStack(item);
            if (item == asItem() && spec.multi()) stack.set(HbmHazards.LEGACY_METADATA.get(), spec.ordinal(state.getValue(LEGACY_META)));
            else if (spec.dropMetadata() != 0) stack.set(HbmHazards.LEGACY_METADATA.get(), spec.dropMetadata());
            result.add(stack);
        }
        Float explosionRadius = context.getOptionalParameter(LootContextParams.EXPLOSION_RADIUS);
        if (explosionRadius != null) {
            float chance = 1F / explosionRadius;
            result.removeIf(stack -> random.nextFloat() > chance);
        }
        // Original ordinary/cluster/depth/meteor ores do not award XP.
        return result;
    }

    private int quantity(RandomSource random) { return spec.quantityMin() + (spec.quantityRandom() > 0 ? random.nextInt(spec.quantityRandom()) : 0); }
    private Item drop(RandomSource random) {
        String symbol = spec.alternateDrop() != null && random.nextInt(spec.alternateChance()) == 0 ? spec.alternateDrop() : spec.drop();
        return symbol == null ? asItem() : HbmContent.item(symbol);
    }

    protected ItemStack blockStack(BlockState state) {
        ItemStack stack = new ItemStack(this);
        if (spec.multi()) stack.set(HbmHazards.LEGACY_METADATA.get(), spec.ordinal(state.getValue(LEGACY_META)));
        return stack;
    }

    @Override public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) { return blockStack(state); }

    @Override
    public int getExpDrop(BlockState state, LevelAccessor level, BlockPos pos, BlockEntity blockEntity, Entity breaker, ItemStack tool) {
        if (!spec.originalClass().equals("BlockSellafieldOre") || spec.drop() == null) return 0;
        var enchantments = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        if (EnchantmentHelper.getItemEnchantmentLevel(enchantments.getOrThrow(Enchantments.SILK_TOUCH), tool) > 0) return 0;
        return experienceRandom.nextInt(5) + 3;
    }

    @Override
    protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        if (spec.depth() && player.getMainHandItem().getItem() instanceof DepthRockTool tool
                && tool.canBreakRock(level, player, player.getMainHandItem(), state, pos)) return (float) (1D / 50D);
        return super.getDestroyProgress(state, player, level, pos);
    }

    @Override
    public boolean canEntityDestroy(BlockState state, BlockGetter level, BlockPos pos, Entity entity) {
        return spec.dragonProof() ? !(entity instanceof EnderDragon) : super.canEntityDestroy(state, level, pos, entity);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbor, BlockPos neighborPos, boolean moving) {
        // This condition is shared by ALL original BlockOre instances, not just oil ore.
        BlockState below = level.getBlockState(pos.below());
        if (spec.oilNeighbor() && !level.isClientSide && com.hbm.registry.HbmDeepslateOres.equivalent(below, "ore_oil_empty")) {
            level.setBlock(pos, com.hbm.registry.HbmDeepslateOres.forHost(state, "ore_oil_empty").defaultBlockState(), 3);
            level.setBlock(pos.below(), com.hbm.registry.HbmDeepslateOres.forHost(below, "ore_oil").defaultBlockState(), 3);
        }
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState previous, boolean moving) {
        super.onPlace(state, level, pos, previous, moving);
        if (spec.radiation() > 0 && !level.isClientSide) level.scheduleTick(pos, this, 20);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (spec.radiation() > 0) {
            HbmRadiation.incrementRad(level, pos.getX(), pos.getY(), pos.getZ(), spec.radiation());
            level.scheduleTick(pos, this, 20);
        }
    }

    @Override protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) { tick(state, level, pos, random); }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (spec.originalClass().equals("BlockSmolder")) entity.igniteForSeconds(3);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (spec.originalClass().equals("BlockSmolder") && level.getBlockState(pos.above()).isAir()) {
            level.addParticle(ParticleTypes.LAVA, pos.getX() + 0.25 + random.nextDouble() * 0.5, pos.getY() + 1.1, pos.getZ() + 0.25 + random.nextDouble() * 0.5, 0, 0, 0);
            level.addParticle(ParticleTypes.FLAME, pos.getX() + 0.25 + random.nextDouble() * 0.5, pos.getY() + 1.1, pos.getZ() + 0.25 + random.nextDouble() * 0.5, 0, 0, 0);
        }
    }
}
