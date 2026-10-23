package com.hbm.machine.soldering;
import com.hbm.item.tool.WeldingParticlePacket;import net.minecraft.core.BlockPos;import net.minecraft.network.RegistryFriendlyByteBuf;import net.minecraft.network.codec.StreamCodec;import net.minecraft.network.protocol.common.custom.CustomPacketPayload;import net.minecraft.server.level.ServerLevel;import net.minecraft.world.phys.Vec3;import net.neoforged.neoforge.network.PacketDistributor;import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Original toggle control, authenticated through the actually open usable machine menu. */
public record SolderingControl(int containerId) implements CustomPacketPayload {
 public static final Type<SolderingControl> TYPE=new Type<>(SolderingContent.id("soldering_collision"));public static final StreamCodec<RegistryFriendlyByteBuf,SolderingControl> CODEC=StreamCodec.of((buffer,payload)->buffer.writeVarInt(payload.containerId),buffer->new SolderingControl(buffer.readVarInt()));
 @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
 public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(payload,context)->context.enqueueWork(()->{var player=context.player();if(player.containerMenu instanceof SolderingMenu menu&&menu.containerId==payload.containerId&&menu.stillValid(player)&&menu.machine instanceof SolderingBlockEntity machine)machine.toggleCollision();}));}
 public static void sparks(ServerLevel server,Vec3 point,BlockPos core){var packet=new WeldingParticlePacket(point.x,point.y,point.z,3,false);for(var player:server.players())if(player.distanceToSqr(core.getX(),core.getY(),core.getZ())<625)PacketDistributor.sendToPlayer(player,packet);}
}
