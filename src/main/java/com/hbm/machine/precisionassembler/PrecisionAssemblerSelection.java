package com.hbm.machine.precisionassembler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Source index+selection control; only an open, usable original assembler menu authorizes mutation. */
public record PrecisionAssemblerSelection(int containerId,int index,String selection) implements CustomPacketPayload {
    public static final Type<PrecisionAssemblerSelection> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath("hbm","precision_assembly_selection"));
    public static final StreamCodec<RegistryFriendlyByteBuf,PrecisionAssemblerSelection> CODEC=StreamCodec.of((b,p)->{b.writeVarInt(p.containerId);b.writeVarInt(p.index);b.writeUtf(p.selection);},b->new PrecisionAssemblerSelection(b.readVarInt(),b.readVarInt(),b.readUtf()));
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(payload,context)->context.enqueueWork(()->{var player=context.player();if(player.containerMenu instanceof PrecisionAssemblerMenu menu&&menu.containerId==payload.containerId&&menu.stillValid(player))menu.selectRecipe(payload.index,payload.selection);}));}
}
