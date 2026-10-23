package com.hbm.block.falling;

import com.hbm.entity.LegacyFallingBlockEntity;
import com.hbm.hazard.HazardType;
import com.hbm.hazard.HbmHazards;
import com.hbm.radiation.HbmRadiation;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/** 1.7 BlockFalling and its X5846 HazardFalling subclass, without modern falling dust or scheduling. */
public class LegacyFallingBlock extends Block implements Fallable {
    public static final IntegerProperty LEGACY_META = IntegerProperty.create("legacy_meta", 0, 15);
    /** Original BlockFalling.fallInstantly global switch, for world-generation callers. */
    public static boolean fallInstantly;
    private final FallingSpec spec;
    private final MapCodec<LegacyFallingBlock> codec;
    // The original hazard subclass stores its dose on the shared singleton block instance.
    private float radiation;
    public LegacyFallingBlock(FallingSpec spec) {
        super(spec.properties()); this.spec = spec; codec = simpleCodec(ignored -> new LegacyFallingBlock(spec));
        registerDefaultState(stateDefinition.any().setValue(LEGACY_META, 0));
    }
    public FallingSpec spec() { return spec; }
    @Override protected MapCodec<? extends LegacyFallingBlock> codec() { return codec; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(LEGACY_META); }
    public int tickDelay() { return radiation > 0 ? 20 : 2; }
    @Override protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState previous, boolean moving) {
        if (previous.is(this)) return;
        // Source onBlockAdded calls its parent using the old shared dose before assigning a new one.
        level.scheduleTick(pos, this, tickDelay());
        if (spec.hazardous()) {
            radiation = (float) HbmHazards.registry().evaluate(new ItemStack(this)).firstLevel(HazardType.RADIATION)
                    .orElseThrow(() -> new IllegalStateException("Unresolved falling block radiation: " + spec.id())) * .1F;
            if (radiation > 0) level.scheduleTick(pos, this, tickDelay());
        }
    }
    @Override protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbor, BlockPos from, boolean moving) { level.scheduleTick(pos, this, tickDelay()); }
    @Override protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (radiation > 0) {
            HbmRadiation.incrementRad(level, pos.getX(), pos.getY(), pos.getZ(), radiation);
            level.scheduleTick(pos, this, tickDelay());
        }
        fall(level, pos, state);
    }
    private void fall(ServerLevel level, BlockPos pos, BlockState state) {
        if (!LegacyFallingRules.isFree(level.getBlockState(pos.below())) || pos.getY() < level.getMinBuildHeight()) return;
        if (!fallInstantly && level.hasChunksAt(pos.offset(-32, -32, -32), pos.offset(32, 32, 32))) {
            LegacyFallingBlockEntity falling = new LegacyFallingBlockEntity(level, pos, state);
            falling(falling);
            // Deliberately leave the source block until the entity's first moved tick validates it.
            level.addFreshEntity(falling);
        } else {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            BlockPos target = pos;
            while (LegacyFallingRules.isFree(level.getBlockState(target.below())) && target.getY() > level.getMinBuildHeight()) target = target.below();
            // Original instant fall discards metadata and disappears at the old bottom boundary.
            if (target.getY() > level.getMinBuildHeight()) level.setBlock(target, defaultBlockState(), 3);
        }
    }
    protected void falling(LegacyFallingBlockEntity entity) { }
    public void legacyLanded(Level level, BlockPos pos, int metadata) { }
    @Override public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        ItemStack stack = new ItemStack(this);
        if (state.getValue(LEGACY_META) != 0) stack.set(HbmHazards.LEGACY_METADATA.get(), state.getValue(LEGACY_META));
        return stack;
    }
}
