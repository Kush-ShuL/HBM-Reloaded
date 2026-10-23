package com.hbm.block.sourceemitter;

import com.hbm.api.tool.ToolableBlock;
import com.hbm.item.dye.ChemicalDyeType;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

/** Real ordinary cube plus RenderEmitter beam, not an HE machine or a redstone switch. */
public final class EmitterBlock extends BaseEntityBlock implements ToolableBlock {
    public static final IntegerProperty META = IntegerProperty.create("legacy_meta", 0, 15);
    public EmitterBlock() {
        super(Properties.of().mapColor(MapColor.METAL).strength(5F, 12F).sound(SoundType.STONE)
                .requiresCorrectToolForDrops().noOcclusion());
        registerDefaultState(stateDefinition.any().setValue(META, 0));
    }
    @Override protected MapCodec<? extends BaseEntityBlock> codec() { return MapCodec.unit(this); }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(META); }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new EmitterEntity(pos, state); }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, SourceEmittersContent.LIGHT_ENTITY.get(), EmitterEntity::tick);
    }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        var player = context.getPlayer(); var pos = context.getClickedPos(); int meta = 0;
        if (player != null) {
            // Actual1.7 BlockPistonBase.determineOrientation: position/eye thresholds, not modern look-pitch.
            boolean close = Math.abs((float) player.getX() - pos.getX()) < 2 && Math.abs((float) player.getZ() - pos.getZ()) < 2;
            double eye = player.getY() + 1.82;
            if (close && eye - pos.getY() > 2) meta = 1;
            else if (close && pos.getY() - eye > 0) meta = 0;
            else meta = switch (Mth.floor(player.getYRot() * 4F / 360F + .5) & 3) { case 0 -> 2; case 1 -> 5; case 2 -> 3; default -> 4; };
        }
        return defaultBlockState().setValue(META, meta);
    }
    @Override protected BlockState rotate(BlockState state, Rotation rotation) {
        int meta = state.getValue(META);
        return meta < 6 ? state.setValue(META, rotation.rotate(Direction.from3DDataValue(meta)).get3DDataValue()) : state;
    }
    @Override protected BlockState mirror(BlockState state, Mirror mirror) {
        int meta = state.getValue(META);
        return meta < 6 ? state.setValue(META, mirror.mirror(Direction.from3DDataValue(meta)).get3DDataValue()) : state;
    }
    /** Old ItemDye included ink/cocoa/lapis/bonemeal, now split into separate real native items. */
    private static int sourceDye(ItemStack stack) {
        int metadata;
        if (stack.is(Items.INK_SAC)) metadata = 0;
        else if (stack.is(Items.COCOA_BEANS)) metadata = 3;
        else if (stack.is(Items.LAPIS_LAZULI)) metadata = 4;
        else if (stack.is(Items.BONE_MEAL)) metadata = 15;
        else if (stack.getItem() instanceof DyeItem dye) metadata = 15 - dye.getDyeColor().getId();
        else return -1;
        // Same canonical old RGB palette; no chemical-dye/OreDictionary acceptance is added.
        return ChemicalDyeType.values()[metadata].color;
    }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        int color = sourceDye(player.getMainHandItem());
        if (color < 0 || !(level.getBlockEntity(pos) instanceof EmitterEntity emitter)) return InteractionResult.PASS;
        emitter.color = color; emitter.setChanged();
        level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
        player.getMainHandItem().shrink(1); // Source also consumes dye in creative.
        return InteractionResult.CONSUME;
    }
    @Override public boolean onScrew(Level level, Player player, BlockPos pos, Direction side, float x, float y, float z, ToolType tool) {
        if (!(level.getBlockEntity(pos) instanceof EmitterEntity emitter)) return false;
        if (tool == ToolType.SCREWDRIVER) emitter.girth += .125F;
        else if (tool == ToolType.DEFUSER) { emitter.girth -= .125F; if (emitter.girth < .125F) emitter.girth = .125F; }
        else if (tool == ToolType.HAND_DRILL) emitter.effect = (emitter.effect + 1) % 5;
        else return false;
        emitter.setChanged(); return true;
    }
}
