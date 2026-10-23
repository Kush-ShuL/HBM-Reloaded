package com.hbm.machine.custommachine;
import com.google.gson.JsonParser;
import java.util.function.Consumer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Definition topology is server-authoritative; recipe contents use the sole native RecipeManager sync. */
public final class CustomMachineNetwork {
    public static Consumer<String> CLIENT=json->{};
    public record Definitions(String json)implements CustomPacketPayload {
        public static final Type<Definitions> TYPE=new Type<>(ResourceLocation.parse("hbm:custom_machine_definitions"));
        public static final StreamCodec<RegistryFriendlyByteBuf,Definitions> CODEC=StreamCodec.of((b,p)->b.writeUtf(p.json,1048576),b->new Definitions(b.readUtf(1048576)));
        @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    }
    static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(Definitions.TYPE,Definitions.CODEC,(packet,context)->context.enqueueWork(()->CLIENT.accept(packet.json)));}
    public static void send(ServerPlayer player){PacketDistributor.sendToPlayer(player,new Definitions(CustomMachinesConfig.snapshot().toString()));}
    private CustomMachineNetwork(){}
}
