package com.hbm.block.fluid;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * 1.7 Forge BlockFluidBase semantics adapted to native 1.21 FluidState.
 * Forge algorithm attribution: King Lemming and OvermindDL1; source provenance is
 * recorded by tools/port_world_fluids.py. No vanilla modern flow algorithm is used.
 */
public abstract class LegacyFluidBlock extends Block implements BucketPickup {
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 15);
    protected final Map<Block, Boolean> displacements = new HashMap<>();
    protected final WorldFluidSpec spec;
    protected final int quantaPerBlock, density, densityDir, tickRate, maxScaledLight;
    protected final float quantaPerBlockFloat;
    private final Supplier<LegacyWorldFluid> fluid;
    private final LegacyMaterial material;
    protected LegacyFluidBlock(WorldFluidSpec spec, Supplier<LegacyWorldFluid> fluid) {
        super(properties(spec));
        this.spec = spec; this.fluid = fluid;
        quantaPerBlock = spec.quanta(); quantaPerBlockFloat = quantaPerBlock;
        density = spec.density(); densityDir = density > 0 ? -1 : 1; tickRate = spec.tickRate();
        maxScaledLight = spec.luminosity();
        material = switch (spec.material()) {
            case "water" -> LegacyMaterial.water;
            case "lava" -> LegacyMaterial.lava;
            case "rock" -> new LegacyMaterial("rock", true, true, false, false, false);
            default -> new LegacyMaterial(spec.material(), false, false, true, !spec.material().equals("fluidcorium"), false);
        };
        // A setBlock without explicit metadata always used raw zero, including finite fluids.
        registerDefaultState(stateDefinition.any().setValue(LEVEL, 0));
    }
    private static Properties properties(WorldFluidSpec spec) {
        var properties = Properties.of().mapColor(spec.material().equals("lava") ? MapColor.FIRE : spec.material().equals("rock") ? MapColor.STONE : MapColor.WATER)
                .strength(0, 300).noCollission().noOcclusion().noLootTable().randomTicks()
                .lightLevel(state -> spec.light(state.getValue(LEVEL)));
        if (!spec.material().equals("rock")) properties.liquid();
        if (!spec.material().equals("rock") && !spec.material().equals("fluidcorium")) properties.replaceable();
        return properties;
    }
    public WorldFluidSpec spec() { return spec; }
    public LegacyMaterial material() { return material; }
    @Override protected MapCodec<? extends LegacyFluidBlock> codec() { return simpleCodec(ignored -> { throw new UnsupportedOperationException("Fluid blocks are registry-defined"); }); }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(LEVEL); }
    @Override protected FluidState getFluidState(BlockState state) { return fluid.get().defaultFluidState().setValue(LEVEL, state.getValue(LEVEL)); }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.INVISIBLE; }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return Shapes.empty(); }
    @Override protected boolean canBeReplaced(BlockState state, net.minecraft.world.item.context.BlockPlaceContext context) { return material.replaceable(); }
    @Override protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState before, boolean moving) {
        if (before.getBlock() != this) level.scheduleTick(pos, this, tickRate);
    }
    @Override protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbor, BlockPos neighborPos, boolean moving) {
        level.scheduleTick(pos, this, tickRate);
        neighborReaction(new LegacyFluidWorld(level), pos);
    }
    protected void neighborReaction(LegacyFluidWorld world, BlockPos pos) { }
    @Override protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) { updateTick(new LegacyFluidWorld(level), pos.getX(), pos.getY(), pos.getZ(), random); }
    @Override protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) { tick(state, level, pos, random); }
    public abstract void updateTick(LegacyFluidWorld world, int x, int y, int z, RandomSource random);
    public abstract int getQuantaValue(LegacyFluidWorld world, int x, int y, int z);
    public abstract int getMaxRenderHeightMeta();
    public int tickRate(LegacyFluidWorld world) { return spec.publicTickRate(); }
    public int densityDirection() { return densityDir; }
    public int getLightValue(LegacyFluidWorld world, int x, int y, int z) { return spec.light(world.getBlockMetadata(x, y, z)); }
    public boolean canDisplace(LegacyFluidWorld world, int x, int y, int z) {
        if (world.isAir(x, y, z)) return true;
        Block block = world.getBlock(x, y, z);
        if (block == this) return false;
        Boolean override = displacement(world.state(x, y, z));
        if (override != null) return override;
        LegacyMaterial material = world.material(x, y, z);
        if (material.blocksMovement() || material == LegacyMaterial.portal) return false;
        int otherDensity = getDensity(world, x, y, z);
        return otherDensity == Integer.MAX_VALUE || density > otherDensity;
    }
    public boolean displaceIfPossible(LegacyFluidWorld world, int x, int y, int z) {
        if (world.isAir(x, y, z)) return true;
        Block block = world.getBlock(x, y, z);
        if (block == this) return false;
        Boolean override = displacement(world.state(x, y, z));
        if (override != null) { if (override) world.drop(x, y, z); return override; }
        LegacyMaterial material = world.material(x, y, z);
        if (material.blocksMovement() || material == LegacyMaterial.portal) return false;
        int otherDensity = getDensity(world, x, y, z);
        if (otherDensity == Integer.MAX_VALUE) { world.drop(x, y, z); return true; }
        return density > otherDensity;
    }
    protected Boolean displacement(BlockState state) {
        Boolean explicit = displacements.get(state.getBlock());
        if (explicit != null) return explicit;
        String path = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath();
        if (state.is(BlockTags.DOORS) || state.is(BlockTags.SIGNS) || path.equals("sugar_cane")) return false;
        return null;
    }
    public static int getDensity(LegacyFluidWorld world, int x, int y, int z) { return world.getBlock(x, y, z) instanceof LegacyFluidBlock fluid ? fluid.density : Integer.MAX_VALUE; }
    public final int getQuantaValueBelow(LegacyFluidWorld world, int x, int y, int z, int maximum) { int value = getQuantaValue(world, x, y, z); return value >= maximum ? -1 : value; }
    public final float getQuantaPercentage(LegacyFluidWorld world, int x, int y, int z) { return getQuantaValue(world, x, y, z) / quantaPerBlockFloat; }
    public boolean shouldSideBeRendered(BlockGetter level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return !state.is(this) && !state.isSolidRender(level, pos);
    }
    public Vec3 getFlowVector(LegacyFluidWorld world, int x, int y, int z) {
        Vec3 vector = Vec3.ZERO;
        int decay = quantaPerBlock - getQuantaValue(world, x, y, z);
        int[][] offsets = {{-1, 0}, {0, -1}, {1, 0}, {0, 1}};
        for (int[] offset : offsets) {
            int x2 = x + offset[0], z2 = z + offset[1];
            int otherDecay = quantaPerBlock - getQuantaValue(world, x2, y, z2);
            if (otherDecay >= quantaPerBlock) {
                if (!world.material(x2, y, z2).blocksMovement()) {
                    otherDecay = quantaPerBlock - getQuantaValue(world, x2, y - 1, z2);
                    if (otherDecay >= 0) {
                        int power = otherDecay - (decay - quantaPerBlock);
                        vector = vector.add(offset[0] * power, 0, offset[1] * power);
                    }
                }
            } else if (otherDecay >= 0) {
                int power = otherDecay - decay;
                vector = vector.add(offset[0] * power, 0, offset[1] * power);
            }
        }
        if (world.getBlock(x, y + 1, z) == this) {
            boolean solid = false;
            // Original short-circuit order N,S,W,E followed by the same positions one block higher.
            for (int dy = 0; dy < 2 && !solid; dy++) for (Direction direction : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST}) {
                BlockPos pos = new BlockPos(x, y + dy, z).relative(direction);
                if (LegacyMaterials.of(world.access.getBlockState(pos)).isSolid()) { solid = true; break; }
            }
            if (solid) vector = vector.normalize().add(0, -6, 0);
        }
        return vector.normalize();
    }
    public double flowDirection(BlockGetter level, BlockPos pos) {
        if (!material.isLiquid()) return -1000;
        Vec3 flow = getFlowVector(new LegacyFluidWorld(level), pos.getX(), pos.getY(), pos.getZ());
        return flow.x == 0 && flow.z == 0 ? -1000 : Math.atan2(flow.z, flow.x) - Math.PI / 2;
    }
    /** Native bucket collection is restricted to the three actual BucketHandler mappings. */
    @Override public ItemStack pickupBlock(Player player, LevelAccessor level, BlockPos pos, BlockState state) {
        if (!com.hbm.fluid.FluidInteropConfig.enabled()) return ItemStack.EMPTY;
        return pickupNative(level, pos, state);
    }
    public ItemStack pickupNative(LevelAccessor level, BlockPos pos, BlockState state) {
        if (spec.bucket() == null || state.getValue(LEVEL) != 0) return ItemStack.EMPTY;
        ItemStack result = new ItemStack(fluid.get().nativeBucket());
        if (result.is(Items.AIR)) return ItemStack.EMPTY;
        level.setBlock(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);
        return result;
    }
    @Override public Optional<SoundEvent> getPickupSound() { return Optional.of(SoundEvents.BUCKET_FILL); }
    /** Legacy IFluidBlock drain result, including the original finite doDrain-after-removal zero. */
    public int drainAmount(Level level, BlockPos pos, boolean doDrain) {
        LegacyFluidWorld world = new LegacyFluidWorld(level);
        if (!spec.finite() && (world.getBlock(pos.getX(), pos.getY(), pos.getZ()) != this || world.getBlockMetadata(pos.getX(), pos.getY(), pos.getZ()) != 0)) return -1;
        if (doDrain) level.setBlock(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);
        return spec.finite() ? net.minecraft.util.Mth.floor(getQuantaPercentage(world, pos.getX(), pos.getY(), pos.getZ()) * 1000) : 1000;
    }
}
