package com.hbm.weapon.sedna.gun.legendary;

import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Original CasingCreator packet/offset overloads. Cosmetic packets never choose damage or consume ammo. */
@EventBusSubscriber(modid="hbm",bus=EventBusSubscriber.Bus.MOD)
public final class CasingEffects {
    public static Consumer<Packet> client=ignored->{};
    public record Packet(double x,double y,double z,CompoundTag data) implements CustomPacketPayload {
        public static final Type<Packet> TYPE=new Type<>(ResourceLocation.parse("hbm:sedna_spent_casing"));
        public static final StreamCodec<RegistryFriendlyByteBuf,Packet> CODEC=StreamCodec.of((out,p)->{out.writeDouble(p.x);out.writeDouble(p.y);out.writeDouble(p.z);out.writeNbt(p.data);},in->new Packet(in.readDouble(),in.readDouble(),in.readDouble(),in.readNbt()));
        @Override public Type<Packet> type(){return TYPE;}
    }
    @SubscribeEvent public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(Packet.TYPE,Packet.CODEC,(packet,context)->context.enqueueWork(()->client.accept(packet)));}
    public static void composeEffect(Level level,LivingEntity entity,double frontOffset,double heightOffset,double sideOffset,double frontMotion,double heightMotion,double sideMotion,double variance,String casing){composeEffect(level,entity,frontOffset,heightOffset,sideOffset,frontMotion,heightMotion,sideMotion,variance,5F,10F,casing,false,0,0,0);}
    public static void composeEffect(Level level,LivingEntity entity,double frontOffset,double heightOffset,double sideOffset,double frontMotion,double heightMotion,double sideMotion,double variance,float pitch,float yaw,String casing){composeEffect(level,entity,frontOffset,heightOffset,sideOffset,frontMotion,heightMotion,sideMotion,variance,pitch,yaw,casing,false,0,0,0);}
    // The original convenience overload ignores its smoke arguments; keep that source behavior.
    public static void composeEffect(Level level,LivingEntity entity,double frontOffset,double heightOffset,double sideOffset,double frontMotion,double heightMotion,double sideMotion,double variance,String casing,boolean smoke,int life,double lift,int nodeLife){composeEffect(level,entity,frontOffset,heightOffset,sideOffset,frontMotion,heightMotion,sideMotion,variance,5F,10F,casing,false,0,0,0);}
    public static void composeEffect(Level level,LivingEntity entity,double frontOffset,double heightOffset,double sideOffset,double frontMotion,double heightMotion,double sideMotion,double variance,float pitchMomentum,float yawMomentum,String casing,boolean smoke,int life,double lift,int nodeLife){
        if(entity.isShiftKeyDown())heightOffset-=.075F;
        var offset=new Vector(sideOffset,heightOffset,frontOffset).rotate(entity.getXRot(),entity.getYRot());
        var motion=new Vector(sideMotion,heightMotion,frontMotion).rotate(entity.getXRot(),entity.getYRot());
        double x=entity.getX()+offset.x,y=entity.getY()+entity.getEyeHeight()+offset.y,z=entity.getZ()+offset.z;
        double mx=entity.getDeltaMovement().x+motion.x+entity.getRandom().nextGaussian()*variance,my=entity.getDeltaMovement().y+motion.y+entity.getRandom().nextGaussian()*variance,mz=entity.getDeltaMovement().z+motion.z+entity.getRandom().nextGaussian()*variance;
        if(entity instanceof Player player&&player.getAbilities().flying)my-=.04D;
        send(level,x,y,z,mx,my,mz,entity.getYRot(),entity.getXRot(),pitchMomentum,yawMomentum,casing,smoke,life,lift,nodeLife);
    }
    public static void composeEffect(Level level,double x,double y,double z,float yaw,float pitch,double frontMotion,double heightMotion,double sideMotion,double variance,float pitchMomentum,float yawMomentum,String casing,boolean smoke,int life,double lift,int nodeLife){
        var motion=new Vector(sideMotion,heightMotion,frontMotion).rotate(pitch,yaw);
        send(level,x,y,z,motion.x+level.random.nextGaussian()*variance,motion.y+level.random.nextGaussian()*variance,motion.z+level.random.nextGaussian()*variance,yaw,pitch,pitchMomentum,yawMomentum,casing,smoke,life,lift,nodeLife);
    }
    private static void send(Level level,double x,double y,double z,double mx,double my,double mz,float yaw,float pitch,float pitchMomentum,float yawMomentum,String casing,boolean smoke,int life,double lift,int nodeLife){
        var data=new CompoundTag();data.putDouble("mX",mx);data.putDouble("mY",my);data.putDouble("mZ",mz);data.putFloat("yaw",yaw);data.putFloat("pitch",pitch);data.putFloat("mPitch",pitchMomentum);data.putFloat("mYaw",yawMomentum);data.putString("name",casing);data.putBoolean("smoking",smoke);data.putInt("smokeLife",life);data.putDouble("smokeLift",lift);data.putInt("nodeLife",nodeLife);
        if(level instanceof ServerLevel server){var packet=new Packet(x,y,z,data);for(var player:server.players())if(player.distanceToSqr(x,y,z)<=2500)PacketDistributor.sendToPlayer(player,packet);}
    }
    private static final class Vector {
        double x,y,z;Vector(double x,double y,double z){this.x=x;this.y=y;this.z=z;}
        Vector rotate(float pitch,float yaw){float angle=-pitch/180F*(float)Math.PI,c=Mth.cos(angle),s=Mth.sin(angle);double py=y*c+z*s,pz=z*c-y*s;y=py;z=pz;angle=-yaw/180F*(float)Math.PI;c=Mth.cos(angle);s=Mth.sin(angle);double px=x*c+z*s;pz=z*c-x*s;x=px;z=pz;return this;}
    }
    private CasingEffects(){}
}
