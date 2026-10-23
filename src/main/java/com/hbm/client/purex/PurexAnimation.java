package com.hbm.client.purex;

import com.hbm.machine.purex.PurexBlockEntity;
import com.hbm.registry.HbmSounds;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

/** Original transient fan/pump clock, overhead frame probe and AudioDynamic loop. */
public final class PurexAnimation {
    private static final Map<PurexBlockEntity,PurexAnimation> STATES=new WeakHashMap<>();
    public int anim,previous;public boolean frame;private Loop sound;
    private PurexAnimation(){}
    public static PurexAnimation get(PurexBlockEntity machine){return STATES.computeIfAbsent(machine,key->new PurexAnimation());}
    public static void tick(PurexBlockEntity machine){get(machine).update(machine);}
    public static void removed(PurexBlockEntity machine){var state=STATES.remove(machine);if(state!=null)state.stop();}
    public float animation(float partial){return previous+(anim-previous)*partial;}
    private void stop(){if(sound!=null){sound.finish();Minecraft.getInstance().getSoundManager().stop(sound);sound=null;}}
    private void update(PurexBlockEntity machine){
        var level=machine.getLevel();if(level==null)return;var client=Minecraft.getInstance();var pos=machine.getBlockPos();
        previous=anim;if(machine.processing())anim++;
        if(level.getGameTime()%20==0)frame=!level.getBlockState(pos.above(5)).isAir();
        if(machine.processing()&&client.player!=null&&client.player.distanceToSqr(pos.getX(),pos.getY(),pos.getZ())<625){
            if(sound==null||!client.getSoundManager().isActive(sound)){sound=new Loop(machine);client.getSoundManager().play(sound);}sound.keepAlive=0;sound.update();
        }else stop();
    }
    private static final class Loop extends AbstractTickableSoundInstance {
        private final PurexBlockEntity machine;private int keepAlive;
        Loop(PurexBlockEntity machine){super(HbmSounds.get("block.chemicalPlant"),SoundSource.BLOCKS,RandomSource.create());this.machine=machine;looping=true;attenuation=Attenuation.NONE;var p=machine.getBlockPos();x=p.getX();y=p.getY();z=p.getZ();update();}
        void update(){pitch=.75F;var player=Minecraft.getInstance().player;volume=machine.volume(1)*(player==null?1:(float)(1-Math.sqrt(player.distanceToSqr(x,y,z))/15));}
        @Override public void tick(){if(machine.isRemoved()||keepAlive>15)finish();keepAlive++;update();}
        void finish(){stop();}
    }
}
