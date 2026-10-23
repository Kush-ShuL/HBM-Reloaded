package com.hbm.machine.reactor.rbmk.crane;
import java.util.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Original five per-player held-key states, independent of weapon controls or a machine menu. */
public final class RbmkCraneInput {
    private static final Map<Player,Integer> KEYS=new WeakHashMap<>();public static int get(Player player){return KEYS.getOrDefault(player,0);}public static void set(Player player,int bits){if(bits==0)KEYS.remove(player);else KEYS.put(player,bits&31);}
    public record Keys(int bits)implements CustomPacketPayload{public static final Type<Keys> TYPE=new Type<>(ResourceLocation.parse("hbm:rbmk_crane_keys"));public static final StreamCodec<RegistryFriendlyByteBuf,Keys> CODEC=StreamCodec.of((b,p)->b.writeByte(p.bits),b->new Keys(b.readUnsignedByte()));@Override public Type<? extends CustomPacketPayload> type(){return TYPE;}}
    public static void register(IEventBus bus){bus.addListener((RegisterPayloadHandlersEvent event)->event.registrar("1").playToServer(Keys.TYPE,Keys.CODEC,(packet,context)->context.enqueueWork(()->set(context.player(),packet.bits))));}private RbmkCraneInput(){}
}
