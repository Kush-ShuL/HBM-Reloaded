package com.hbm.entity.mob.duck;

import com.hbm.bomb.nuclear.NuclearMukeEffects;
import com.hbm.registry.HbmSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.*;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** Actual prank boss: 25x original chicken mesh, invulnerable, mountable and dismissed with peas. */
public final class Quackos extends SourceDuck {
    private final ServerBossEvent boss=new ServerBossEvent(getDisplayName(),BossEvent.BossBarColor.PURPLE,BossEvent.BossBarOverlay.PROGRESS);
    public Quackos(EntityType<? extends Quackos> type,Level level){super(type,level);}
    @Override protected SoundEvent getAmbientSound(){return HbmSounds.get("entity.megaquacc");}
    @Override protected SoundEvent getHurtSound(DamageSource source){return HbmSounds.get("entity.megaquacc");}
    @Override protected SoundEvent getDeathSound(){return HbmSounds.get("entity.megaquacc");}
    @Override public Quackos getBreedOffspring(ServerLevel level,AgeableMob other){return DuckContent.QUACKOS.get().create(level);}
    @Override public boolean isInvulnerable(){return true;}
    @Override public boolean isInvulnerableTo(DamageSource source){return true;}
    @Override public void setHealth(float health){super.setHealth(getMaxHealth());}
    @Override public void die(DamageSource source){}
    @Override public void remove(RemovalReason reason){
        if(!level().isClientSide&&(reason==RemovalReason.KILLED||reason==RemovalReason.DISCARDED))return;
        if(boss!=null)boss.removeAllPlayers();super.remove(reason);
    }
    @Override public boolean canBeLeashed(){return false;}
    @Override public void startSeenByPlayer(ServerPlayer player){super.startSeenByPlayer(player);boss.addPlayer(player);}
    @Override public void stopSeenByPlayer(ServerPlayer player){super.stopSeenByPlayer(player);boss.removePlayer(player);}
    @Override public void setCustomName(Component name){super.setCustomName(name);if(boss!=null)boss.setName(getDisplayName());}
    @Override public InteractionResult mobInteract(Player player,InteractionHand hand){
        var result=super.mobInteract(player,hand);if(result.consumesAction())return result;
        if(!level().isClientSide&&(getFirstPassenger()==null||getFirstPassenger()==player)){player.startRiding(this,true);return InteractionResult.CONSUME;}
        return InteractionResult.PASS;
    }
    @Override protected void positionRider(Entity passenger,MoveFunction move){
        super.positionRider(passenger,move);
        if(!hasPassenger(passenger))return;
        float radians=yBodyRot*(float)Math.PI/180F;
        move.accept(passenger,getX()+.1F*Mth.sin(radians),getY()+getBbHeight()-.125F,getZ()-.1F*Mth.cos(radians));
        if(passenger instanceof LivingEntity living)living.yBodyRot=yBodyRot;
    }
    @Override public void aiStep(){
        super.aiStep();
        if(!level().isClientSide&&getY()<level().getMinBuildHeight()-30){setPos(getX()+random.nextGaussian()*30,level().getMaxBuildHeight(),getZ()+random.nextGaussian()*30);}
        boss.setProgress(1);
    }
    public void sourceDespawn(){
        if(level() instanceof ServerLevel server){
            for(int i=0;i<150;i++){
                var packet=new NuclearMukeEffects(getX()+random.nextDouble()*20-10,getY()+random.nextDouble()*25,getZ()+random.nextDouble()*20-10,"bf",true);
                for(var player:server.players())if(player.distanceToSqr(position())<=150D*150D)net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player,packet);
            }
            spawnAtLocation(new net.minecraft.world.item.ItemStack(DuckContent.SPAWNER.get(),3));
        }
        boss.removeAllPlayers();super.remove(RemovalReason.DISCARDED);
    }
}
