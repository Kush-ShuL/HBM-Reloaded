package com.hbm.environment.impact;

import java.util.function.Consumer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** The original three PermaSync impact fields, every tick, scoped to the actual client level. */
public record ImpactSync(ResourceLocation dimension,float fire,float dust,boolean impact) implements CustomPacketPayload {
    public static final Type<ImpactSync> TYPE=new Type<>(ResourceLocation.parse("hbm:impact_environment"));
    public static final StreamCodec<RegistryFriendlyByteBuf,ImpactSync> CODEC=StreamCodec.of((b,p)->{b.writeResourceLocation(p.dimension);b.writeFloat(p.fire);b.writeFloat(p.dust);b.writeBoolean(p.impact);},b->new ImpactSync(b.readResourceLocation(),b.readFloat(),b.readFloat(),b.readBoolean()));
    private static Consumer<ImpactSync> client=ignored->{};
    public static void bindClient(Consumer<ImpactSync> receiver){client=receiver;}
    public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(TYPE,CODEC,(packet,context)->context.enqueueWork(()->client.accept(packet)));}
    public static void send(ServerPlayer player,ImpactState state){PacketDistributor.sendToPlayer(player,new ImpactSync(player.level().dimension().location(),state.fire(),state.dust(),state.impact()));}
    @Override public Type<ImpactSync> type(){return TYPE;}
}
