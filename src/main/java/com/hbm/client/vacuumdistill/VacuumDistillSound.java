package com.hbm.client.vacuumdistill;
import com.hbm.machine.vacuumdistill.VacuumDistillBlockEntity;
import com.hbm.registry.HbmSounds;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
/** Source boiler sound0.25 volume/15 range/20-tick grace, native lifecycle cleanup. */
public final class VacuumDistillSound {
    private static final Map<VacuumDistillBlockEntity,VacuumDistillSound> STATES=new WeakHashMap<>();private int audioTime;private Loop sound;
    private VacuumDistillSound(){}
    public static void tick(VacuumDistillBlockEntity machine){STATES.computeIfAbsent(machine,m->new VacuumDistillSound()).update(machine);}
    public static void removed(VacuumDistillBlockEntity machine){var state=STATES.remove(machine);if(state!=null)state.stop();}
    private void stop(){if(sound!=null){sound.finish();Minecraft.getInstance().getSoundManager().stop(sound);sound=null;}}
    private void update(VacuumDistillBlockEntity machine){if(machine.isOn)audioTime=20;if(audioTime>0){audioTime--;var manager=Minecraft.getInstance().getSoundManager();if(sound==null||!manager.isActive(sound)){sound=new Loop(machine);manager.play(sound);}sound.keepAlive=0;sound.update();}else stop();}
    private static final class Loop extends AbstractTickableSoundInstance{private final VacuumDistillBlockEntity machine;private int keepAlive;Loop(VacuumDistillBlockEntity machine){super(HbmSounds.get("block.boiler"),SoundSource.BLOCKS,RandomSource.create());this.machine=machine;looping=true;attenuation=Attenuation.NONE;var p=machine.getBlockPos();x=p.getX();y=p.getY();z=p.getZ();update();}void update(){pitch=1;var player=Minecraft.getInstance().player;volume=(machine.muffled?.025F:.25F)*(player==null?1:(float)(1-Math.sqrt(player.distanceToSqr(x,y,z))/15));}@Override public void tick(){if(machine.isRemoved()||++keepAlive>20)finish();}void finish(){stop();}}
}
