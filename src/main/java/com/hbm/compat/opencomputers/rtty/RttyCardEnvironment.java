package com.hbm.compat.opencomputers.rtty;

import com.hbm.world.structure.templates.radio.RTTYSystem;
import java.util.concurrent.ConcurrentHashMap;
import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.AbstractManagedEnvironment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.level.Level;

/** Source RTTY environment; OC owns its network, ticking and item persistence. */
public final class RttyCardEnvironment extends AbstractManagedEnvironment {
    private final EnvironmentHost host;
    private final ConcurrentHashMap<String, Long> boundEvents = new ConcurrentHashMap<>();

    RttyCardEnvironment(EnvironmentHost host) {
        this.host = host;
        setNode(Network.newNode(this, Visibility.Neighbors).withComponent("rtty").create());
    }

    Level world() { return host.world(); }

    @Callback(direct = true, doc = "function(channel:string):handle -- Opens a handle to the given RTTY channel")
    public Object[] open(Context context, Arguments args) {
        return new Object[] { new RttyChannelValue(node().address(), parseChannel(args)) };
    }

    @Callback(direct = true, doc = "function(channel:string) -- Binds an OS event to the given RTTY channel")
    public Object[] bindEvent(Context context, Arguments args) {
        boundEvents.put(parseChannel(args), -1L);
        return new Object[0];
    }

    @Callback(direct = true, doc = "function(channel:string) -- Unbinds an OS event from the given RTTY channel")
    public Object[] unbindEvent(Context context, Arguments args) {
        boundEvents.remove(parseChannel(args));
        return new Object[0];
    }

    @Override public boolean canUpdate() { return true; }

    @Override public void update() {
        Level world = world();
        if (boundEvents.isEmpty() || world == null || world.isClientSide || node().network() == null) return;
        for (var event : boundEvents.entrySet()) {
            RTTYSystem.RTTYChannel message = RTTYSystem.listen(world, event.getKey());
            if (message == null || message.timeStamp <= event.getValue()) continue;
            if (boundEvents.replace(event.getKey(), event.getValue(), message.timeStamp)) {
                node().sendToReachable("computer.signal", event.getKey(), message.signal);
            }
        }
    }

    @Override public void save(CompoundTag nbt) {
        super.save(nbt);
        if (boundEvents.isEmpty()) {
            nbt.remove("boundEvents");
        } else {
            ListTag events = new ListTag();
            for (String event : boundEvents.keySet()) events.add(StringTag.valueOf(event));
            nbt.put("boundEvents", events);
        }
    }

    @Override public void load(CompoundTag nbt) {
        super.load(nbt);
        boundEvents.clear();
        ListTag events = nbt.getList("boundEvents", 8);
        for (int i = 0; i < events.size(); i++) boundEvents.put(events.getString(i), -1L);
    }

    private static String parseChannel(Arguments args) {
        String channel = args.checkString(0);
        if (channel.isEmpty()) throw new IllegalArgumentException("bad argument #1 (channel name expected, got no value)");
        return channel;
    }
}
