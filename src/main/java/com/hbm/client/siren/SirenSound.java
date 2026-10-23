package com.hbm.client.siren;

import com.hbm.machine.siren.*;
import com.hbm.registry.HbmSounds;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

/** Native sound instance preserving original corner position, RECORDS slider and manual attenuation. */
public final class SirenSound extends AbstractTickableSoundInstance {
    private static final List<SirenSound> LIST=new ArrayList<>();
    private final SirenBlockEntity machine;
    private final SirenTrack.SoundType type;
    private final String originalPath;
    public float intendedVolume=10.0F;
    private SirenSound(SirenTrack track,SirenBlockEntity machine){super(HbmSounds.get(track.sourceSound),SoundSource.RECORDS,RandomSource.create());this.machine=machine;this.type=track.type;this.originalPath="hbm:"+track.sourceSound;LIST.add(this);looping=true;volume=1;pitch=1;delay=0;attenuation=Attenuation.NONE;var pos=machine.getBlockPos();x=(float)pos.getX();y=(float)pos.getY();z=(float)pos.getZ();}
    @Override public void tick(){if(machine.isRemoved())stop();var player=Minecraft.getInstance().player;if(player!=null){float distance=(float)Math.sqrt(Math.pow(x-player.getX(),2)+Math.pow(y-player.getY(),2)+Math.pow(z-player.getZ(),2));volume=(distance/intendedVolume)*-2+2;}else volume=intendedVolume;looping=type==SirenTrack.SoundType.LOOP;}
    public static void packet(SirenPacket packet){var minecraft=Minecraft.getInstance();if(minecraft.level==null||!(minecraft.level.getBlockEntity(new BlockPos(packet.x(),packet.y(),packet.z())) instanceof SirenBlockEntity machine))return;SirenSound sound=null;for(var candidate:LIST)if(candidate.machine==machine)sound=candidate;
        var track=SirenTrack.getEnum(packet.track());if(packet.active()){if(sound==null){if(packet.track()>0){var created=new SirenSound(track,machine);created.looping=track.type==SirenTrack.SoundType.LOOP;created.intendedVolume=track.volume;minecraft.getSoundManager().play(created);}}else{if(track.sourceSound!=null&&!sound.originalPath.equals("hbm:"+track.sourceSound)){sound.stop();if(packet.track()>0)minecraft.getSoundManager().play(new SirenSound(track,machine));}sound.intendedVolume=track.volume;}}else if(sound!=null){sound.stop();LIST.remove(sound);}}
}
