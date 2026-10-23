package com.hbm.client.animation;

import com.hbm.client.animation.bus.HbmAnimations;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT)
public final class AnimationClientEvents {
    private AnimationClientEvents() { }

    @SubscribeEvent
    public static void sampleWorldRenderClock(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) AnimationClock.update();
    }

    @SubscribeEvent
    public static void expireBeforeHud(RenderGuiEvent.Pre event) {
        HbmAnimations.expire(AnimationClock.get_ms());
    }
}
