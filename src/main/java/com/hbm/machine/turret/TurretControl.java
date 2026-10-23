package com.hbm.machine.turret;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** The old AuxButton and whitelist control paths, accepted only for the actual open turret inventory. */
public record TurretControl(BlockPos position,int action,int value,String name) implements CustomPacketPayload {
    public static final Type<TurretControl> TYPE=new Type<>(ResourceLocation.parse("hbm:turret_control"));
    public static final StreamCodec<RegistryFriendlyByteBuf,TurretControl> CODEC=StreamCodec.of((b,p)->{b.writeBlockPos(p.position);b.writeInt(p.action);b.writeInt(p.value);b.writeUtf(p.name,25);},b->new TurretControl(b.readBlockPos(),b.readInt(),b.readInt(),b.readUtf(25)));
    @Override public Type<TurretControl> type(){return TYPE;}
    public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(p,c)->c.enqueueWork(()->{var player=c.player();if(player.containerMenu instanceof TurretMenu menu&&menu.position.equals(p.position)&&menu.stillValid(player)&&player.level().hasChunkAt(p.position)&&player.level().getBlockEntity(p.position) instanceof TurretBaseEntity sentry&&!(sentry instanceof SentryEntity normal&&normal.damaged())){switch(p.action){case 0->sentry.control(p.value);case 1->{if(!p.name.isEmpty())sentry.addName(p.name);}case 2->sentry.removeName(p.value);default->{}}}}));}
}
