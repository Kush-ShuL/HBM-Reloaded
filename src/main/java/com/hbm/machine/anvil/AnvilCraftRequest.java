package com.hbm.machine.anvil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Native recipe IDs replace unstable legacy list indices; execution remains bound to the open server AnvilMenu. */
public record AnvilCraftRequest(int menu,ResourceLocation recipe,boolean maximum) implements CustomPacketPayload{
    public static final Type<AnvilCraftRequest> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath("hbm","anvil_craft"));
    public static final StreamCodec<RegistryFriendlyByteBuf,AnvilCraftRequest> CODEC=StreamCodec.of((buf,value)->{buf.writeVarInt(value.menu);buf.writeResourceLocation(value.recipe);buf.writeBoolean(value.maximum);},buf->new AnvilCraftRequest(buf.readVarInt(),buf.readResourceLocation(),buf.readBoolean()));
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(payload,context)->context.enqueueWork(()->{if(context.player().containerMenu instanceof AnvilMenu menu&&menu.containerId==payload.menu())menu.craft(payload.recipe(),payload.maximum());}));}
}
