package com.hbm.explosion;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Original float origin and signed-byte affected-block wire format, plus the original Amat discriminator. */
public record ExplosionVisuals(int effect,double x,double y,double z,float size,List<BlockPos> blocks) implements CustomPacketPayload {
    public static final int STANDARD=0,AMAT=1;
    public static final Type<ExplosionVisuals> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath("hbm","legacy_explosion_visuals"));
    public static final StreamCodec<RegistryFriendlyByteBuf,ExplosionVisuals> CODEC=StreamCodec.of((buffer,value)->{
        buffer.writeByte(value.effect);if(value.effect==AMAT){buffer.writeDouble(value.x);buffer.writeDouble(value.y);buffer.writeDouble(value.z);}else{buffer.writeFloat((float)value.x);buffer.writeFloat((float)value.y);buffer.writeFloat((float)value.z);}buffer.writeFloat(value.size);buffer.writeInt(value.blocks.size());
        for(BlockPos pos:value.blocks){buffer.writeByte(pos.getX()-(int)value.x);buffer.writeByte(pos.getY()-(int)value.y);buffer.writeByte(pos.getZ()-(int)value.z);}
    },buffer->{
        int effect=buffer.readUnsignedByte();double x=effect==AMAT?buffer.readDouble():buffer.readFloat(),y=effect==AMAT?buffer.readDouble():buffer.readFloat(),z=effect==AMAT?buffer.readDouble():buffer.readFloat();float size=buffer.readFloat();int count=buffer.readInt();
        if(count<0||count>buffer.readableBytes()/3)throw new IllegalArgumentException("Invalid explosion block list");
        var blocks=new ArrayList<BlockPos>(count);for(int i=0;i<count;i++)blocks.add(new BlockPos((int)x+buffer.readByte(),(int)y+buffer.readByte(),(int)z+buffer.readByte()));
        return new ExplosionVisuals(effect,x,y,z,size,List.copyOf(blocks));
    });
    @Override public Type<ExplosionVisuals> type(){return TYPE;}
    public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(TYPE,CODEC,(value,context)->context.enqueueWork(()->com.hbm.client.bomb.nuclear.LegacyExplosionEffects.receive(value)));}
    public static void send(Level level,int effect,double x,double y,double z,float size,List<BlockPos> blocks,double range){
        if(!(level instanceof ServerLevel server))return;
        var packet=new ExplosionVisuals(effect,x,y,z,size,List.copyOf(blocks));
        for(var player:server.players())if(player.distanceToSqr(x,y,z)<=range*range)PacketDistributor.sendToPlayer(player,packet);
    }
}
