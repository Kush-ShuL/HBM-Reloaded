package com.hbm.machine.mininglaser;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** The one real AuxButton toggle, validated against the actual open menu and its distance. */
public record MiningLaserControl(int containerId) implements CustomPacketPayload {
    public static final Type<MiningLaserControl> TYPE=new Type<>(ResourceLocation.parse("hbm:mining_laser_control"));
    public static final StreamCodec<RegistryFriendlyByteBuf,MiningLaserControl> CODEC=StreamCodec.of((buf,p)->buf.writeVarInt(p.containerId),buf->new MiningLaserControl(buf.readVarInt()));
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(packet,context)->context.enqueueWork(()->{var player=context.player();if(player.containerMenu instanceof MiningLaserMenu menu&&menu.containerId==packet.containerId&&menu.stillValid(player)&&player.level().getBlockEntity(menu.position) instanceof MiningLaserEntity machine){machine.isOn=!machine.isOn;machine.setChanged();}}));}
}
