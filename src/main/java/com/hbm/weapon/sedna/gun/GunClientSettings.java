package com.hbm.weapon.sedna.gun;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class GunClientSettings {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.DoubleValue GUN_ANIMATION_SPEED;
    public static final ModConfigSpec.BooleanValue GUN_MODEL_FOV,GUN_VISUAL_RECOIL,GUN_ANIMS_LEGACY;
    static{var builder=new ModConfigSpec.Builder();GUN_MODEL_FOV=builder.define(java.util.List.of("viewmodel","useCameraFov"),false);GUN_VISUAL_RECOIL=builder.define(java.util.List.of("viewmodel","visualRecoilEnabled"),true);GUN_ANIMS_LEGACY=builder.define(java.util.List.of("animation","classicStyleEnabled"),false);GUN_ANIMATION_SPEED=builder.comment("枪械视觉动画速度倍率。默认1；大于1加快，小于1放慢；范围0至1.7976931348623157E308。",
            "0按0.001倍计算，不会禁用动画。只调整视觉关键帧，不改变射速、弹药或实际装填耗时。",
            "新建关键帧读取当前值；已经缓存的资源动画需重载资源后生效。")
            .defineInRange(java.util.List.of("animation","speedMultiplier"),1D,0D,Double.MAX_VALUE);SPEC=builder.build();}
    public static boolean modelFov(){return SPEC.isLoaded()&&GUN_MODEL_FOV.get();}
    public static boolean visualRecoil(){return !SPEC.isLoaded()||GUN_VISUAL_RECOIL.get();}
    public static boolean legacyAnimations(){return SPEC.isLoaded()&&GUN_ANIMS_LEGACY.get();}
    public static double animationSpeed(){return SPEC.isLoaded()?GUN_ANIMATION_SPEED.get():1D;}
    private GunClientSettings(){}
}
