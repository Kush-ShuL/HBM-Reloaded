package com.hbm.world.structure.logic;

import com.hbm.api.machine.ICopiable;
import com.hbm.api.tool.ToolableBlock.ToolType;
import com.hbm.multiblock.MuffledMachine;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/** Real source authoring tile: tool-authored settings, source packet audience and one-way runtime replacement. */
public final class LogicAuthoring extends BlockEntity implements ICopiable, MuffledMachine {
    public int placedRotation, disguiseMeta;
    public String disguiseId;
    public String actionID = "FODDER_WAVE", conditionID = "PLAYER_CUBE_5", interactionID;
    public boolean muffled, tilted;
    private boolean triggerReplace;
    private List<String> pendingDependencies = List.of();
    public LogicAuthoring(BlockPos pos, BlockState state) { this(WandLogicContent.WAND_ENTITY.get(), pos, state); }
    public LogicAuthoring(BlockEntityType<?> type, BlockPos pos, BlockState state) { super(type, pos, state); }
    public void trigger() { triggerReplace = true; }
    /** The source INBTTileEntityTransformable callback receives the one canonical source debug configuration. */
    public void transform(boolean structureDebug) { triggerReplace = !structureDebug; }
    public void transformTE(int coordBaseMode) { transform(com.hbm.config.SourceServerConfig.STRUCTURE_DEBUG.get()); }
    public List<String> pendingDependencies() { return pendingDependencies; }
    /** Called after the real host's ItemBlock/normal-render/non-sneak predicate, with no held-stack consumption. */
    public void applyDisguise(Block block, int sourceMetadata) { disguiseId = BuiltInRegistries.BLOCK.getKey(block).toString(); disguiseMeta = sourceMetadata & 15; }
    public boolean cycle(ToolType tool, boolean sneaking) {
        List<String> names; String old;
        switch (tool) {
            case SCREWDRIVER -> { names = LogicProgram.getActionNames(); old = actionID; }
            case DEFUSER -> { names = LogicProgram.getConditionNames(); old = conditionID; }
            case HAND_DRILL -> { names = LogicProgram.getInteractionNames(); old = interactionID; }
            default -> { return false; }
        }
        int index = Math.max(0, Math.min(names.size() - 1, names.indexOf(old) + (sneaking ? -1 : 1)));
        switch (tool) { case SCREWDRIVER -> actionID = names.get(index); case DEFUSER -> conditionID = names.get(index); case HAND_DRILL -> interactionID = names.get(index); default -> {} }
        return true;
    }
    public static void tick(Level level, BlockPos pos, BlockState state, LogicAuthoring tile) {
        if (!(level instanceof ServerLevel server)) return;
        if (tile.triggerReplace) tile.replace();
        else {
            var packet = tile.getUpdatePacket();
            // Source networkPackNT(15) addresses the actual integer tile coordinates.
            for (var player : server.players()) if (player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) <= 15D * 15D) player.connection.send(packet);
        }
    }
    private void replace() {
        var world = (ServerLevel) level;
        String target = disguiseId == null ? "logic_block_invis" : "logic_block";
        var missing = new java.util.ArrayList<String>();
        for (String symbol : List.of("wand_logic", target)) if (!LogicWorld.blockReady(symbol)) missing.add("block " + symbol + " -> " + LogicWorld.blockId(symbol));
        pendingDependencies = List.copyOf(missing); if (!missing.isEmpty()) return;
        if (!getBlockState().is(LogicWorld.block("wand_logic", 0).getBlock())) {
            com.mojang.logging.LogUtils.getLogger().warn("Block at {} is not the actual source wand logic, cancelling replacement", worldPosition); return;
        }
        world.setBlock(worldPosition, LogicWorld.block(target, 0), 3);
        if (!(world.getBlockEntity(worldPosition) instanceof LogicRuntime runtime))
            throw new IllegalStateException("Actual logic host did not create its real LogicRuntime at " + worldPosition);
        runtime.actionID = actionID; runtime.conditionID = conditionID; runtime.interactionID = interactionID;
        runtime.direction = LogicWorld.orientation(placedRotation); runtime.disguiseId = disguiseId; runtime.disguiseMeta = disguiseMeta;
    }
    @Override public CompoundTag getSettings(Level world, BlockPos position) { var tag = new CompoundTag(); writeSettings(tag); return tag; }
    private void writeSettings(CompoundTag tag) {
        tag.putString("actionID", actionID); tag.putString("conditionID", conditionID);
        if (interactionID != null) tag.putString("interactionID", interactionID);
        tag.putInt("rotation", placedRotation);
        if (disguiseId != null) { tag.putString("disguise", disguiseId); tag.putInt("disguiseMeta", disguiseMeta); }
    }
    private void readSettings(CompoundTag tag) {
        actionID = tag.getString("actionID"); conditionID = tag.getString("conditionID");
        if (tag.contains("interactionID")) interactionID = tag.getString("interactionID");
        placedRotation = tag.getInt("rotation");
        if (tag.contains("disguise")) { disguiseId = tag.getString("disguise"); disguiseMeta = tag.getInt("disguiseMeta"); }
    }
    @Override public void pasteSettings(CompoundTag tag, int index, Level world, Player player, BlockPos position) {
        actionID = tag.getString("actionID"); conditionID = tag.getString("conditionID"); interactionID = tag.getString("interactionID");
        placedRotation = tag.getInt("disguiseMeta"); // Authored source copy/paste bug, not rotation.
        if (tag.contains("disguise")) { disguiseId = tag.getString("disguise"); disguiseMeta = tag.getInt("disguiseMeta"); }
    }
    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookup) { super.saveAdditional(tag, lookup); writeSettings(tag); tag.putBoolean("muffled", muffled); tag.putBoolean("tilted", tilted); if (triggerReplace) tag.putBoolean("hbm_wand_transform", true); }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider lookup) { super.loadAdditional(tag, lookup); readSettings(tag); muffled = tag.getBoolean("muffled"); tilted = tag.getBoolean("tilted"); if (tag.getBoolean("hbm_wand_transform")) transformTE(0); }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup) { return saveWithoutMetadata(lookup); }
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookup) { loadAdditional(tag, lookup); }
    @Override public void onDataPacket(net.minecraft.network.Connection connection, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider lookup) { handleUpdateTag(packet.getTag(), lookup); requestModelDataUpdate(); if (level != null) level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),3); }
    @Override public boolean isMuffled() { return muffled; }
    @Override public void setMuffled(boolean value) { muffled = value; }
}
