package com.hbm.bomb.conventional;

import com.hbm.interfaces.IBomb.BombReturnCode;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Original laser PlayerInformPacket, keyed as ID_DETONATOR=8 with the default one-second lifetime. */
@EventBusSubscriber(modid="hbm",bus=EventBusSubscriber.Bus.MOD)
public record DetonatorNotice(int result) implements CustomPacketPayload {
    public static final Type<DetonatorNotice> TYPE=new Type<>(ResourceLocation.parse("hbm:detonator_notice"));
    public static final StreamCodec<RegistryFriendlyByteBuf,DetonatorNotice> CODEC=StreamCodec.of((b,p)->b.writeVarInt(p.result),b->new DetonatorNotice(b.readVarInt()));
    @Override public Type<DetonatorNotice> type(){return TYPE;}
    @SubscribeEvent public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(TYPE,CODEC,(p,c)->c.enqueueWork(()->{
        var codes=BombReturnCode.values();if(p.result<0||p.result>=codes.length)return;var code=codes[p.result];
        com.hbm.item.settings.client.LegacyInfoSystem.push(Component.translatable(code.getUnlocalizedMessage()).withStyle(code.wasSuccessful()?ChatFormatting.YELLOW:ChatFormatting.RED),8);
    }));}
    public static void send(Player player,BombReturnCode result){if(player instanceof ServerPlayer server)PacketDistributor.sendToPlayer(server,new DetonatorNotice(result.ordinal()));}
}
