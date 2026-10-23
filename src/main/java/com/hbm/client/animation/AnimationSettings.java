package com.hbm.client.animation;

import java.util.Objects;
import com.hbm.weapon.sedna.gun.GunClientSettings;
import java.util.function.DoubleSupplier;

/** One runtime supplier for the client gun-keyframe speed. */
public final class AnimationSettings {
    private static volatile DoubleSupplier speed = GunClientSettings::animationSpeed;
    private AnimationSettings() { }
    public static double gunAnimationSpeed() { return speed.getAsDouble(); }
    public static void bindGunAnimationSpeed(DoubleSupplier config) { speed = Objects.requireNonNull(config); }
}
