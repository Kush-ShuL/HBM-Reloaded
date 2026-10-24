package com.hbm.machine.woodprocessing;

import com.hbm.damage.HbmDamageSources;
import java.util.function.Consumer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Real source turbofan100 damage and original vanillaburst blockdust, not a modern generic terrain burst. */
public record WoodEffects(double x,double y,double z,int count) implements CustomPacketPayload {
    public static Consumer<WoodEffects> CLIENT=packet->{};
    public static final Type<WoodEffects> TYPE=new Type<>(ResourceLocation.parse("hbm:wood_cut_blood"));
    public static final StreamCodec<RegistryFriendlyByteBuf,WoodEffects> CODEC=StreamCodec.of((b,p)->{b.writeDouble(p.x);b.writeDouble(p.y);b.writeDouble(p.z);b.writeVarInt(p.count);},b->new WoodEffects(b.readDouble(),b.readDouble(),b.readDouble(),b.readVarInt()));
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(TYPE,CODEC,(packet,context)->context.enqueueWork(()->CLIENT.accept(packet)));}
    static void cut(ServerLevel level,AABB box){cut(level,box,entity->{});}
    public static void cut(ServerLevel level,AABB box,Consumer<LivingEntity> afterDamage){for(var entity:level.getEntitiesOfClass(LivingEntity.class,box))if(entity.isAlive()&&entity.hurt(HbmDamageSources.source(level,HbmDamageSources.turbofan),100)){
        afterDamage.accept(entity); // Source Thresher alone awards nitra after an actual successful fatal hit.
        level.playSound(null,entity.getX(),entity.getY(),entity.getZ(),SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR,SoundSource.HOSTILE,2,.95F+level.random.nextFloat()*.2F);
        int count=Math.min((int)Math.ceil(entity.getMaxHealth()/4),250)*4;
        var packet=new WoodEffects(entity.getX(),entity.getY()+entity.getBbHeight()*.5,entity.getZ(),count);
        try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginPayloadGroup(level.getServer(),packet)){for(var player:level.players())if(player.distanceToSqr(entity.getX(),entity.getY(),entity.getZ())<2500)PacketDistributor.sendToPlayer(player,packet);dispatchScope.commit();}
    }}
}
