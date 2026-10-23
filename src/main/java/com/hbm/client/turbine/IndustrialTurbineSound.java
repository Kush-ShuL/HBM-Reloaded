package com.hbm.client.turbine;
import com.hbm.machine.turbine.IndustrialTurbineBlockEntity;
import com.hbm.registry.HbmSounds;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;
/** Original spin-dependent large-turbine loop,35-block start check,20-range wrapper and20-tick keepAlive. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class IndustrialTurbineSound {
    private static final Map<IndustrialTurbineBlockEntity,Loop> SOUNDS=new IdentityHashMap<>();
    public static void tick(IndustrialTurbineBlockEntity turbine){var player=Minecraft.getInstance().player;var pos=turbine.getBlockPos();if(turbine.spin>0&&player!=null&&player.distanceToSqr(pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5)<=1225){var manager=Minecraft.getInstance().getSoundManager();var sound=SOUNDS.get(turbine);if(sound==null||!manager.isActive(sound)){if(sound!=null)sound.finish();sound=new Loop(turbine);SOUNDS.put(turbine,sound);manager.play(sound);}sound.keepAlive=0;sound.update();}else removed(turbine);}
    public static void removed(IndustrialTurbineBlockEntity turbine){var sound=SOUNDS.remove(turbine);if(sound!=null){sound.finish();Minecraft.getInstance().getSoundManager().stop(sound);}}
    @SubscribeEvent public static void unload(LevelEvent.Unload event){if(event.getLevel().isClientSide()){for(var sound:SOUNDS.values()){sound.finish();Minecraft.getInstance().getSoundManager().stop(sound);}SOUNDS.clear();}}
    private static final class Loop extends AbstractTickableSoundInstance {
        private final IndustrialTurbineBlockEntity turbine;private int keepAlive;
        Loop(IndustrialTurbineBlockEntity turbine){super(HbmSounds.get("block.largeTurbineRunning"),SoundSource.BLOCKS,RandomSource.create());this.turbine=turbine;looping=true;attenuation=Attenuation.NONE;var pos=turbine.getBlockPos();x=pos.getX()+.5;y=pos.getY()+.5;z=pos.getZ()+.5;update();}
        void update(){float spin=(float)Math.min(1F,turbine.spin*2),maximum=(.25F+spin*.75F)*(turbine.muffled?.1F:1F);pitch=.5F+spin*.5F+turbine.audioDesync;var player=Minecraft.getInstance().player;float distance=player==null?0:(float)Math.sqrt(player.distanceToSqr(x,y,z));volume=Math.max(0,distance/20F*-maximum+maximum);}
        @Override public void tick(){if(turbine.isRemoved()||keepAlive>20)finish();keepAlive++;update();}void finish(){stop();}
    }
}
