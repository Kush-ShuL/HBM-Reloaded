package com.hbm.damage;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

/** Registry-backed source definitions; original message IDs retain their exact spelling. */
public final class HbmDamageSources {
    private HbmDamageSources() {}
    private static ResourceKey<DamageType> key(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("hbm", name));
    }
    public static DamageSource source(Level level, ResourceKey<DamageType> type) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type));
    }
    private static DamageSource indirect(String name, Entity direct, Entity owner) {
        return new DamageSource(direct.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(key(name)), direct, owner);
    }
    public static final ResourceKey<DamageType> nuclearBlast = key("nuclearblast");
    public static final ResourceKey<DamageType> mudPoisoning = key("mudpoisoning");
    public static final ResourceKey<DamageType> acid = key("acid");
    public static final ResourceKey<DamageType> euthanizedSelf = key("euthanizedself");
    public static final ResourceKey<DamageType> euthanizedSelf2 = key("euthanizedself2");
    public static final ResourceKey<DamageType> tauBlast = key("taublast");
    public static final ResourceKey<DamageType> radiation = key("radiation");
    public static final ResourceKey<DamageType> digamma = key("digamma");
    public static final ResourceKey<DamageType> suicide = key("suicide");
    public static final ResourceKey<DamageType> rubble = key("rubble");
    public static final ResourceKey<DamageType> shrapnel = key("shrapnel");
    public static final ResourceKey<DamageType> blackhole = key("blackhole");
    public static final ResourceKey<DamageType> turbofan = key("blender");
    public static final ResourceKey<DamageType> meteorite = key("meteorite");
    public static final ResourceKey<DamageType> boxcar = key("boxcar");
    public static final ResourceKey<DamageType> boat = key("boat");
    public static final ResourceKey<DamageType> building = key("building");
    public static final ResourceKey<DamageType> taint = key("taint");
    public static final ResourceKey<DamageType> ams = key("ams");
    public static final ResourceKey<DamageType> amsCore = key("amscore");
    public static final ResourceKey<DamageType> broadcast = key("broadcast");
    public static final ResourceKey<DamageType> bang = key("bang");
    public static final ResourceKey<DamageType> pc = key("pc");
    public static final ResourceKey<DamageType> cloud = key("cloud");
    public static final ResourceKey<DamageType> lead = key("lead");
    public static final ResourceKey<DamageType> enervation = key("enervation");
    public static final ResourceKey<DamageType> electricity = key("electricity");
    public static final ResourceKey<DamageType> exhaust = key("exhaust");
    public static final ResourceKey<DamageType> spikes = key("spikes");
    public static final ResourceKey<DamageType> lunar = key("lunar");
    public static final ResourceKey<DamageType> monoxide = key("monoxide");
    public static final ResourceKey<DamageType> asbestos = key("asbestos");
    public static final ResourceKey<DamageType> blacklung = key("blacklung");
    public static final ResourceKey<DamageType> mku = key("mku");
    public static final ResourceKey<DamageType> vacuum = key("vacuum");
    public static final ResourceKey<DamageType> overdose = key("overdose");
    public static final ResourceKey<DamageType> microwave = key("microwave");
    public static final String s_bullet = "revolverBullet";
    public static final String s_emplacer = "chopperBullet";
    public static final String s_tau = "tau";
    public static final String s_combineball = "cmb";
    public static final String s_zomg_prefix = "subAtomic";
    public static final String s_euthanized = "euthanized";
    public static final String s_emp = "electrified";
    public static final String s_flamethrower = "flamethrower";
    public static final String s_immolator = "plasma";
    public static final String s_cryolator = "ice";
    public static final String s_laser = "laser";
    public static final String s_boil = "boil";
    public static final String s_acid = "acidPlayer";
    public static DamageSource causeBulletDamage(Entity direct, Entity owner) {
        return indirect("revolverbullet", direct, owner);
    }
    public static DamageSource causeDisplacementDamage(Entity direct, Entity owner) {
        return indirect("chopperbullet", direct, owner);
    }
    public static DamageSource causeTauDamage(Entity direct, Entity owner) {
        return indirect("tau", direct, owner);
    }
    public static DamageSource causeCombineDamage(Entity direct, Entity owner) {
        return indirect("cmb", direct, owner);
    }
    public static DamageSource euthanized(Entity direct, Entity owner) {
        return indirect("euthanized", direct, owner);
    }
    public static DamageSource causeSubatomicDamage(Entity direct, Entity owner) {
        return indirect("subatomic" + (direct.level().random.nextInt(5) + 1), direct, owner);
    }
    public static boolean getIsTau(DamageSource source) {
        return source.getDirectEntity() != null && source.is(key("tau"));
    }
    public static boolean getIsSubatomic(DamageSource source) {
        if (source.getDirectEntity() == null) return false;
        return source.typeHolder().unwrapKey().map(key -> key.location().getNamespace().equals("hbm") && key.location().getPath().startsWith("subatomic")).orElse(false);
    }
}
