package com.hbm.entity.aircraft.c130;

import com.hbm.bomb.nuclear.LegacyVector;
import com.hbm.world.structure.loot.LegacyStructureLoot;
import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/** Actual C130 flight and source ItemPoolsC130 payloads. */
public final class EntityC130 extends EntityPlaneBase {
    public enum C130PayloadType { SUPPLIES, WEAPONS, A_FUCKING_FUEL_TRUCK }
    public static Consumer<EntityC130> CLIENT_TICK = ignored -> {};
    public static Consumer<EntityC130> CLIENT_REMOVE = ignored -> {};
    public C130PayloadType payload = C130PayloadType.SUPPLIES;
    public EntityC130(EntityType<?> type, Level level) { super(type, level); }
    @Override public void tick() {
        super.tick();
        if (level().isClientSide) CLIENT_TICK.accept(this);
        if (level() instanceof ServerLevel server && tickCount == getLifetime() / 2 && health > 0) {
            var crate = new EntityParachuteCrate(C130Content.PARACHUTE_CRATE.get(), level());
            var motion = getDeltaMovement();
            crate.setPos(getX() - motion.x * 7, getY() - 10, getZ() - motion.z * 7);
            if (payload == C130PayloadType.SUPPLIES)
                for (int i = 0; i < 5; i++) crate.items.add(LegacyStructureLoot.draw(server, crate.position(), "POOL_SUPPLIES", random));
            if (payload == C130PayloadType.WEAPONS) {
                int amount = 1 + random.nextInt(2);
                for (int i = 0; i < amount; i++) crate.items.add(LegacyStructureLoot.draw(server, crate.position(), "POOL_WEAPONS", random));
                for (int i = 0; i < 6; i++) crate.items.add(LegacyStructureLoot.draw(server, crate.position(), "POOL_AMMO", random));
            }
            // The source's third enum still drops an empty crate; it has no fuel-truck implementation.
            server.addFreshEntity(crate);
        }
    }
    public void fac(Level level, double x, double y, double z, C130PayloadType payload) {
        var vector = LegacyVector.createVectorHelper(level.random.nextDouble() - .5, 0, level.random.nextDouble() - .5).normalize();
        vector.xCoord *= 2; vector.zCoord *= 2;
        this.payload = payload;
        moveTo(x - vector.xCoord * 100, y + 100, z - vector.zCoord * 100, 0, 0);
        loadNeighboringChunks((int) (x / 16), (int) (z / 16));
        setDeltaMovement(vector.xCoord, 0, vector.zCoord);
        rotation();
    }
    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        int id = tag.getInt("payload");
        payload = C130PayloadType.values()[Math.abs(id % C130PayloadType.values().length)];
    }
    @Override protected void addAdditionalSaveData(CompoundTag tag) { super.addAdditionalSaveData(tag); tag.putInt("payload", payload.ordinal()); }
    @Override public void remove(RemovalReason reason) { if (level().isClientSide) CLIENT_REMOVE.accept(this); super.remove(reason); }
}
