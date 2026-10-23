package com.hbm.machine.pneumaticstorage;

import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Source control/cache deltas, authorized against the player's actual open menu and range. */
public final class PneumaticStoragePackets {
    public record Control(int menu,BlockPos pos,String key) implements CustomPacketPayload {
        public static final Type<Control> TYPE=new Type<>(PneumaticStorageContent.id("pneumatic_storage_control"));
        public static final StreamCodec<RegistryFriendlyByteBuf,Control> CODEC=StreamCodec.of((b,p)->{b.writeVarInt(p.menu);b.writeBlockPos(p.pos);b.writeUtf(p.key,16);},b->new Control(b.readVarInt(),b.readBlockPos(),b.readUtf(16)));
        @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    }
    public record Click(int menu,long hash,int click) implements CustomPacketPayload {
        public static final Type<Click> TYPE=new Type<>(PneumaticStorageContent.id("pneumatic_storage_click"));
        public static final StreamCodec<RegistryFriendlyByteBuf,Click> CODEC=StreamCodec.of((b,p)->{b.writeVarInt(p.menu);b.writeLong(p.hash);b.writeByte(p.click);},b->new Click(b.readVarInt(),b.readLong(),b.readUnsignedByte()));
        @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    }
    public record DeltaLine(long hash,long amount,ItemStack identity){}
    public record Deltas(int menu,List<DeltaLine> lines) implements CustomPacketPayload {
        public static final Type<Deltas> TYPE=new Type<>(PneumaticStorageContent.id("pneumatic_storage_deltas"));
        public static final StreamCodec<RegistryFriendlyByteBuf,Deltas> CODEC=StreamCodec.of((b,p)->{b.writeVarInt(p.menu);b.writeVarInt(p.lines.size());for(var line:p.lines){b.writeLong(line.hash);b.writeLong(line.amount);ItemStack.OPTIONAL_STREAM_CODEC.encode(b,line.identity);}},b->{int menu=b.readVarInt(),size=b.readVarInt();if(size<0||size>48)throw new IllegalArgumentException("Source pneumatic delta batch >48");var lines=new ArrayList<DeltaLine>(size);for(int i=0;i<size;i++)lines.add(new DeltaLine(b.readLong(),b.readLong(),ItemStack.OPTIONAL_STREAM_CODEC.decode(b)));return new Deltas(menu,List.copyOf(lines));});
        @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    }
    static void register(RegisterPayloadHandlersEvent event){var registry=event.registrar("1");registry.playToServer(Control.TYPE,Control.CODEC,(p,context)->context.enqueueWork(()->{var player=context.player();if(player.containerMenu instanceof PneumaticStorageMenu menu&&menu.containerId==p.menu&&menu.position().equals(p.pos)&&menu.stillValid(player)){if(menu.machine instanceof PneumaticStorageBase storage&&p.key.equals("pressure"))storage.cyclePressure();if(menu.machine instanceof PneumaticExporter exporter)exporter.toggle(p.key);}}));registry.playToServer(Click.TYPE,Click.CODEC,(p,context)->context.enqueueWork(()->{if(context.player().containerMenu instanceof PneumaticAccessMenu menu&&menu.containerId==p.menu&&menu.stillValid(context.player()))menu.transfer(p.hash,p.click);}));registry.playToClient(Deltas.TYPE,Deltas.CODEC,(p,context)->context.enqueueWork(()->{if(context.player().containerMenu instanceof PneumaticAccessMenu menu&&menu.containerId==p.menu)menu.receive(p.lines);}));}
    private PneumaticStoragePackets(){}
}
