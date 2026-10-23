package com.hbm.block.pipe;

import com.hbm.registry.HbmSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;

/** Original enveloped pipe pitch, including the lower break pitch. */
public final class PipeSounds {
    private static boolean breaking;
    private static final java.util.Random RANDOM = new java.util.Random();
    public static final SoundType TYPE = new SoundType(0.85F, 0.85F, SoundType.METAL.getBreakSound(), SoundType.METAL.getStepSound(), SoundType.METAL.getPlaceSound(), SoundType.METAL.getHitSound(), SoundType.METAL.getFallSound()) {
        @Override public SoundEvent getBreakSound() { breaking = true; return HbmSounds.get("block.pipePlaced"); }
        @Override public SoundEvent getPlaceSound() { breaking = false; return HbmSounds.get("block.pipePlaced"); }
        @Override public SoundEvent getStepSound() { breaking = false; return super.getStepSound(); }
        @Override public float getPitch() { return 0.85F - (breaking ? 0.15F : 0F) + RANDOM.nextFloat() * 0.2F; }
    };
    private PipeSounds() {}
}
