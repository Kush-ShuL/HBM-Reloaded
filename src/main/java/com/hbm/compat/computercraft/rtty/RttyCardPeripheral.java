package com.hbm.compat.computercraft.rtty;

import com.hbm.item.rttycard.RttyCardContent;
import com.hbm.world.structure.templates.radio.RTTYSystem;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IDynamicPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/** One original RTTY managed environment, now hosted by a genuine CC upgrade slot. */
final class RttyCardPeripheral implements IDynamicPeripheral {
    private static final String[] METHODS = { "open", "bindEvent", "unbindEvent" };
    private final RttyCardHost host;
    private final String address;
    private final ConcurrentHashMap<String, Long> boundEvents = new ConcurrentHashMap<>();
    private final Set<IComputerAccess> computers = ConcurrentHashMap.newKeySet();
    private volatile ServerLevel world;
    private volatile boolean installed = true;

    RttyCardPeripheral(RttyCardHost host) {
        this.host = host;
        world = host.level();
        ItemStack card = host.card();
        if (!card.is(RttyCardContent.CARD.get())) throw new IllegalStateException("RTTY environment requires its actual installed card");
        CompoundTag data = data(card);
        String savedAddress = data.getCompound("node").getString("address");
        address = savedAddress.isEmpty() ? UUID.randomUUID().toString() : savedAddress;
        ListTag events = data.getList("boundEvents", 8);
        for (int i = 0; i < events.size(); i++) boundEvents.put(events.getString(i), -1L);
        persist(card);
    }

    @Override public String getType() { return "rtty"; }
    @Override public String[] getMethodNames() { return METHODS.clone(); }
    @Override public boolean equals(IPeripheral other) { return other == this; }
    @Override public Object getTarget() { return this; }
    @Override public void attach(IComputerAccess computer) { computers.add(computer); }
    @Override public void detach(IComputerAccess computer) { computers.remove(computer); }

    @Override public MethodResult callMethod(IComputerAccess computer, ILuaContext context, int method, IArguments args) throws LuaException {
        String channel = parseChannel(args);
        return switch (method) {
            case 0 -> { requireWorld(); yield MethodResult.of(new RttyChannelHandle(this, address, channel)); }
            // Native host component mutation is a main-thread task, including the physical upgrade save.
            case 1, 2 -> context.executeMainThreadTask(() -> {
                requireWorld();
                ItemStack card = currentCard();
                if (method == 1) boundEvents.put(channel, -1L);
                else boundEvents.remove(channel);
                persist(card);
                return new Object[0]; // Original bindEvent documentation says boolean, but returns no values.
            });
            default -> throw new LuaException("No such method");
        };
    }

    /** Called by the native upgrade's server tick, even when no events are bound. */
    void tick() {
        ItemStack card = host.card();
        ServerLevel level = host.level();
        if (level == null || !card.is(RttyCardContent.CARD.get()) || !address.equals(data(card).getCompound("node").getString("address"))) {
            installed = false;
            world = null;
            return;
        }
        world = level;
        if (boundEvents.isEmpty()) return;
        for (var event : boundEvents.entrySet()) {
            RTTYSystem.RTTYChannel message = RTTYSystem.listen(level, event.getKey());
            if (message == null || message.timeStamp <= event.getValue()) continue;
            // Avoid overriding a simultaneous unbind/rebind; the source map is concurrent as well.
            if (!boundEvents.replace(event.getKey(), event.getValue(), message.timeStamp)) continue;
            for (IComputerAccess computer : computers) computer.queueEvent(event.getKey(), message.signal);
        }
    }

    ServerLevel requireWorld() throws LuaException {
        ServerLevel level = world;
        if (!installed || level == null || computers.isEmpty()) throw new LuaException("no such component");
        return level;
    }
    void requireAddress(String expected) throws LuaException {
        if (!address.equals(expected)) throw new LuaException("no such component");
        requireWorld();
    }
    private ItemStack currentCard() throws LuaException {
        ItemStack card = host.card();
        if (!card.is(RttyCardContent.CARD.get()) || !address.equals(data(card).getCompound("node").getString("address"))) {
            installed = false;
            world = null;
            throw new LuaException("no such component");
        }
        return card;
    }
    private void persist(ItemStack card) {
        CustomData.update(DataComponents.CUSTOM_DATA, card, root -> {
            CompoundTag data = root.getCompound("oc:data");
            CompoundTag node = data.getCompound("node");
            node.putString("address", address);
            data.put("node", node);
            if (boundEvents.isEmpty()) data.remove("boundEvents");
            else {
                ListTag events = new ListTag();
                for (String event : boundEvents.keySet()) events.add(StringTag.valueOf(event));
                data.put("boundEvents", events);
            }
            root.put("oc:data", data);
        });
        host.save(card);
    }
    private static CompoundTag data(ItemStack card) {
        CustomData custom = card.get(DataComponents.CUSTOM_DATA);
        return custom == null ? new CompoundTag() : custom.copyTag().getCompound("oc:data");
    }
    private static String parseChannel(IArguments args) throws LuaException {
        String channel = args.getString(0);
        if (channel.isEmpty()) throw new LuaException("bad argument #1 (channel name expected, got no value)");
        return channel;
    }
}
