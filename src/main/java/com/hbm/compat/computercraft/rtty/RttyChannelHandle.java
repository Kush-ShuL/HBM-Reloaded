package com.hbm.compat.computercraft.rtty;

import com.hbm.world.structure.templates.radio.RTTYSystem;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.IDynamicLuaObject;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;

/** Source channel handle: independent volatile lastRead; only the two Sync methods yield to the next native main-thread task. */
final class RttyChannelHandle implements IDynamicLuaObject {
    private static final String[] METHODS = { "channel", "read", "write", "readSync", "writeSync" };
    private final RttyCardPeripheral card;
    private final String cardAddress;
    private final String channel;
    private long lastRead = -1;
    RttyChannelHandle(RttyCardPeripheral card, String address, String channel) { this.card = card; cardAddress = address; this.channel = channel; }
    @Override public String[] getMethodNames() { return METHODS.clone(); }
    @Override public MethodResult callMethod(ILuaContext context, int method, IArguments args) throws LuaException {
        return switch (method) {
            case 0 -> MethodResult.of(channel);
            case 1 -> MethodResult.of(read());
            case 2 -> { write(signal(args)); yield MethodResult.of(); }
            case 3 -> context.executeMainThreadTask(this::read);
            case 4 -> {
                Object signal = signal(args); // Copy out of IArguments before a deferred native task.
                yield context.executeMainThreadTask(() -> { write(signal); return new Object[0]; });
            }
            default -> throw new LuaException("No such method");
        };
    }
    private synchronized Object[] read() throws LuaException {
        card.requireAddress(cardAddress);
        RTTYSystem.RTTYChannel message = RTTYSystem.listen(card.requireWorld(), channel);
        if (message == null) return new Object[] { null, false };
        boolean fresh = message.timeStamp > lastRead;
        lastRead = message.timeStamp;
        return new Object[] { message.signal, fresh };
    }
    private void write(Object signal) throws LuaException {
        card.requireAddress(cardAddress);
        RTTYSystem.broadcast(card.requireWorld(), channel, signal);
    }
    private static Object signal(IArguments args) throws LuaException {
        Object value = "string".equals(args.getType(0)) ? args.getString(0) : args.get(0);
        if (value == null) throw new LuaException("bad argument #1 (value expected)");
        return value;
    }
}
