package com.hbm.machine.turret;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Source muzzle and casing packets retain the turret-block origin for the original fifty-block recipient radius. */
public final class TurretEffects {
    public static Consumer<Packet> client=ignored->{};
    public record Packet(double x,double y,double z,CompoundTag data) implements CustomPacketPayload {
        public static final Type<Packet> TYPE=new Type<>(ResourceLocation.parse("hbm:turret_effect"));
        public static final StreamCodec<RegistryFriendlyByteBuf,Packet> CODEC=StreamCodec.of((b,p)->{b.writeDouble(p.x);b.writeDouble(p.y);b.writeDouble(p.z);b.writeNbt(p.data);},b->new Packet(b.readDouble(),b.readDouble(),b.readDouble(),b.readNbt()));
        @Override public Type<Packet> type(){return TYPE;}
    }
    public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(Packet.TYPE,Packet.CODEC,(packet,context)->context.enqueueWork(()->client.accept(packet)));}
    public static void send(ServerLevel level,BlockPos origin,double x,double y,double z,CompoundTag data){var packet=new Packet(x,y,z,data);try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginPayloadGroup(level.getServer(),packet)){for(var player:level.players())if(player.distanceToSqr(origin.getX(),origin.getY(),origin.getZ())<=2500)PacketDistributor.sendToPlayer(player,packet);dispatchScope.commit();}}
    private TurretEffects(){}
}
