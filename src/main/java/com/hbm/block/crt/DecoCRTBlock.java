package com.hbm.block.crt;

import com.hbm.hazard.HbmHazards;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

/** Real BlockDecoCRT/BlockMulti: full collision cube, four screen variants and four source yaw states. */
public final class DecoCRTBlock extends Block {
    public static final IntegerProperty META = IntegerProperty.create("legacy_meta", 0, 15);
    private static final MapCodec<DecoCRTBlock> CODEC = simpleCodec(DecoCRTBlock::new);
    public DecoCRTBlock() {
        this(Properties.of().mapColor(MapColor.METAL).strength(5, 6).sound(SoundType.STONE)
                .requiresCorrectToolForDrops().noOcclusion());
    }
    public DecoCRTBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(META, 0));
    }
    @Override protected MapCodec<? extends Block> codec() { return CODEC; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(META); }
    @Override protected int getLightBlock(BlockState state, BlockGetter level, BlockPos position) { return 0; }
    @Override protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos position) { return true; }
    public BlockState fromLegacy(int metadata) { return defaultBlockState().setValue(META, metadata & 15); }
    /** Source damageDropped: orientation is removed while the actual screen variant is retained. */
    public static int damageDropped(int metadata) { return (Math.abs(metadata) % 16) / 4; }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        int yaw = Mth.floor(context.getRotation() * 4F / 360F + .5D) & 3;
        int variant = DecoCRTItem.variant(context.getItemInHand());
        return fromLegacy(variant * 4 + yaw);
    }
    public static int transformMeta(int metadata, int clockwiseTurns) {
        return ((metadata + clockwiseTurns) & 3) | (metadata / 4 * 4);
    }
    @Override protected BlockState rotate(BlockState state, Rotation rotation) {
        int turns = switch (rotation) { case NONE -> 0; case CLOCKWISE_90 -> 1; case CLOCKWISE_180 -> 2; case COUNTERCLOCKWISE_90 -> 3; };
        return fromLegacy(transformMeta(state.getValue(META), turns));
    }
    @Override protected BlockState mirror(BlockState state, Mirror mirror) {
        int meta = state.getValue(META), yaw = meta & 3;
        if (mirror == Mirror.LEFT_RIGHT) yaw = (2 - yaw) & 3;
        if (mirror == Mirror.FRONT_BACK) yaw = -yaw & 3;
        return fromLegacy((meta & 12) | yaw);
    }
    /** Only the source explicit Shredder callback calls this: every state becomes broken, preserving yaw. */
    public void breakScreen(Level level, BlockPos position) {
        BlockState state = level.getBlockState(position);
        if (state.getBlock() == this) level.setBlock(position, fromLegacy(state.getValue(META) % 4 + 4), UPDATE_ALL);
    }
    @Override protected List<ItemStack> getDrops(BlockState state, LootParams.Builder context) {
        Float radius = context.getOptionalParameter(LootContextParams.EXPLOSION_RADIUS);
        if (radius != null && context.getLevel().random.nextFloat() > 1F / radius) return List.of();
        return List.of(DecoCRTItem.stack(damageDropped(state.getValue(META))));
    }
    @Override public ItemStack getCloneItemStack(LevelReader level, BlockPos position, BlockState state) {
        return DecoCRTItem.stack(damageDropped(state.getValue(META)));
    }
}
