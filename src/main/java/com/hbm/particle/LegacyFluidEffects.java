package com.hbm.particle;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Typed source tower/gasfire particle payloads shared by real fluid machines and gas clouds. */
public final class LegacyFluidEffects {
    public record Tower(float lift,float base,float maximum,int life,boolean noWind,Float strafe,Float alpha,Integer color){
        public Tower(float lift,float base,float maximum,int life){this(lift,base,maximum,life,false,null,null,null);}
        public Tower color(int color){return new Tower(lift,base,maximum,life,noWind,strafe,alpha,color);}
        public Tower withoutWind(){return new Tower(lift,base,maximum,life,true,strafe,alpha,color);}
        public Tower strafe(float strafe){return new Tower(lift,base,maximum,life,noWind,strafe,alpha,color);}
        public Tower alpha(float alpha){return new Tower(lift,base,maximum,life,noWind,strafe,alpha,color);}
    }
    public record Packet(boolean gasfire,double x,double y,double z,double mx,double my,double mz,float scale,Tower tower)implements CustomPacketPayload{
        public static final Type<Packet> TYPE=new Type<>(ResourceLocation.parse("hbm:legacy_fluid_effects"));
        public static final StreamCodec<RegistryFriendlyByteBuf,Packet> CODEC=StreamCodec.of((b,p)->{b.writeBoolean(p.gasfire);b.writeDouble(p.x);b.writeDouble(p.y);b.writeDouble(p.z);if(p.gasfire){b.writeDouble(p.mx);b.writeDouble(p.my);b.writeDouble(p.mz);b.writeFloat(p.scale);}else{var t=p.tower;b.writeFloat(t.lift);b.writeFloat(t.base);b.writeFloat(t.maximum);b.writeInt(t.life);b.writeBoolean(t.noWind);b.writeBoolean(t.strafe!=null);if(t.strafe!=null)b.writeFloat(t.strafe);b.writeBoolean(t.alpha!=null);if(t.alpha!=null)b.writeFloat(t.alpha);b.writeBoolean(t.color!=null);if(t.color!=null)b.writeInt(t.color);}},b->{boolean gas=b.readBoolean();double x=b.readDouble(),y=b.readDouble(),z=b.readDouble();if(gas)return new Packet(true,x,y,z,b.readDouble(),b.readDouble(),b.readDouble(),b.readFloat(),null);float lift=b.readFloat(),base=b.readFloat(),max=b.readFloat();int life=b.readInt();boolean noWind=b.readBoolean();Float strafe=b.readBoolean()?b.readFloat():null,alpha=b.readBoolean()?b.readFloat():null;Integer color=b.readBoolean()?b.readInt():null;return new Packet(false,x,y,z,0,0,0,0,new Tower(lift,base,max,life,noWind,strafe,alpha,color));});
        @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    }
    public static void register(IEventBus bus){bus.addListener(LegacyFluidEffects::payloads);}
    private static void payloads(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(Packet.TYPE,Packet.CODEC,(packet,context)->context.enqueueWork(()->com.hbm.client.particle.LegacyFluidEffectsClient.receive(packet)));}
    public static void tower(Level level,double x,double y,double z,Tower tower){send(level,new Packet(false,x,y,z,0,0,0,0,tower));}
    public static void gasFlame(Level level,double x,double y,double z,double mx,double my,double mz){gasFlame(level,x,y,z,mx,my,mz,6.5F);}
    public static void gasFlame(Level level,double x,double y,double z,double mx,double my,double mz,float scale){send(level,new Packet(true,x,y,z,mx,my,mz,scale>0?scale:6.5F,null));}
    private static void send(Level level,Packet packet){if(level instanceof ServerLevel server){try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginPayloadGroup(server.getServer(),packet)){for(var player:server.players())if(player.distanceToSqr(packet.x,packet.y,packet.z)<22500)PacketDistributor.sendToPlayer(player,packet);dispatchScope.commit();}}else com.hbm.client.particle.LegacyFluidEffectsClient.receive(packet);}
    private LegacyFluidEffects(){}
}
