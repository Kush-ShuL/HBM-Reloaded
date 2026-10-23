package com.hbm.client.chemicalfactory;

import com.hbm.machine.chemicalfactory.ChemicalFactoryEntity;
import com.hbm.registry.HbmSounds;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

/** Original transient slider/spinner clock, overhead frame probe and AudioDynamic loop. */
public final class ChemicalFactoryAnimation {
    private static final Map<ChemicalFactoryEntity,ChemicalFactoryAnimation> STATES=new WeakHashMap<>();
    public int anim,previous;public boolean frame;private Loop sound;
    private ChemicalFactoryAnimation(){}
    public static ChemicalFactoryAnimation get(ChemicalFactoryEntity machine){return STATES.computeIfAbsent(machine,key->new ChemicalFactoryAnimation());}
    public static void tick(ChemicalFactoryEntity machine){get(machine).update(machine);}
    public static void removed(ChemicalFactoryEntity machine){var state=STATES.remove(machine);if(state!=null)state.stop();}
    public float animation(float partial){return previous+(anim-previous)*partial;}
    private void stop(){if(sound!=null){sound.finish();Minecraft.getInstance().getSoundManager().stop(sound);sound=null;}}
    private void update(ChemicalFactoryEntity machine){
        var level=machine.getLevel();if(level==null)return;var client=Minecraft.getInstance();var pos=machine.getBlockPos();
        previous=anim;if(machine.processing())anim++;
        if(level.getGameTime()%20==0)frame=!level.getBlockState(pos.above(3)).isAir();
        if(machine.processing()&&client.player!=null&&client.player.distanceToSqr(pos.getX(),pos.getY(),pos.getZ())<2500){
            if(sound==null||!client.getSoundManager().isActive(sound)){sound=new Loop(machine);client.getSoundManager().play(sound);}sound.keepAlive=0;sound.update();
        }else stop();
    }
    private static final class Loop extends AbstractTickableSoundInstance {
        private final ChemicalFactoryEntity machine;private int keepAlive;
        Loop(ChemicalFactoryEntity machine){super(HbmSounds.get("block.chemicalPlant"),SoundSource.BLOCKS,RandomSource.create());this.machine=machine;looping=true;attenuation=Attenuation.NONE;var p=machine.getBlockPos();x=p.getX();y=p.getY();z=p.getZ();update();}
        void update(){pitch=1;var player=Minecraft.getInstance().player;volume=(machine.isMuffled()?.1F:1F)*(player==null?1:(float)(1-Math.sqrt(player.distanceToSqr(x,y,z))/15));}
        @Override public void tick(){if(machine.isRemoved()||++keepAlive>20)finish();}
        void finish(){stop();}
    }
}
