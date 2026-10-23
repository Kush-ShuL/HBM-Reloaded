package com.hbm.client.weapon.accelerator;
import com.hbm.registry.HbmSounds;
import com.hbm.weapon.sedna.gun.GunAnimation;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
/** Actual attached Tau charge loop: source range15, keepAlive10 and uncapped rising pitch. */
public final class TauChargeAudio {
    private static final Map<LivingEntity,Loop> SOUNDS=new WeakHashMap<>();
    public static void update(LivingEntity owner,GunAnimation animation,int timer){
        var running=SOUNDS.get(owner);
        if(animation!=GunAnimation.SPINUP||timer>=300){if(running!=null)running.finish();return;}
        if(running==null||running.isStopped()){var created=new Loop(owner);SOUNDS.put(owner,created);Minecraft.getInstance().getSoundManager().play(created);}
        if(running!=null&&!running.isStopped()){running.keepAlive=0;running.pitch(.75F+timer*.01F);running.attach();}
    }
    private static final class Loop extends AbstractTickableSoundInstance {
        private final LivingEntity owner;private int keepAlive;
        Loop(LivingEntity owner){super(HbmSounds.get("weapon.fire.tauLoop"),SoundSource.PLAYERS,RandomSource.create());this.owner=owner;looping=true;attenuation=Attenuation.NONE;pitch=.75F;attach();}
        void pitch(float value){pitch=value;}
        void attach(){x=(float)owner.getX();y=(float)owner.getY();z=(float)owner.getZ();var player=Minecraft.getInstance().player;if(player==owner){y+=10;volume=1F;}else volume=player==null?1F:1F-(float)Math.sqrt(player.distanceToSqr(x,y,z))/15F;}
        @Override public void tick(){if(owner.isRemoved()||keepAlive>10){finish();return;}keepAlive++;attach();}
        void finish(){stop();}
    }
    private TauChargeAudio(){}
}
