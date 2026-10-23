package com.hbm.client.entity.aircraft.c130;

import com.hbm.entity.aircraft.c130.EntityC130;
import com.hbm.entity.aircraft.c130.EntityPlaneBase;
import com.hbm.registry.HbmSounds;
import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;

/** Original AudioDynamic linear250-block volume, max2, pitch1 and20-tick keep-alive. */
@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT)
public final class C130Sound extends AbstractTickableSoundInstance {
    private static final Map<EntityPlaneBase, C130Sound> SOUNDS = new IdentityHashMap<>();
    private int timeSinceKeepAlive;
    private C130Sound(EntityPlaneBase plane,String sound) {
        super(HbmSounds.get(sound), SoundSource.PLAYERS, RandomSource.create());
        looping = true; delay = 0; pitch = 1; volume = 1;
        attenuation = SoundInstance.Attenuation.NONE;
        updatePosition(plane);
    }
    private void updatePosition(EntityPlaneBase plane) { x = (float) plane.getX(); y = (float) plane.getY(); z = (float) plane.getZ(); }
    public static void keepAlive(EntityC130 plane) { keepAlive(plane,"entity.bomberLoop"); }
    public static void keepAlive(EntityPlaneBase plane,String soundId) {
        if (plane.isRemoved() || plane.syncedHealth() <= 0) { remove(plane); return; }
        var manager = Minecraft.getInstance().getSoundManager();
        var sound = SOUNDS.get(plane);
        if (sound == null || !manager.isActive(sound)) {
            if (sound != null) { sound.stop(); manager.stop(sound); }
            sound = new C130Sound(plane,soundId); SOUNDS.put(plane, sound); manager.play(sound);
        }
        sound.timeSinceKeepAlive = 0; sound.updatePosition(plane);
    }
    @Override public void tick() {
        var player = Minecraft.getInstance().player;
        volume = player == null ? 2 : (float) Math.sqrt(player.distanceToSqr(x, y, z)) / 250F * -2F + 2F;
        if (timeSinceKeepAlive > 20) stop();
        timeSinceKeepAlive++;
    }
    public static void remove(EntityPlaneBase plane) {
        var sound = SOUNDS.remove(plane);
        if (sound != null) { sound.stop(); Minecraft.getInstance().getSoundManager().stop(sound); }
    }
    @SubscribeEvent public static void unload(LevelEvent.Unload event) {
        if (event.getLevel().isClientSide()) for (var plane : SOUNDS.keySet().toArray(EntityPlaneBase[]::new)) remove(plane);
    }
}
