package com.hbm.block.cable;

import com.hbm.energy.EnergyConductor;
import com.hbm.energy.HbmEnergy;
import com.hbm.energy.PowerNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

public class NativeCableBlockEntity extends BlockEntity implements EnergyConductor {
    protected PowerNode node;
    private boolean chunkUnloaded;
    private BlockState paint;
    public NativeCableBlockEntity(BlockPos pos, BlockState state) { this(CableContent.ENTITY.get(), pos, state); }
    protected NativeCableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) { super(type, pos, state); }
    @Override public PowerNode energyNode() { return node; }
    @Override public boolean isLoaded() { return level != null && !isRemoved() && !chunkUnloaded; }
    @Override public boolean canConnect(Direction side) { return side != null; }
    public boolean shouldConduct() { return !getBlockState().hasProperty(SwitchedCableBlock.ENABLED) || getBlockState().getValue(SwitchedCableBlock.ENABLED); }

    public static void tick(Level level, BlockPos pos, BlockState state, NativeCableBlockEntity cable) {
        cable.updateConductingState();
        if (level.getGameTime() % 20 == 0) cable.refreshConnections();
    }
    public void updateConductingState() {
        if (!(level instanceof ServerLevel server) || isRemoved() || chunkUnloaded) return;
        if (!shouldConduct()) { disconnect(); return; }
        if (node == null || node.isExpired()) {
            var space = HbmEnergy.nodespace(server);
            node = space.getNode(worldPosition);
            if (node == null || node.isExpired()) {
                node = new PowerNode(worldPosition);
                space.createNode(node);
            }
        }
    }
    public void disconnect() {
        if (node != null && !node.isExpired() && level instanceof ServerLevel server) HbmEnergy.nodespace(server).destroyNode(node);
        node = null;
    }
    public void refreshConnections() {
        if (level == null || level.isClientSide || isRemoved() || chunkUnloaded) return;
        BlockState state = getBlockState();
        if (state.getBlock() instanceof NativeCableBlock block) {
            BlockState refreshed = block.refreshConnections(state, level, worldPosition);
            if (refreshed != state) level.setBlock(worldPosition, refreshed, Block.UPDATE_ALL);
        }
    }
    @Override public void onLoad() { super.onLoad(); chunkUnloaded = false; updateConductingState(); refreshConnections(); }
    @Override public void onChunkUnloaded() { chunkUnloaded = true; super.onChunkUnloaded(); }
    @Override public void setRemoved() { if (!chunkUnloaded) disconnect(); super.setRemoved(); }

    public BlockState paint() { return paint; }
    public void setPaint(BlockState paint) {
        this.paint = paint;
        setChanged(); requestModelDataUpdate();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }
    public CompoundTag getSettings() {
        var tag = new CompoundTag();
        if (paint != null) tag.put("paintblock", NbtUtils.writeBlockState(paint));
        return tag;
    }
    public void pasteSettings(CompoundTag tag) {
        if (tag.contains("paintblock", CompoundTag.TAG_COMPOUND) && level != null)
            setPaint(NbtUtils.readBlockState(level.holderLookup(Registries.BLOCK), tag.getCompound("paintblock")));
    }
    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.saveAdditional(tag, lookup);
        if (paint != null) tag.put("paint", NbtUtils.writeBlockState(paint));
    }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.loadAdditional(tag, lookup);
        paint = tag.contains("paint", CompoundTag.TAG_COMPOUND) ? NbtUtils.readBlockState(lookup.lookupOrThrow(Registries.BLOCK), tag.getCompound("paint")) : null;
        requestModelDataUpdate();
    }
    @Override public ModelData getModelData() { return paint == null ? ModelData.EMPTY : ModelData.builder().with(CableModelData.PAINT, paint).build(); }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup) { return saveWithoutMetadata(lookup); }
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider lookup) {
        super.onDataPacket(connection, packet, lookup);
        requestModelDataUpdate();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }
}
