package com.hbm.network.dispatch;

import com.hbm.bomb.nuclear.programmable.TimedBalefireState;
import com.hbm.door.generic.GenericDoorStatePacket;
import com.hbm.machine.fensu.FensuPackets;
import com.hbm.machine.massstorage.MassStoragePackets;
import com.hbm.machine.radar.RadarStatePacket;
import com.hbm.machine.reactor.rbmk.console.RbmkConsoleNetwork;
import com.hbm.machine.reactorcontroller.ReactorControllerPackets;
import com.hbm.machine.soyuzlauncher.SoyuzLauncherSync;
import com.hbm.missile.custom.launcher.CustomLauncherSync;
import com.hbm.world.oldlantern.OldLanternPacket;
import com.hbm.world.structure.cabinet.CabinetAnimationPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;

/** BufPacket descendants whose already-existing native transport is a typed payload. */
final class SourceLoadedTypedSnapshots {
    private SourceLoadedTypedSnapshots() {}

    static Packet<?> freeze(MinecraftServer server,CustomPacketPayload payload) {
        if (payload instanceof FensuPackets.State value) return copy(server,value,FensuPackets.State.CODEC);
        if (payload instanceof RbmkConsoleNetwork.Update value) return copy(server,value,RbmkConsoleNetwork.Update.CODEC);
        if (payload instanceof RadarStatePacket value) return copy(server,value,RadarStatePacket.CODEC);
        if (payload instanceof SoyuzLauncherSync value) return copy(server,value,SoyuzLauncherSync.CODEC);
        if (payload instanceof ReactorControllerPackets.Snapshot value) return copy(server,value,ReactorControllerPackets.Snapshot.CODEC);
        if (payload instanceof TimedBalefireState value) return copy(server,value,TimedBalefireState.CODEC);
        if (payload instanceof OldLanternPacket value) return copy(server,value,OldLanternPacket.CODEC);
        if (payload instanceof GenericDoorStatePacket value) return copy(server,value,GenericDoorStatePacket.CODEC);
        if (payload instanceof CabinetAnimationPacket value) return copy(server,value,CabinetAnimationPacket.CODEC);
        // Its multipart branch replaces TEMissileMultipartPacket, which was NOT threaded.
        if (payload instanceof CustomLauncherSync value) return value.data().contains("multipart") ? null
                : copy(server,value,CustomLauncherSync.CODEC);
        // The existing codec is package-private; copy every record field and the entire component-bearing stack.
        if (payload instanceof MassStoragePackets.State value) return new ClientboundCustomPayloadPacket(
                new MassStoragePackets.State(value.pos().immutable(),value.stockpile(),value.output(),value.filter().copy()));
        return null;
    }

    private static <T extends CustomPacketPayload> Packet<?> copy(MinecraftServer server,T value,
            StreamCodec<? super RegistryFriendlyByteBuf,T> codec) {
        return new ClientboundCustomPayloadPacket(SourcePacketSnapshot.copy(server,value,codec));
    }
}
