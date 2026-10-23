package com.hbm.client.meteor;

import com.hbm.registry.HbmSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

/** AudioDynamic: manual linear200 distance, keepAlive10, source ClientProxy never applies its pitch argument. */
final class MeteorFallingSound extends AbstractTickableSoundInstance {
    private int timeSinceKeepAlive;
    MeteorFallingSound(float x,float y,float z){
        super(HbmSounds.get("entity.meteoriteFallingLoop"),SoundSource.BLOCKS,RandomSource.create());
        this.x=x;this.y=y;this.z=z;looping=true;attenuation=Attenuation.NONE;pitch=1;volume=1;
    }
    void keepAlive(float x,float y,float z){timeSinceKeepAlive=0;this.x=x;this.y=y;this.z=z;}
    @Override public void tick(){
        var player=Minecraft.getInstance().player;
        float distance=player==null?0:(float)Math.sqrt(Math.pow(x-player.getX(),2)+Math.pow(y-player.getY(),2)+Math.pow(z-player.getZ(),2));
        volume=distance/200F*-1F+1F;
        if(timeSinceKeepAlive>10)finish();
        timeSinceKeepAlive++;
    }
    void finish(){stop();}
}
