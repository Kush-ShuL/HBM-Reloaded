package com.hbm.client.equipment.npcheadgear;

import com.hbm.equipment.npcheadgear.MiningHelmetItem;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/** Original DFS direct block-light writes, isolated to the real client light query and dirty sections. */
@EventBusSubscriber(modid="hbm", value=Dist.CLIENT)
public final class No9Light {
    private record Cell(int light, long expires) {}
    private record Snapshot(ClientLevel level, Map<Long, Cell> cells) {}
    private static volatile Snapshot visible = new Snapshot(null, Map.of());
    private static ClientLevel world;
    private static final Map<Long, Cell> lights = new HashMap<>();
    private static final Set<Long> breadcrumb = new HashSet<>(), dirty = new HashSet<>();
    private static Long lastCheck;

    public static int value(ClientLevel level, BlockPos pos, int nativeLight) {
        var snapshot = visible;
        if (snapshot.level != level) return nativeLight;
        Cell value = snapshot.cells.get(pos.asLong());
        return value == null ? nativeLight : Math.max(nativeLight, value.light);
    }
    private static void world(ClientLevel level) {
        if (world == level) return;
        world = level; lights.clear(); breadcrumb.clear(); dirty.clear(); lastCheck = null;
        visible = new Snapshot(level, Map.of());
    }
    public static void armorTick(Player player) {
        if (!(player.level() instanceof ClientLevel level) || level.getGameTime() % 2 != 0
                || !MiningHelmetItem.isOn(player.getItemBySlot(EquipmentSlot.HEAD))) return;
        world(level);
        checkLights(level);
        // Source updateLightByType completed expiry before reading saved light
        // for the new beam; never resurrect an expired value from the old snapshot.
        visible = new Snapshot(level, Map.copyOf(lights));
        // Source Library.rayTrace interpolation0, collider-only, no liquids, range50.
        Vec3 start = player.getEyePosition(0), end = start.add(player.getViewVector(0).scale(50));
        var hit = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        if (hit.getType() == HitResult.Type.BLOCK) {
            double distance = player.getEyePosition().distanceTo(hit.getLocation());
            int intensity = Math.min(15, (int) (25 - distance * 25 / 50F));
            var position = hit.getBlockPos().relative(hit.getDirection());
            lightUp(level, position.getX(), position.getY(), position.getZ(), intensity);
            breadcrumb.clear();
        }
        publish(level);
    }
    private static void lightUp(ClientLevel level, int x, int y, int z, int light) {
        if (light <= 0) return;
        var pos = new BlockPos(x, y, z); long key = pos.asLong();
        if (!breadcrumb.add(key)) return;
        if (level.getBlockState(pos).getLightBlock(level, pos) >= 15) return;
        int existing = level.getBrightness(LightLayer.BLOCK, pos);
        Cell previous = lights.get(key);
        if (previous != null) existing = Math.max(existing, previous.light);
        int updated = Math.min(15, Math.max(existing, light));
        lights.put(key, new Cell(updated, level.getGameTime() + 5));
        if (previous == null || previous.light != updated) dirty.add(SectionPos.asLong(pos));
        // Keep exact original recursive visit order and first-visited breadcrumb behavior.
        lightUp(level, x + 1, y, z, light - 1); lightUp(level, x - 1, y, z, light - 1);
        lightUp(level, x, y + 1, z, light - 1); lightUp(level, x, y - 1, z, light - 1);
        lightUp(level, x, y, z + 1, light - 1); lightUp(level, x, y, z - 1, light - 1);
    }
    private static void checkLights(ClientLevel level) {
        long now = level.getGameTime();
        var it = lights.entrySet().iterator();
        while (it.hasNext()) {
            var entry = it.next();
            if (now > entry.getValue().expires) { dirty.add(SectionPos.asLong(BlockPos.of(entry.getKey()))); it.remove(); }
        }
        lastCheck = now;
    }
    private static void publish(ClientLevel level) {
        visible = new Snapshot(level, Map.copyOf(lights));
        for (long section : dirty) level.setSectionDirtyWithNeighbors(SectionPos.x(section), SectionPos.y(section), SectionPos.z(section));
        dirty.clear();
    }
    @SubscribeEvent public static void tick(ClientTickEvent.Post event) {
        var level = Minecraft.getInstance().level;
        world(level);
        if (level != null && lastCheck != null && lastCheck < level.getGameTime() + 15) { checkLights(level); publish(level); }
    }
    private No9Light() {}
}
