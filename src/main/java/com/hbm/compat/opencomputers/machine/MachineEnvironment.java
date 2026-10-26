package com.hbm.compat.opencomputers.machine;

import com.hbm.api.computer.ComputerArguments;
import com.hbm.api.computer.ComputerControl;
import com.hbm.api.computer.ComputerException;
import com.hbm.compat.opencomputers.HbmOpenComputerCapabilities;
import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.ManagedPeripheral;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.SidedEnvironment;
import li.cil.oc.api.network.Visibility;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Objects;
import java.util.Set;

/** One stable wrapper per block entity, persisted as an attachment rather than an adapter component. */
final class MachineEnvironment implements ManagedEnvironment, ManagedPeripheral, SidedEnvironment, INBTSerializable<CompoundTag> {
    private final BlockEntity owner;
    private final ComputerControl control;
    private Node node;
    private CompoundTag savedNode = new CompoundTag();
    private String componentName;
    private volatile String[] methods = new String[0];
    private int sides;

    MachineEnvironment(IAttachmentHolder holder) {
        if (!(holder instanceof BlockEntity entity) || !(entity instanceof ComputerControl computer)) {
            throw new IllegalArgumentException("An OC machine environment requires a ComputerControl block entity");
        }
        owner = entity;
        control = computer;
    }

    BlockEntity owner() { return owner; }

    static boolean available(BlockEntity owner) {
        if (owner.isRemoved() || !(owner.getLevel() instanceof ServerLevel level) || !level.getServer().isSameThread()) return false;
        var chunk = level.getChunkSource().getChunkNow(owner.getBlockPos().getX() >> 4, owner.getBlockPos().getZ() >> 4);
        return chunk != null && chunk.getBlockEntity(owner.getBlockPos()) == owner;
    }

    static int connectionMask(ComputerControl control) {
        int mask = 0;
        for (Direction side : Direction.values()) if (control.canComputerConnect(side)) mask |= 1 << side.ordinal();
        return mask;
    }

    void activate() { refreshDescription(); }

    private void refreshDescription() {
        String name = control.computerType();
        if (!Objects.equals(componentName, name)) {
            release();
            componentName = name;
        }
        String[] currentMethods = control.computerMethods();
        if (!Arrays.equals(methods, currentMethods)) methods = currentMethods == null ? new String[0] : currentMethods.clone();
    }

    @Override
    public Node node() {
        if (!available(owner) || connectionMask(control) == 0) return null;
        if (node == null) {
            refreshDescription();
            if (componentName == null || componentName.isBlank()) return null;
            var builder = Network.newNode(this, Visibility.Network);
            if (builder == null) return null;
            node = builder.withComponent(componentName, Visibility.Network).create();
            if (node != null) node.load(savedNode);
        }
        return node;
    }

    @Override public Node sidedNode(Direction side) { return canConnect(side) ? node() : null; }
    @Override public boolean canConnect(Direction side) {
        return available(owner) && (side == null ? connectionMask(control) != 0 : control.canComputerConnect(side));
    }
    @Override public boolean canUpdate() { return false; }
    @Override public void update() {}
    @Override public String[] methods() { return methods.clone(); }

    @Override
    public Object[] invoke(String method, Context context, Arguments arguments) throws Exception {
        if (!available(owner)) throw new IllegalStateException("Machine is no longer available");
        refreshDescription();
        Node current = node;
        if (current == null || current.network() == null) throw new IllegalStateException("Machine is no longer connected");
        // A previously acquired component must not keep using a now-denied side or a removed cable.
        pruneConnections();
        Node computer = context == null ? null : context.node();
        if (computer == null || current.network() == null || computer.network() != current.network()
                || !current.canBeReachedFrom(computer)) throw new IllegalStateException("Machine is no longer connected on an allowed side");
        if (!Arrays.asList(methods).contains(method)) throw new NoSuchMethodException(method);
        try {
            // ManagedPeripheral's OC callbacks are non-direct, so world access stays on the server thread.
            return control.callComputer(method, new ComputerArguments(arguments.toArray()));
        } catch (ComputerException error) {
            throw new IllegalArgumentException(error.getMessage(), error);
        }
    }

    boolean tick() {
        if (!available(owner)) return false;
        refreshDescription();
        int currentSides = connectionMask(control);
        if (currentSides == 0) {
            release();
            sides = 0;
            return true;
        }
        Node current = node();
        if (current == null) return true;
        if (sides != currentSides || current.network() == null) {
            pruneConnections();
            Network.joinOrCreateNetwork(owner);
            sides = currentSides;
        }
        rememberAddress();
        return true;
    }

    private void pruneConnections() {
        if (node == null || node.network() == null) return;
        ServerLevel level = (ServerLevel) owner.getLevel();
        Set<Node> allowed = Collections.newSetFromMap(new IdentityHashMap<>());
        for (Direction side : Direction.values()) {
            if (!control.canComputerConnect(side)) continue;
            var position = owner.getBlockPos().relative(side);
            var chunk = level.getChunkSource().getChunkNow(position.getX() >> 4, position.getZ() >> 4);
            if (chunk == null) continue;
            BlockEntity neighbor = chunk.getBlockEntity(position);
            if (neighbor == null || neighbor.isRemoved()) continue;
            var state = chunk.getBlockState(position);
            Direction opposite = side.getOpposite();
            SidedEnvironment sided = level.getCapability(HbmOpenComputerCapabilities.SIDED_ENVIRONMENT, position, state, neighbor, opposite);
            Node remote;
            if (sided != null) remote = sided.sidedNode(opposite);
            else {
                Environment environment = level.getCapability(HbmOpenComputerCapabilities.ENVIRONMENT, position, state, neighbor, opposite);
                remote = environment == null ? null : environment.node();
            }
            if (remote != null) allowed.add(remote);
        }
        // Disconnect may split the graph, so take a snapshot before editing it.
        var neighbors = new java.util.ArrayList<Node>();
        node.neighbors().forEach(neighbors::add);
        for (Node neighbor : neighbors) if (!allowed.contains(neighbor)) node.disconnect(neighbor);
    }

    private void rememberAddress() {
        if (node != null && node.address() != null && !node.address().equals(savedNode.getString("address"))) {
            node.save(savedNode);
            owner.setChanged();
        }
    }

    void release() {
        if (node != null) {
            node.save(savedNode);
            node.remove();
            node = null;
        }
        sides = 0;
    }

    @Override public void onConnect(Node connected) { if (connected == node) rememberAddress(); }
    @Override public void onDisconnect(Node disconnected) {}
    @Override public void onMessage(Message message) {}

    @Override public void load(CompoundTag tag) {
        release();
        savedNode = tag.getCompound("node").copy();
    }
    @Override public void save(CompoundTag tag) {
        CompoundTag saved = savedNode.copy();
        if (node != null) node.save(saved);
        tag.put("node", saved);
    }
    @Override public CompoundTag serializeNBT(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        save(tag);
        return tag;
    }
    @Override public void deserializeNBT(HolderLookup.Provider registries, CompoundTag tag) { load(tag); }
}
