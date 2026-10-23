package com.hbm.client.animation;

/** The frame-sampled millisecond clock used by X5846's weapon buses and HMF. */
public final class AnimationClock {
    private static long timeMillis;
    private AnimationClock() { }
    public static void update() { timeMillis = System.currentTimeMillis(); }
    public static void update(long frameTimeMillis) { timeMillis = frameTimeMillis; }
    public static long get_ms() { return timeMillis; }
}
