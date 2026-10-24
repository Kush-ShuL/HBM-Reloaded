package com.hbm.network.dispatch;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.attachment.AttachmentHolder;

/** Carries only the existing single-update fanout across native packet construction and its loop. */
public final class AttachmentFanoutScope implements AutoCloseable {
    private static final ThreadLocal<AttachmentFanoutScope> CURRENT=new ThreadLocal<>();
    private final AttachmentFanoutScope previous;
    private final MinecraftServer server;
    private final boolean playerUpdate;
    private Packet<?> updatePacket;
    private PacketDispatch.Scope group;

    private AttachmentFanoutScope(AttachmentHolder holder) {
        previous=CURRENT.get();
        playerUpdate=holder instanceof ServerPlayer;
        server=holder instanceof ServerPlayer player?player.serverLevel().getServer():null;
        CURRENT.set(this);
    }

    public static AttachmentFanoutScope begin(AttachmentHolder holder) {
        return new AttachmentFanoutScope(holder);
    }

    public static void packetBuilt(Packet<?> packet) {
        var current=CURRENT.get();
        if(current!=null&&current.server!=null) {
            current.updatePacket=packet;
            current.group=PacketDispatch.beginGroup(current.server,packet);
        }
    }

    /** The exact packet produced by native syncUpdate, rather than an indistinguishable initial packet. */
    public static boolean isUpdatePacket(Packet<?> packet) {
        var current=CURRENT.get();
        return current!=null&&current.playerUpdate&&packet!=null&&current.updatePacket==packet;
    }

    public void commit() {
        if(group!=null)group.commit();
    }

    @Override public void close() {
        try {
            if(group!=null)group.close();
        } finally {
            if(previous==null)CURRENT.remove();else CURRENT.set(previous);
        }
    }
}
