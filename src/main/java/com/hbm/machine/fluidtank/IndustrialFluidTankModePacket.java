package com.hbm.machine.fluidtank;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Original mode button, authorized through the actual open container. */
public record IndustrialFluidTankModePacket(int containerId) implements CustomPacketPayload {
    public static final Type<IndustrialFluidTankModePacket> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath("hbm","industrial_fluid_tank_mode"));
    public static final StreamCodec<RegistryFriendlyByteBuf,IndustrialFluidTankModePacket> CODEC=StreamCodec.of((buf,payload)->buf.writeVarInt(payload.containerId),buf->new IndustrialFluidTankModePacket(buf.readVarInt()));
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(payload,context)->context.enqueueWork(()->{var player=context.player();if(player.containerMenu instanceof IndustrialFluidTankMenu menu&&menu.containerId==payload.containerId&&menu.stillValid(player))menu.cycleMode();}));}
}
