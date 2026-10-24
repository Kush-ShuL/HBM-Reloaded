package com.hbm.weapon.sedna.gun;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionHand;
import java.util.UUID;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Input contains only key edges/held identity. Damage, ammo, timings, spread and projectile creation remain authoritative server state. */
@EventBusSubscriber(modid="hbm",bus=EventBusSubscriber.Bus.MOD)
public final class GunNetwork {
    public record Input(int slot,InteractionHand hand,ResourceLocation item,UUID instance,GunKey key,boolean state) implements CustomPacketPayload {
        public static final Type<Input> TYPE=new Type<>(ResourceLocation.parse("hbm:sedna_gun_input"));
        public static final StreamCodec<RegistryFriendlyByteBuf,Input> CODEC=StreamCodec.of((out,p)->{out.writeVarInt(p.slot);out.writeEnum(p.hand);out.writeResourceLocation(p.item);out.writeUUID(p.instance);out.writeEnum(p.key);out.writeBoolean(p.state);},in->new Input(in.readVarInt(),in.readEnum(InteractionHand.class),in.readResourceLocation(),in.readUUID(),in.readEnum(GunKey.class),in.readBoolean()));
        @Override public Type<Input> type(){return TYPE;}
    }
    public record Animation(int slot,InteractionHand hand,ResourceLocation item,UUID instance,GunAnimation animation,int receiver,int config) implements CustomPacketPayload {
        public static final Type<Animation> TYPE=new Type<>(ResourceLocation.parse("hbm:sedna_gun_animation"));
        public static final StreamCodec<RegistryFriendlyByteBuf,Animation> CODEC=StreamCodec.of((out,p)->{out.writeVarInt(p.slot);out.writeEnum(p.hand);out.writeResourceLocation(p.item);out.writeUUID(p.instance);out.writeEnum(p.animation);out.writeVarInt(p.receiver);out.writeVarInt(p.config);},in->new Animation(in.readVarInt(),in.readEnum(InteractionHand.class),in.readResourceLocation(),in.readUUID(),in.readEnum(GunAnimation.class),in.readVarInt(),in.readVarInt()));
        @Override public Type<Animation> type(){return TYPE;}
    }
    public record Flash(int entityId,InteractionHand hand) implements CustomPacketPayload {
        public static final Type<Flash> TYPE=new Type<>(ResourceLocation.parse("hbm:sedna_muzzle_flash"));
        public static final StreamCodec<RegistryFriendlyByteBuf,Flash> CODEC=StreamCodec.of((out,p)->{out.writeVarInt(p.entityId);out.writeEnum(p.hand);},in->new Flash(in.readVarInt(),in.readEnum(InteractionHand.class)));
        @Override public Type<Flash> type(){return TYPE;}
    }
    public record BlackPowder(double x,double y,double z,double hx,double hy,double hz,int clouds,float scale,float cloudSpeed,int sparks,float sparkSpeed) implements CustomPacketPayload {
        public static final Type<BlackPowder> TYPE=new Type<>(ResourceLocation.parse("hbm:sedna_black_powder"));
        public static final StreamCodec<RegistryFriendlyByteBuf,BlackPowder> CODEC=StreamCodec.of((out,p)->{out.writeDouble(p.x);out.writeDouble(p.y);out.writeDouble(p.z);out.writeDouble(p.hx);out.writeDouble(p.hy);out.writeDouble(p.hz);out.writeInt(p.clouds);out.writeFloat(p.scale);out.writeFloat(p.cloudSpeed);out.writeInt(p.sparks);out.writeFloat(p.sparkSpeed);},in->new BlackPowder(in.readDouble(),in.readDouble(),in.readDouble(),in.readDouble(),in.readDouble(),in.readDouble(),in.readInt(),in.readFloat(),in.readFloat(),in.readInt(),in.readFloat()));
        @Override public Type<BlackPowder> type(){return TYPE;}
    }
    @SubscribeEvent public static void register(RegisterPayloadHandlersEvent event){
        var registrar=event.registrar("2");
        registrar.playToServer(Input.TYPE,Input.CODEC,(packet,ctx)->ctx.enqueueWork(()->{
            if(!(ctx.player() instanceof ServerPlayer player)||!player.isAlive()||player.isSpectator()||packet.slot<0||packet.slot>8||player.getInventory().selected!=packet.slot||!GunSettings.enabled())return;
            GunHands.prepare(player);
            var stack=player.getItemInHand(packet.hand);if(!(stack.getItem() instanceof ItemGunBaseNT gun)||!GunHands.operable(player,packet.hand)||!BuiltInRegistries.ITEM.getKey(stack.getItem()).equals(packet.item)||!packet.instance.equals(GunHands.identity(stack)))return;
            if(GunHands.dual(player)&&(packet.key==GunKey.SECONDARY||packet.key==GunKey.TERTIARY)){if(ItemGunBaseNT.getIsAiming(stack))ItemGunBaseNT.setIsAiming(stack,false);return;}
            gun.handleKeybind(player,new GunInventory(player),stack,packet.key,packet.state);
        }));
        registrar.playToClient(Animation.TYPE,Animation.CODEC,(packet,ctx)->ctx.enqueueWork(()->{
            var client=ItemGunBaseNT.CLIENT;if(client==null)return;var player=client.localPlayer();if(player==null||player.getInventory().selected!=packet.slot)return;
            var stack=player.getItemInHand(packet.hand);if(!(stack.getItem() instanceof ItemGunBaseNT gun)||!BuiltInRegistries.ITEM.getKey(stack.getItem()).equals(packet.item)||!packet.instance.equals(GunHands.identity(stack))||packet.config<0||packet.config>=gun.getConfigCount())return;
            client.animation(stack,packet.slot,packet.hand,packet.animation,packet.receiver,packet.config);
        }));
        registrar.playToClient(Flash.TYPE,Flash.CODEC,(packet,ctx)->ctx.enqueueWork(()->{if(ItemGunBaseNT.CLIENT!=null)ItemGunBaseNT.CLIENT.muzzleFlash(packet.entityId,packet.hand);}));
        registrar.playToClient(BlackPowder.TYPE,BlackPowder.CODEC,(packet,ctx)->ctx.enqueueWork(()->{if(ItemGunBaseNT.CLIENT!=null)ItemGunBaseNT.CLIENT.blackPowder(packet);}));
    }
    public static void animation(ServerPlayer player,ItemStack stack,GunAnimation type,int receiver,int config){if(GunHands.identity(stack)==null){GunHands.ensureIdentity(stack);player.inventoryMenu.broadcastChanges();}PacketDistributor.sendToPlayer(player,new Animation(player.getInventory().selected,GunHands.hand(player,stack),BuiltInRegistries.ITEM.getKey(stack.getItem()),GunHands.ensureIdentity(stack),type,receiver,config));}
    public static void flash(LivingEntity entity){if(entity.level() instanceof ServerLevel level)for(var player:level.players())if(player.distanceToSqr(entity.getX(),entity.getY(),entity.getZ())<=100D*100D)PacketDistributor.sendToPlayer(player,new Flash(entity.getId(),GunHands.hand(entity,GunHands.activeStack())));}
    public static void blackPowder(Level level,double x,double y,double z,double hx,double hy,double hz,int clouds,float scale,float cloudSpeed,int sparks,float sparkSpeed){if(level instanceof ServerLevel server){var packet=new BlackPowder(x,y,z,hx,hy,hz,clouds,scale,cloudSpeed,sparks,sparkSpeed);try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginPayloadGroup(server.getServer(),packet)){for(var player:server.players())if(player.distanceToSqr(x,y,z)<=200D*200D)PacketDistributor.sendToPlayer(player,packet);dispatchScope.commit();}}}
    private GunNetwork(){}
}
