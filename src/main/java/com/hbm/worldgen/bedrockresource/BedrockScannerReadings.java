package com.hbm.worldgen.bedrockresource;

import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Original seven keyed PlayerInform notices; actual existing inbox owns their four-second lifetime/HUD. */
@EventBusSubscriber(modid="hbm",bus=EventBusSubscriber.Bus.MOD)
public record BedrockScannerReadings(double[] levels) implements CustomPacketPayload {
    public BedrockScannerReadings{if(levels.length!=BedrockOreType.values().length)throw new IllegalArgumentException("Expected six source densities");levels=levels.clone();}
    public static final Type<BedrockScannerReadings> TYPE=new Type<>(ResourceLocation.parse("hbm:bedrock_scanner_readings"));
    public static final StreamCodec<RegistryFriendlyByteBuf,BedrockScannerReadings> CODEC=StreamCodec.of((buffer,packet)->{for(double level:packet.levels)buffer.writeDouble(level);},buffer->{double[] values=new double[BedrockOreType.values().length];for(int i=0;i<values.length;i++)values[i]=buffer.readDouble();return new BedrockScannerReadings(values);});
    @Override public Type<BedrockScannerReadings> type(){return TYPE;}
    @SubscribeEvent public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(TYPE,CODEC,(packet,context)->context.enqueueWork(packet::show));}
    private void show(){
        for(var type:BedrockOreType.values()){
            double amount=levels[type.ordinal()];
            var text=Component.translatable("item.bedrock_ore.type."+type.suffix+".name").append(": "+BedrockDensity.displayed(amount)+" (").append(Component.translatable(BedrockDensity.translateDensity(amount)).withStyle(BedrockDensity.getColor(amount))).append(Component.literal(")").withStyle(ChatFormatting.RESET));
            com.hbm.item.settings.client.LegacyInfoSystem.push(text,4000,777+type.ordinal());
        }
        double average=BedrockDensity.average(levels);var fluid=BedrockDensity.getBoreFluid(average);
        var text=Component.translatable("hud.hbm.bedrock_resource.tier",BedrockDensity.getTier(average)).withStyle(ChatFormatting.YELLOW);
        if(fluid!=null)text.append(" - "+fluid.fill+"mB ").append(fluid.type.displayName());
        com.hbm.item.settings.client.LegacyInfoSystem.push(text,4000,777+BedrockOreType.values().length);
    }
    public static void send(ServerPlayer player,double[] values){PacketDistributor.sendToPlayer(player,new BedrockScannerReadings(values));}
}
