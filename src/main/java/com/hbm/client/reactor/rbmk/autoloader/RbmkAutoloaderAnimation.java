package com.hbm.client.reactor.rbmk.autoloader;
import com.hbm.machine.reactor.rbmk.autoloader.RbmkAutoloaderEntity;
import com.hbm.particle.LegacyFluidEffects;
import com.hbm.registry.HbmSounds;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.*;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
/** Original two-tick approach, 25-block loop/stop and three true tower emissions while fully inserted. */
final class RbmkAutoloaderAnimation {
    private static final Map<RbmkAutoloaderEntity,RbmkAutoloaderAnimation> STATES=new WeakHashMap<>();private Loop loop;
    static void tick(RbmkAutoloaderEntity machine){STATES.computeIfAbsent(machine,key->new RbmkAutoloaderAnimation()).update(machine);}
    static void removed(RbmkAutoloaderEntity machine){var state=STATES.remove(machine);if(state!=null)state.stop(machine,false);}
    private void update(RbmkAutoloaderEntity machine){var game=Minecraft.getInstance();if(game.level==null)return;machine.lastPiston=machine.renderPiston;if(machine.turnProgress>0){machine.renderPiston+=(machine.syncPiston-machine.renderPiston)/machine.turnProgress;machine.turnProgress--;}else machine.renderPiston=machine.syncPiston;
        if(machine.renderPiston>.01&&machine.renderPiston<.99){if(loop==null||!game.getSoundManager().isActive(loop)){loop=new Loop(machine);game.getSoundManager().play(loop);}loop.keepAlive=0;loop.update();}else stop(machine,true);
        if(machine.renderPiston>.99){var pos=machine.getBlockPos();var random=game.level.random;int life=70+random.nextInt(30);double x=pos.getX()+.5+random.nextGaussian()*.125,z=pos.getZ()+.5+random.nextGaussian()*.125;for(int i=0;i<3;i++)LegacyFluidEffects.tower(game.level,x,pos.getY()+.25,z,new LegacyFluidEffects.Tower(0,.25F,1.5F,life).withoutWind().alpha(2).strafe(.05F));}
    }
    private void stop(RbmkAutoloaderEntity machine,boolean end){if(loop==null)return;var game=Minecraft.getInstance();loop.finish();game.getSoundManager().stop(loop);loop=null;if(end&&game.level!=null)game.getSoundManager().play(new SimpleSoundInstance(HbmSounds.get("door.wgh_stop"),SoundSource.BLOCKS,machine.volume(2),1,game.level.random,machine.getBlockPos()));}
    private static final class Loop extends AbstractTickableSoundInstance {
        private final RbmkAutoloaderEntity machine;private int keepAlive;
        Loop(RbmkAutoloaderEntity machine){super(HbmSounds.get("door.wgh_start"),SoundSource.BLOCKS,RandomSource.create());this.machine=machine;looping=true;attenuation=Attenuation.NONE;var pos=machine.getBlockPos();x=pos.getX();y=pos.getY();z=pos.getZ();pitch=1;update();}
        void update(){var player=Minecraft.getInstance().player;volume=machine.volume(.75F)*(player==null?1:Math.max(0,1-(float)Math.sqrt(player.distanceToSqr(x,y,z))/25));}
        @Override public void tick(){if(machine.isRemoved()||++keepAlive>5)finish();}void finish(){stop();}
    }
}
