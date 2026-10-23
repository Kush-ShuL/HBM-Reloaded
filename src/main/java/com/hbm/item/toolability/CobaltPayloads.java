package com.hbm.item.toolability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class CobaltPayloads {
    public record Cycle() implements CustomPacketPayload{
        static final Type<Cycle> TYPE=new Type<>(ResourceLocation.parse("hbm:cobalt_ability_cycle"));
        static final StreamCodec<RegistryFriendlyByteBuf,Cycle> CODEC=StreamCodec.unit(new Cycle());
        @Override public Type<Cycle> type(){return TYPE;}
    }
    public record Configure(CompoundTag data)implements CustomPacketPayload{
        static final Type<Configure> TYPE=new Type<>(ResourceLocation.parse("hbm:cobalt_ability_configuration"));
        static final StreamCodec<RegistryFriendlyByteBuf,Configure> CODEC=StreamCodec.of((b,p)->b.writeNbt(p.data),b->new Configure(b.readNbt()));
        @Override public Type<Configure> type(){return TYPE;}
    }
    public record Status(int area,int level,int harvest,int harvestLevel)implements CustomPacketPayload{
        static final Type<Status> TYPE=new Type<>(ResourceLocation.parse("hbm:cobalt_ability_notice"));
        static final StreamCodec<RegistryFriendlyByteBuf,Status> CODEC=StreamCodec.of((b,p)->{b.writeByte(p.area);b.writeByte(p.level);b.writeByte(p.harvest);b.writeByte(p.harvestLevel);},b->new Status(b.readByte(),b.readByte(),b.readByte(),b.readByte()));
        @Override public Type<Status> type(){return TYPE;}
    }
    public static void register(RegisterPayloadHandlersEvent event){
        var registry=event.registrar("2");
        registry.playToServer(Cycle.TYPE,Cycle.CODEC,(p,c)->c.enqueueWork(()->{if(c.player() instanceof ServerPlayer player)CobaltInteractions.cycle(player);}));
        registry.playToServer(Configure.TYPE,Configure.CODEC,(p,c)->c.enqueueWork(()->{if(c.player() instanceof ServerPlayer player&&player.getMainHandItem().getItem() instanceof SourceAbilityTool&&p.data!=null)CobaltPresets.read(p.data,AvailableAbilities.of(player.getMainHandItem())).apply(player.getMainHandItem());}));
        registry.playToClient(Status.TYPE,Status.CODEC,(p,c)->c.enqueueWork(()->com.hbm.client.toolability.CobaltToolClient.notice(new CobaltPresets.Preset(p.area,p.level,p.harvest,p.harvestLevel))));
    }
    static void status(ServerPlayer player,CobaltPresets.Preset preset){PacketDistributor.sendToPlayer(player,new Status(preset.area(),preset.recursionLevel,preset.harvest,preset.harvestLevel));}
    private CobaltPayloads(){}
}
