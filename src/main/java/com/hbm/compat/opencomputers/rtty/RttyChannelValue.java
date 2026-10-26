package com.hbm.compat.opencomputers.rtty;

import com.hbm.world.structure.templates.radio.RTTYSystem;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.prefab.AbstractValue;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

/** A persistable native OC userdata value with the original per-handle read cursor. */
public final class RttyChannelValue extends AbstractValue {
    private String cardAddress;
    private String channel;
    private long lastRead = -1;

    /** Required by OC's value deserializer. */
    public RttyChannelValue() { }

    RttyChannelValue(String cardAddress, String channel) {
        this.cardAddress = cardAddress;
        this.channel = channel;
    }

    @Callback(direct = true, doc = "function():string -- Returns the channel name this handle is bound to")
    public Object[] channel(Context context, Arguments args) { return new Object[] { channel }; }

    @Callback(direct = false, doc = "function():any, boolean -- Reads the current signal; takes one Minecraft tick")
    public Object[] readSync(Context context, Arguments args) { return read(context, args); }

    @Callback(direct = false, doc = "function(signal:any) -- Writes a signal; takes one Minecraft tick")
    public Object[] writeSync(Context context, Arguments args) { return write(context, args); }

    @Callback(direct = true, doc = "function():any, boolean -- Reads the current signal from this handle's channel")
    public synchronized Object[] read(Context context, Arguments args) {
        RTTYSystem.RTTYChannel message = RTTYSystem.listen(world(context), channel);
        if (message == null) return new Object[] { null, false };
        boolean fresh = message.timeStamp > lastRead;
        lastRead = message.timeStamp;
        return new Object[] { message.signal, fresh };
    }

    @Callback(direct = true, doc = "function(signal:any) -- Writes the given signal to this handle's channel")
    public Object[] write(Context context, Arguments args) {
        Object signal = args.isString(0) ? args.checkString(0) : args.checkAny(0);
        RTTYSystem.broadcast(world(context), channel, signal);
        return new Object[0];
    }

    @Override public void save(CompoundTag nbt) {
        nbt.putString("cardAddress", cardAddress);
        nbt.putString("channel", channel);
    }

    @Override public void load(CompoundTag nbt) {
        cardAddress = nbt.getString("cardAddress");
        channel = nbt.getString("channel");
        lastRead = -1;
    }

    private Level world(Context context) {
        Node computer = context.node();
        Node card = computer.network() == null ? null : computer.network().node(cardAddress);
        if (card == null || !(card.host() instanceof RttyCardEnvironment environment)
                || environment.world() == null || environment.world().isClientSide) {
            throw new IllegalArgumentException("no such component");
        }
        return environment.world();
    }
}
