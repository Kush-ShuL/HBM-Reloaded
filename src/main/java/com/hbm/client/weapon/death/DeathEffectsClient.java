package com.hbm.client.weapon.death;

import com.hbm.weapon.sedna.death.DeathParticlePacket;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLivingEvent;

/** Source entity visibility lasts two wall-clock seconds, independently of particle lifetime. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class DeathEffectsClient {
    private static final Map<Integer,Long> VANISHED=new HashMap<>();
    private DeathEffectsClient(){}
    @SubscribeEvent(priority=EventPriority.HIGHEST)public static void hideCorpse(RenderLivingEvent.Pre<?,?> event){Long until=VANISHED.get(event.getEntity().getId());if(until!=null){if(until>System.currentTimeMillis())event.setCanceled(true);else VANISHED.remove(event.getEntity().getId());}}
    public static void receive(DeathParticlePacket packet){
        var client=Minecraft.getInstance();var level=client.level;if(level==null)return;var entity=level.getEntity(packet.entityId());if(packet.kind()==DeathParticlePacket.Kind.GIBLETS)VANISHED.put(packet.entityId(),System.currentTimeMillis()+2000);if(entity==null)return;
        var random=level.random;
        switch(packet.kind()){
            case ASHES->{VANISHED.put(packet.entityId(),System.currentTimeMillis()+2000);for(int i=0;i<packet.count();i++){double x=entity.getX()+(entity.getBbWidth()+packet.scale()*2)*(random.nextDouble()-.5),y=entity.getY()+entity.getBbHeight()*random.nextDouble(),z=entity.getZ()+(entity.getBbWidth()+packet.scale()*2)*(random.nextDouble()-.5);client.particleEngine.add(new AshesParticle(level,x,y,z,packet.scale()));client.particleEngine.createParticle(ParticleTypes.FLAME,x,y,z,0,0,0);}}
            case SKELETON->{
                if(!(entity instanceof LivingEntity living))return;VANISHED.put(packet.entityId(),System.currentTimeMillis()+2000);var boner=SkeletonBodies.SKULLANIZER.get(entity.getClass().getSimpleName());if(boner==null)return;boolean skeleton=entity instanceof AbstractSkeleton;
                for(var bone:boner.apply(living)){
                    if(packet.gib()&&random.nextBoolean()&&!skeleton)continue;
                    var particle=new SkeletonParticle(level,bone.x(),bone.y(),bone.z(),packet.brightness(),bone.type());particle.yaw=particle.oldYaw=bone.yaw();particle.pitch=particle.oldPitch=bone.pitch();
                    if(packet.gib()){particle.makeGib(skeleton);particle.setParticleSpeed(random.nextGaussian()*packet.force(),(random.nextGaussian()+1)*packet.force(),random.nextGaussian()*packet.force());}client.particleEngine.add(particle);
                }
            }
            case GIBLETS->{
                int width=(int)(entity.getBbWidth()/.25F),height=(int)(entity.getBbHeight()/.25F),count=(int)(width*1.5*height);if(packet.divider()>0)count=(int)Math.ceil(count/(double)packet.divider());double multiplier=random.nextInt(15)==0?10:1;
                for(int i=0;i<count;i++)client.particleEngine.add(new GibletParticle(level,packet.x(),packet.y(),packet.z(),random.nextGaussian()*.25*multiplier,random.nextDouble()*multiplier,random.nextGaussian()*.25*multiplier,packet.gibType()));
            }
        }
    }
}
