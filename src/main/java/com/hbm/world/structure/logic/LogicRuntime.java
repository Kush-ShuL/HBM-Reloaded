package com.hbm.world.structure.logic;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/** Original runtime tile lifecycle shared by the genuine visible and invisible logic hosts. */
public final class LogicRuntime extends BlockEntity {
    public int phase, timer, direction = 6;
    public String actionID = "FODDER_WAVE", conditionID = "PLAYER_CUBE_5", interactionID;
    public String disguiseId;
    public int disguiseMeta;
    public Player player;
    private LogicProgram.Step action;
    private LogicProgram.Condition condition;
    private LogicProgram.Interaction interaction;
    private boolean disguised;
    private List<String> pendingDependencies = List.of();

    public LogicRuntime(BlockPos pos, BlockState state) { this(WandLogicContent.RUNTIME_ENTITY.get(), pos, state); }
    public LogicRuntime(BlockEntityType<?> type, BlockPos pos, BlockState state) { super(type, pos, state); }
    public ServerLevel server() { return (ServerLevel) level; }
    public List<String> pendingDependencies() { return pendingDependencies; }
    public static void tick(Level level, BlockPos pos, BlockState state, LogicRuntime tile) {
        if (!level.isClientSide) {
            if (tile.action == null) tile.action = LogicProgram.actions.get(tile.actionID);
            if (tile.condition == null) tile.condition = LogicProgram.conditions.get(tile.conditionID);
            if (tile.interaction == null && tile.interactionID != null) tile.interaction = LogicProgram.interactions.get(tile.interactionID);
            if (tile.action == null || tile.condition == null) { level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3); return; }
            var missing = new ArrayList<String>(tile.action.pending()); missing.addAll(tile.condition.pending());
            tile.pendingDependencies = List.copyOf(new java.util.TreeSet<>(missing));
            if (missing.isEmpty()) {
                tile.action.run().accept(tile);
                if (tile.condition.run().test(tile)) { tile.phase++; tile.timer = 0; } else tile.timer++;
            }
        }
        if (!tile.disguised) { tile.setChanged(); level.sendBlockUpdated(pos, state, state, 3); tile.disguised = true; }
    }
    public boolean interact(LogicProgram.Use use) {
        // Source interaction is cached by the server update, not eagerly by a right click before first tick.
        if (interaction == null) return false;
        pendingDependencies = interaction.pending();
        if (!pendingDependencies.isEmpty()) return false;
        interaction.run().accept(this, use); return true;
    }
    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.saveAdditional(tag, lookup); tag.putInt("phase", phase); tag.putString("actionID", actionID); tag.putString("conditionID", conditionID);
        if (interactionID != null) tag.putString("interactionID", interactionID);
        tag.putInt("direction", direction);
        if (disguiseId != null) { tag.putString("disguise", disguiseId); tag.putInt("disguiseMeta", disguiseMeta); }
        // Source timer, cached handlers, player reference and one-time disguise flag are intentionally not persisted.
    }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.loadAdditional(tag, lookup); phase = tag.getInt("phase"); actionID = tag.getString("actionID"); conditionID = tag.getString("conditionID");
        if (tag.contains("interactionID")) interactionID = tag.getString("interactionID");
        direction = LogicWorld.orientation(tag.getInt("direction"));
        if (tag.contains("disguise")) { disguiseId = tag.getString("disguise"); disguiseMeta = tag.getInt("disguiseMeta"); }
    }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup) { return saveWithoutMetadata(lookup); }
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookup) { loadAdditional(tag, lookup); }
    @Override public void onDataPacket(net.minecraft.network.Connection connection, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider lookup) { handleUpdateTag(packet.getTag(), lookup); requestModelDataUpdate(); if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3); }
}
