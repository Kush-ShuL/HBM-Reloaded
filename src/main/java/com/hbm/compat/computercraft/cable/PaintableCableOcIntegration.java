package com.hbm.compat.computercraft.cable;

import com.hbm.compat.opencomputers.HbmOpenComputerCapabilities;
import java.util.EnumMap;
import li.cil.oc.api.Network;
import li.cil.oc.api.internal.Colored;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.SidedEnvironment;
import li.cil.oc.api.network.Visibility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

/** A native OC graph alongside the independent CC graph; neither routes into the other. */
final class PaintableCableOcIntegration {
    private static final BlockCapability<Colored, Direction> COLORED = BlockCapability.createSided(
            ResourceLocation.fromNamespaceAndPath("opencomputers", "colored"), Colored.class);

    static void register(IEventBus bus) {
        PaintableComputerCableEntity.OC_NETWORK_FACTORY = Host::new;
        bus.addListener(PaintableCableOcIntegration::capabilities);
    }

    private static void capabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(HbmOpenComputerCapabilities.ENVIRONMENT, PaintableComputerCableContent.ENTITY.get(),
                (cable, side) -> host(cable));
        event.registerBlockEntity(HbmOpenComputerCapabilities.SIDED_ENVIRONMENT, PaintableComputerCableContent.ENTITY.get(),
                (cable, side) -> host(cable));
        event.registerBlockEntity(COLORED, PaintableComputerCableContent.ENTITY.get(),
                (cable, side) -> new Colored() {
                    @Override public int getColor() { return cable.color(); }
                    @Override public void setColor(int color) { cable.setColor(SourceCableColors.fromInt(color)); }
                    @Override public boolean controlsConnectivity() { return true; }
                });
    }

    private static Host host(PaintableComputerCableEntity cable) {
        return cable.ocNetwork() instanceof Host host && !host.closed ? host : null;
    }

    private static boolean ready(ServerLevel level, BlockPos pos) {
        return level.getChunkSource().getChunkNow(pos.getX() >> 4, pos.getZ() >> 4) != null;
    }

    private static final class Host implements Environment, SidedEnvironment, PaintableCableNetwork {
        private final PaintableComputerCableEntity cable;
        private final Node node;
        private final EnumMap<Direction, Node> links = new EnumMap<>(Direction.class);
        private boolean dirty = true;
        private boolean closed;

        private Host(PaintableComputerCableEntity cable) {
            this.cable = cable;
            node = Network.newNode(this, Visibility.None).create();
        }

        @Override public Node node() { return closed ? null : node; }
        @Override public Node sidedNode(Direction side) { return canConnect(side) ? node() : null; }

        @Override public boolean canConnect(Direction side) {
            if (closed || side == null || cable.isRemoved() || !(cable.getLevel() instanceof ServerLevel level)) return false;
            BlockPos adjacent = cable.getBlockPos().relative(side);
            if (!ready(level, adjacent)) return false;
            Colored neighbor = level.getCapability(COLORED, adjacent, side.getOpposite());
            // The original HBM cable requires equal RGB when a neighbor provides a color.
            return neighbor == null || neighbor.getColor() == cable.color();
        }

        @Override public void changed() { dirty = true; }

        @Override public void tick() {
            if (closed || cable.isRemoved() || !(cable.getLevel() instanceof ServerLevel level)) return;
            if (!dirty && level.getGameTime() % 20 != 0) return;
            dirty = false;
            if (node.network() == null) Network.joinNewNetwork(node);
            for (Direction side : Direction.values()) {
                BlockPos adjacent = cable.getBlockPos().relative(side);
                Node next = null;
                if (!ready(level, adjacent)) dirty = true;
                else if (canConnect(side)) {
                    var sided = level.getCapability(HbmOpenComputerCapabilities.SIDED_ENVIRONMENT, adjacent, side.getOpposite());
                    if (sided != null) next = sided.sidedNode(side.getOpposite());
                    else {
                        var environment = level.getCapability(HbmOpenComputerCapabilities.ENVIRONMENT, adjacent, side.getOpposite());
                        if (environment != null) next = environment.node();
                    }
                }
                Node previous = links.get(side);
                if (previous != next && previous != null) node.disconnect(previous);
                if (next == null) links.remove(side);
                else {
                    links.put(side, next);
                    node.connect(next);
                }
            }
        }

        @Override public void onConnect(Node connected) { if (connected == node) cable.setChanged(); }
        @Override public void onDisconnect(Node disconnected) { if (disconnected == node) dirty = true; }
        @Override public void onMessage(Message message) { }
        @Override public void load(CompoundTag tag) { node.load(tag); }
        @Override public void save(CompoundTag tag) { node.save(tag); }
        @Override public void close() { if (!closed) { closed = true; node.remove(); links.clear(); } }
    }

    private PaintableCableOcIntegration() { }
}
