package com.hbm.entity.missile;

import api.hbm.entity.IRadarDetectableNT;
import com.hbm.block.LegacyBlockTraits;
import com.hbm.bomb.nuclear.*;
import com.hbm.entity.aircraft.bomber.BomberClouds;
import com.hbm.entity.mob.maskman.LegacyNpcBullets;
import com.hbm.entity.projectile.EntityBulletBaseNT;
import com.hbm.explosion.ExplosionLarge;
import com.hbm.missile.custom.*;
import com.hbm.world.taint.*;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import static com.hbm.missile.custom.CustomMissileParts.*;

/** Original EntityMissileCustom on the one already-real NT trajectory/chunk-ticket engine. */
public final class CustomMissileEntity extends EntityMissileBaseNT {
    private static final EntityDataAccessor<CompoundTag> PARTS = SynchedEntityData.defineId(CustomMissileEntity.class, EntityDataSerializers.COMPOUND_TAG);
    public float fuel, consumption;
    public static Consumer<Exhaust> CONTRAIL = effect -> {};
    public record Exhaust(Level level, double x, double y, double z, FuelType fuel) {}
    public CustomMissileEntity(EntityType<? extends CustomMissileEntity> type, Level level) { super(type, level); }
    public CustomMissileEntity(Level level, float x, float y, float z, int targetX, int targetZ, CustomMissileStruct parts) {
        this(CustomMissileFlightContent.CUSTOM.get(), level); initialize(x, y, z, targetX, targetZ);
        setYRot(0); // Original custom constructor starts yaw0; the shared server trajectory supplies bearing on its first tick.
        setParts(parts); fuel = parts.fuselage().definition.number(1); consumption = parts.thruster().definition.number(1);
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(PARTS, new CompoundTag()); }
    public CustomMissileStruct parts() { return CustomMissileStruct.read(entityData.get(PARTS)); }
    private void setParts(CustomMissileStruct parts) { entityData.set(PARTS, parts.write()); }
    @Override public void tick() {
        var parts = parts();
        if (parts.warhead() == null || parts.fuselage() == null || parts.thruster() == null) { discard(); return; }
        var type = WarheadType.valueOf(parts.warhead().definition.enumName(0));
        if (type.updateCustom != null) type.updateCustom.accept(this);
        if (!level().isClientSide && hasPropulsion()) fuel -= consumption;
        super.tick();
    }
    @Override public boolean hasPropulsion() { return fuel > 0; }
    @Override protected void spawnContrail() {
        var parts = parts(); if (parts.fuselage() == null) return;
        var type = FuelType.valueOf(parts.fuselage().definition.enumName(0)); if (type == FuelType.XENON) return;
        var motion = getDeltaMovement(); var direction = LegacyVector.createVectorHelper(motion.x, motion.y, motion.z).normalize();
        for (int i = 0; i < velocity; i++) CONTRAIL.accept(new Exhaust(level(), getX() - direction.xCoord * i, getY() - direction.yCoord * i, getZ() - direction.zCoord * i, type));
    }
    @Override protected void killMissile() {
        if (isRemoved()) return;
        discard(); var motion = getDeltaMovement();
        ExplosionLarge.explode(level(), getX(), getY(), getZ(), 5, true, false, true);
        ExplosionLarge.spawnShrapnelShower(level(), getX(), getY(), getZ(), motion.x, motion.y, motion.z, 15, .075);
    }
    @Override public void onMissileImpact(HitResult hit) {
        var part = parts().warhead(); if (part == null || level().isClientSide) return;
        var type = WarheadType.valueOf(part.definition.enumName(0)); float strength = part.definition.number(1);
        if (type.impactCustom != null) { type.impactCustom.accept(this); return; }
        var motion = getDeltaMovement();
        switch (type) {
            case HE -> {
                ExplosionLarge.explode(level(), getX(), getY(), getZ(), strength, true, false, true);
                ExplosionLarge.jolt(level(), getX(), getY(), getZ(), strength, (int) (strength * 50), .25);
            }
            case INC -> {
                ExplosionLarge.explodeFire(level(), getX(), getY(), getZ(), strength, true, false, true);
                ExplosionLarge.jolt(level(), getX(), getY(), getZ(), strength * 1.5, (int) (strength * 50), .25);
            }
            case BUSTER -> {
                // Exact original ExplosionLarge.buster: fixed two-block steps, native explosion at every step, no replacement blast algorithm.
                var vector = LegacyVector.createVectorHelper(motion.x, motion.y, motion.z).normalize();
                for (int i = 0; i < strength * 4; i += 2) level().explode(null, getX() + vector.xCoord * i, getY() + vector.yCoord * i, getZ() + vector.zCoord * i, strength, Level.ExplosionInteraction.TNT);
            }
            case NUCLEAR, TX -> {
                level().addFreshEntity(NuclearExplosionMK5.statFac(level(), (int) strength, getX(), getY(), getZ()));
                NuclearTorex.statFacStandard(level(), getX(), getY(), getZ(), strength);
            }
            case BALEFIRE -> {
                var explosion = new NuclearBalefireExplosion(NuclearSpecialContent.BALEFIRE_EXPLOSION.get(), level());
                explosion.setPos(position()); explosion.destructionRange = (int) strength; level().addFreshEntity(explosion);
                NuclearTorex.statFacBale(level(), getX(), getY(), getZ(), strength);
            }
            case N2 -> {
                level().addFreshEntity(NuclearExplosionMK5.statFacNoRad(level(), (int) strength, getX(), getY(), getZ()));
                NuclearTorex.statFacStandard(level(), getX(), getY(), getZ(), strength);
            }
            case TAINT -> {
                int radius = (int) strength;
                for (int i = 0; i < radius * 10; i++) {
                    int x = random.nextInt(radius) + (int) getX() - (radius / 2 - 1), y = random.nextInt(radius) + (int) getY() - (radius / 2 - 1), z = random.nextInt(radius) + (int) getZ() - (radius / 2 - 1);
                    var pos = new BlockPos(x, y, z); var state = level().getBlockState(pos);
                    if (LegacyBlockTraits.isNormalCube(level(), pos, state) && !state.isAir()) level().setBlock(pos, TaintContent.TAINT.get().defaultBlockState().setValue(TaintBlock.META, random.nextInt(3) + 4), 2);
                }
            }
            case CLOUD -> {
                level().levelEvent(2002, new BlockPos((int) Math.round(getX()), (int) Math.round(getY()), (int) Math.round(getZ())), 0);
                BomberClouds.spawn(level(), getX() - motion.x, getY() - motion.y, getZ() - motion.z, 750, 2.5, true);
            }
            case TURBINE -> {
                ExplosionLarge.explode(level(), getX(), getY(), getZ(), 10, true, false, true);
                int count = (int) strength; var vector = LegacyVector.createVectorHelper(.5, 0, 0);
                for (int i = 0; i < count; i++) {
                    var blade = new EntityBulletBaseNT(level(), LegacyNpcBullets.TURBINE);
                    blade.moveTo(getX() - motion.x, getY() - motion.y + random.nextGaussian(), getZ() - motion.z, 0, 0);
                    blade.setDeltaMovement(vector.xCoord, 0, vector.zCoord); level().addFreshEntity(blade);
                    vector.rotateAroundY((float) (Math.PI * 2F / (float) count));
                }
            }
            // Original CLUSTER and SCHRAB/custom defaults have no built-in action; callbacks above remain the source extension path.
            default -> { }
        }
    }
    @Override protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag); tag.putFloat("fuel", fuel); tag.putFloat("consumption", consumption);
        var parts = entityData.get(PARTS); for (var key : new String[]{"warhead", "fuselage", "fins", "thruster"}) if (parts.contains(key)) tag.putString(key, parts.getString(key));
    }
    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag); fuel = tag.getFloat("fuel"); consumption = tag.getFloat("consumption"); setParts(CustomMissileStruct.read(tag));
    }
    @Override public String getUnlocalizedName() {
        var f = parts().fuselage(); if (f == null) return "radar.target.custom";
        if (f.definition.top() == PartSize.SIZE_10 && f.definition.bottom() == PartSize.SIZE_10) return "radar.target.custom10";
        if (f.definition.top() == PartSize.SIZE_10 && f.definition.bottom() == PartSize.SIZE_15) return "radar.target.custom1015";
        if (f.definition.top() == PartSize.SIZE_15 && f.definition.bottom() == PartSize.SIZE_15) return "radar.target.custom15";
        if (f.definition.top() == PartSize.SIZE_15 && f.definition.bottom() == PartSize.SIZE_20) return "radar.target.custom1520";
        if (f.definition.top() == PartSize.SIZE_20 && f.definition.bottom() == PartSize.SIZE_20) return "radar.target.custom20";
        return "radar.target.custom";
    }
    @Override public int getBlipLevel() { return switch (getUnlocalizedName()) {
        case "radar.target.custom10" -> IRadarDetectableNT.TIER10; case "radar.target.custom1015" -> IRadarDetectableNT.TIER10_15;
        case "radar.target.custom15" -> IRadarDetectableNT.TIER15; case "radar.target.custom1520" -> IRadarDetectableNT.TIER15_20;
        case "radar.target.custom20" -> IRadarDetectableNT.TIER20; default -> IRadarDetectableNT.TIER1; }; }
    @Override public List<ItemStack> getDebris() { return List.of(); }
    @Override public ItemStack getDebrisRareDrop() { return ItemStack.EMPTY; }
    @Override public ItemStack getMissileItemForInfo() { return new ItemStack(CustomMissileContent.MISSILE.get()); }
}
