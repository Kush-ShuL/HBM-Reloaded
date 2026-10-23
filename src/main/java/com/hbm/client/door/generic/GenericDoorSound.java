package com.hbm.client.door.generic;
import com.hbm.door.generic.GenericDoorBlockEntity;
import com.hbm.registry.HbmSounds;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.*;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;
/** Source state-edge loop starts/stops, original linear10 range/volume2/pitch1 and terminal end sound. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class GenericDoorSound {
    private static final Map<GenericDoorBlockEntity,List<Loop>>LOOPS=new IdentityHashMap<>();
    public static void state(GenericDoorBlockEntity door,byte next){
        byte before=door.state;var manager=Minecraft.getInstance().getSoundManager();
        if(before==GenericDoorBlockEntity.CLOSED&&next==GenericDoorBlockEntity.OPENING||before==GenericDoorBlockEntity.OPEN&&next==GenericDoorBlockEntity.CLOSING){remove(door);var loops=new ArrayList<Loop>();if(door.declaration().loop!=null){var loop=new Loop(door,door.declaration().loop);loops.add(loop);manager.play(loop);}String start=door.declaration().startSound(next==GenericDoorBlockEntity.OPENING);if(start!=null){var pos=door.getBlockPos();door.getLevel().playLocalSound(pos.getX(),pos.getY(),pos.getZ(),HbmSounds.get(start),SoundSource.BLOCKS,door.volume(),1,false);}String secondary=door.declaration().secondaryLoop();if(secondary!=null){var extra=new Loop(door,secondary);loops.add(extra);manager.play(extra);}LOOPS.put(door,loops);}
        if(next==GenericDoorBlockEntity.OPEN||next==GenericDoorBlockEntity.CLOSED)remove(door);
        if(before==GenericDoorBlockEntity.OPENING&&next==GenericDoorBlockEntity.OPEN||before==GenericDoorBlockEntity.CLOSING&&next==GenericDoorBlockEntity.CLOSED){String sound=door.declaration().endSound(next==GenericDoorBlockEntity.OPEN);if(sound!=null){var pos=door.getBlockPos();door.getLevel().playLocalSound(pos.getX(),pos.getY(),pos.getZ(),HbmSounds.get(sound),SoundSource.BLOCKS,door.volume(),1,false);}}
    }
    public static void remove(GenericDoorBlockEntity door){var loops=LOOPS.remove(door);if(loops!=null)for(var loop:loops){loop.finish();Minecraft.getInstance().getSoundManager().stop(loop);}}
    @SubscribeEvent public static void unload(LevelEvent.Unload event){if(event.getLevel().isClientSide())for(var door:List.copyOf(LOOPS.keySet()))remove(door);}
    private static final class Loop extends AbstractTickableSoundInstance {
        private final GenericDoorBlockEntity door;
        Loop(GenericDoorBlockEntity door,String sound){super(HbmSounds.get(sound),SoundSource.BLOCKS,RandomSource.create());this.door=door;var pos=door.getBlockPos();x=pos.getX();y=pos.getY();z=pos.getZ();looping=true;attenuation=SoundInstance.Attenuation.NONE;pitch=1;update();}
        private void update(){var player=Minecraft.getInstance().player;volume=door.volume()*(player==null?1:Math.max(0,1-(float)Math.sqrt(player.distanceToSqr(x,y,z))/10));}
        @Override public void tick(){if(door.isRemoved())finish();else update();}void finish(){stop();}
    }
    private GenericDoorSound(){}
}
