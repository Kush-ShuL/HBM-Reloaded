package com.hbm.client.gascentrifuge;

import com.hbm.machine.gascentrifuge.GasCentrifugeBlockEntity;
import com.hbm.registry.HbmSounds;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

/** Original +2/-3 ramp, >10 threshold, 25-block wake range and AudioDynamic linear10-block volume. */
public final class GasCentrifugeSound {
    private static final Map<GasCentrifugeBlockEntity,State> STATES=new WeakHashMap<>();
    private static final class State { int ramp; Loop sound; }
    private GasCentrifugeSound() {}
    public static void clientTick(GasCentrifugeBlockEntity machine) {
        var state=STATES.computeIfAbsent(machine,ignored->new State()); state.ramp=Math.max(0,Math.min(60,state.ramp+(machine.isProgressing?2:-3)));
        var client=Minecraft.getInstance(); var player=client.player;
        if(state.ramp>10&&player!=null&&player.distanceToSqr(machine.getBlockPos().getX()+.5,machine.getBlockPos().getY()+.5,machine.getBlockPos().getZ()+.5)<625) {
            if(state.sound==null||!client.getSoundManager().isActive(state.sound)) { state.sound=new Loop(machine); client.getSoundManager().play(state.sound); }
            state.sound.update(state.ramp); state.sound.keepAlive=0;
        } else if(state.sound!=null) { state.sound.finish(); client.getSoundManager().stop(state.sound); state.sound=null; }
    }
    public static void removed(GasCentrifugeBlockEntity machine) { State state=STATES.remove(machine); if(state!=null&&state.sound!=null) { state.sound.finish(); Minecraft.getInstance().getSoundManager().stop(state.sound); } }
    private static final class Loop extends AbstractTickableSoundInstance {
        final GasCentrifugeBlockEntity machine; int keepAlive;
        Loop(GasCentrifugeBlockEntity machine) {
            super(HbmSounds.get("block.centrifugeoperate"),SoundSource.BLOCKS,RandomSource.create()); this.machine=machine;
            looping=true; delay=0; attenuation=Attenuation.NONE; x=machine.getBlockPos().getX(); y=machine.getBlockPos().getY(); z=machine.getBlockPos().getZ(); update(12);
        }
        void update(int ramp) { pitch=(ramp-10)/100F+.5F; var player=Minecraft.getInstance().player; volume=machine.volume(player==null?1:(float)(1-Math.sqrt(player.distanceToSqr(x,y,z))/10)); }
        @Override public void tick() { if(machine.isRemoved()||++keepAlive>20) finish(); }
        void finish() { stop(); }
    }
}
