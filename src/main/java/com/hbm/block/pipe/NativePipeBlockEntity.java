package com.hbm.block.pipe;

import com.hbm.energy.NodeConnection;
import com.hbm.fluid.FluidTank;
import com.hbm.fluid.HbmFluidType;
import com.hbm.fluid.HbmFluids;
import com.hbm.fluid.network.FluidNetworks;
import com.hbm.fluid.network.FluidNode;
import com.hbm.fluid.network.FluidUser;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class NativePipeBlockEntity extends BlockEntity implements FluidUser,com.hbm.api.machine.IFluidCopiable {
    private static final HbmFluidType[] SMOKES = {HbmFluids.SMOKE, HbmFluids.SMOKE_LEADED, HbmFluids.SMOKE_POISON};
    protected HbmFluidType type = HbmFluids.NONE;
    protected FluidNode node;
    private final FluidNode[] exhaustNodes = new FluidNode[3];
    private final List<BlockPos> connected = new ArrayList<>();
    private BlockState paint;
    private boolean chunkUnloaded;
    public NativePipeBlockEntity(BlockPos pos, BlockState state) { this(PipeContent.ENTITY.get(), pos, state); }
    protected NativePipeBlockEntity(net.minecraft.world.level.block.entity.BlockEntityType<?> type, BlockPos pos, BlockState state) { super(type, pos, state); }
    public PipeKind kind() { return ((NativePipeBlock) getBlockState().getBlock()).kind(); }
    public HbmFluidType getFluidType() { return type; }
    public HbmFluidType[] networkTypes() { return kind().exhaust() ? SMOKES.clone() : new HbmFluidType[]{type}; }
    public Direction back() { return getBlockState().getValue(SpecialPipeBlock.FACING).getOpposite(); }
    @Override public boolean canConnect(HbmFluidType type, Direction side) {
        if (side == null || kind().anchor() && side != back()) return false;
        return kind().exhaust() ? type == SMOKES[0] || type == SMOKES[1] || type == SMOKES[2] : this.type == type;
    }
    @Override public FluidTank[] getAllTanks() { return FluidTank.EMPTY_ARRAY; }
    @Override public boolean supportsSettingsCopy() { return !kind().exhaust(); }
    @Override public int[] getFluidIDToCopy() { return new int[]{type.getID()}; }
    @Override public FluidTank getTankToPaste() { return null; }
    @Override public void pasteSettings(CompoundTag data,int index,Level world,net.minecraft.world.entity.player.Player player,BlockPos pos) {
        int[] ids=data.getIntArray("fluidID");if(ids.length==0||kind().exhaust()) return;
        HbmFluidType selected=HbmFluids.fromID(index>=0&&index<ids.length?ids[index]:0);
        if(PipeControls.flags(player).control()) NativePipeBlock.changeType(world,pos,type,selected,64);else setType(selected);
    }
    public void setType(HbmFluidType type) {
        if (kind().exhaust()) return;
        removeNodes(); this.type = type; sync();
    }
    public BlockState paint() { return paint; }
    public void setPaint(BlockState paint) { this.paint = paint; sync(); }
    public void sync() {
        setChanged();
        if (level != null) { level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3); level.invalidateCapabilities(worldPosition); }
    }
    public void updateState() { if ((kind() == PipeKind.VALVE || kind() == PipeKind.COUNTER) && !getBlockState().getValue(NativePipeBlock.OPEN)) removeNodes(); }
    public static void tick(Level level, BlockPos position, BlockState state, NativePipeBlockEntity pipe) {
        if (level instanceof ServerLevel server) pipe.serverTick(server);
    }
    protected void serverTick(ServerLevel server) {
        if ((kind() == PipeKind.VALVE || kind() == PipeKind.COUNTER) && !getBlockState().getValue(NativePipeBlock.OPEN)) { removeNodes(); return; }
        if (kind().exhaust()) for (int i = 0; i < SMOKES.length; i++) exhaustNodes[i] = ensureNode(server, SMOKES[i], exhaustNodes[i]);
        else node = ensureNode(server, type, node);
    }
    private FluidNode ensureNode(ServerLevel server, HbmFluidType type, FluidNode existing) {
        if (existing == null || existing.isExpired()) {
            var space = FluidNetworks.nodespace(server, type);
            existing = space.getNode(worldPosition);
            if (existing == null || existing.isExpired()) {
                existing = new FluidNode(worldPosition);
                if (kind().anchor()) {
                    existing.setConnections(new NodeConnection(worldPosition.relative(back()), back()));
                    for (BlockPos target : connected) existing.addRemoteConnection(target);
                }
                space.createNode(existing);
            }
        }
        return existing;
    }
    protected void removeNodes() {
        if (level instanceof ServerLevel server) {
            if (node != null) FluidNetworks.nodespace(server, type).destroyNode(node);
            for (int i = 0; i < exhaustNodes.length; i++) {
                if (exhaustNodes[i] != null) FluidNetworks.nodespace(server, SMOKES[i]).destroyNode(exhaustNodes[i]);
                exhaustNodes[i] = null;
            }
        }
        node = null;
    }
    public List<BlockPos> connected() { return List.copyOf(connected); }
    public Vec3 connectionPoint() { return Vec3.atCenterOf(worldPosition); }
    public double maxPipeLength() { return 10; }
    /** Same status order, including adopting NONE before measuring the connection length. */
    public static int canLink(NativePipeBlockEntity first, NativePipeBlockEntity second) {
        if (!first.kind().anchor() || !second.kind().anchor() || first.kind() != second.kind()) return 1;
        if (first == second) return 2;
        if (!first.kind().exhaust()) {
            if (first.type == HbmFluids.NONE && second.type != HbmFluids.NONE) first.setType(second.type);
            if (second.type == HbmFluids.NONE && first.type != HbmFluids.NONE) second.setType(first.type);
            if (first.type != second.type) return 4;
        }
        return Math.min(first.maxPipeLength(), second.maxPipeLength()) >= first.connectionPoint().distanceTo(second.connectionPoint()) ? 0 : 3;
    }
    public void addConnection(BlockPos target) {
        if (!kind().anchor()) return;
        connected.add(target.immutable());
        if (level instanceof ServerLevel server) {
            serverTick(server);
            if (node != null) node.addRemoteConnection(target);
            for (FluidNode exhaust : exhaustNodes) if (exhaust != null) exhaust.addRemoteConnection(target);
        }
        sync();
    }
    public void disconnectAll() {
        if (level != null && !level.isClientSide) {
            for (BlockPos target : connected) if (!target.equals(worldPosition) && level.hasChunkAt(target)
                    && level.getBlockEntity(target) instanceof NativePipeBlockEntity other && other.kind().anchor()) {
                other.connected.removeIf(worldPosition::equals); other.removeNodes(); other.sync();
            }
            removeNodes();
        }
    }
    @Override public void onLoad() {
        super.onLoad(); chunkUnloaded = false;
        // Adopt retained topology before a newly loaded pipe can be broken or retyped.
        if (level instanceof ServerLevel server) {
            if (kind().exhaust()) for (int i = 0; i < SMOKES.length; i++) exhaustNodes[i] = FluidNetworks.nodespace(server, SMOKES[i]).getNode(worldPosition);
            else node = FluidNetworks.nodespace(server, type).getNode(worldPosition);
            updateState();
        }
    }
    @Override public void setRemoved() { if (!chunkUnloaded) removeNodes(); super.setRemoved(); }
    @Override public void onChunkUnloaded() { chunkUnloaded = true; super.onChunkUnloaded(); }
    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries); tag.putInt("type", type.getID());
        if (paint != null) tag.put("paint", NbtUtils.writeBlockState(paint));
        if (kind().anchor()) {
            tag.putInt("conCount", connected.size());
            for (int i = 0; i < connected.size(); i++) { var pos = connected.get(i); tag.putIntArray("con" + i, new int[]{pos.getX(), pos.getY(), pos.getZ()}); }
        }
    }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries); type = HbmFluids.fromID(tag.getInt("type"));
        paint = tag.contains("paint") ? NbtUtils.readBlockState(registries.lookupOrThrow(Registries.BLOCK), tag.getCompound("paint")) : null;
        connected.clear();
        for (int i = 0; i < tag.getInt("conCount"); i++) { int[] pos = tag.getIntArray("con" + i); if (pos.length == 3) connected.add(new BlockPos(pos[0], pos[1], pos[2])); }
    }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries) { return saveWithoutMetadata(registries); }
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
}
