package com.hbm.client.particle;

import com.hbm.particle.LegacyFluidEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;

public final class LegacyFluidEffectsClient {
    public static void receive(LegacyFluidEffects.Packet packet){
        var minecraft=Minecraft.getInstance();var level=minecraft.level;if(level==null)return;
        if(packet.gasfire()){minecraft.particleEngine.add(new LegacyGasFlameParticle(level,packet.x(),packet.y(),packet.z(),packet.mx(),packet.my(),packet.mz(),packet.scale()));return;}
        int setting=minecraft.options.particles().get()==ParticleStatus.ALL?0:minecraft.options.particles().get()==ParticleStatus.DECREASED?1:2;
        if(setting==2||setting==1&&!level.random.nextBoolean())return;
        var t=packet.tower();var particle=new LegacyCoolingTowerParticle(level,packet.x(),packet.y(),packet.z());particle.setLift(t.lift());particle.setBaseScale(t.base());particle.setMaxScale(t.maximum());particle.setLife(t.life()/(setting+1));if(t.noWind())particle.noWind();if(t.strafe()!=null)particle.setStrafe(t.strafe());if(t.alpha()!=null)particle.alphaMod(t.alpha());if(t.color()!=null){int color=t.color();particle.setColor((color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F);}minecraft.particleEngine.add(particle);
    }
    private LegacyFluidEffectsClient(){}
}
