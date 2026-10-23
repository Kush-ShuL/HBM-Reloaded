package com.hbm.world.structure.logic;

import com.hbm.bomb.conventional.TimedChargeBlock;
import com.hbm.exposure.HbmExposure;
import com.hbm.recipe.generic.GenericRecipeConfigCodec;
import com.hbm.world.structure.LegacyStructureStates;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/** Explicit old coordinate/direction/NBT adapters, not modern AABB normalization or replacement source content. */
final class LogicWorld {
    static final int[] DX = {0, 0, 0, 0, -1, 1, 0}, DY = {-1, 1, 0, 0, 0, 0, 0}, DZ = {0, 0, -1, 1, 0, 0, 0};
    static int orientation(int index) { return index >= 0 && index < 6 ? index : 6; }
    static int opposite(int index) { return switch (orientation(index)) { case 0 -> 1; case 1 -> 0; case 2 -> 3; case 3 -> 2; case 4 -> 5; case 5 -> 4; default -> 6; }; }
    static int rotateUp(int index) { return switch (orientation(index)) { case 2 -> 5; case 5 -> 3; case 3 -> 4; case 4 -> 2; default -> orientation(index); }; }
    static String blockId(String symbol) { return LogicDependencies.data().getAsJsonObject("blockIds").get(symbol).getAsString(); }
    static boolean blockReady(String symbol) { return BuiltInRegistries.BLOCK.containsKey(ResourceLocation.parse(blockId(symbol))); }
    static BlockState block(String symbol, int metadata) {
        String id = blockId(symbol);
        if (!blockReady(symbol)) throw new LogicDependencies.MissingDependencies(List.of("block " + symbol + " -> " + id));
        var block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(id));
        if (block instanceof TimedChargeBlock) return block.defaultBlockState().setValue(TimedChargeBlock.FACE, Direction.from3DDataValue(metadata));
        return LegacyStructureStates.resolve(id, metadata, 0);
    }
    static ItemStack stack(String symbol) { return new ItemStack(LogicDependencies.item(symbol)); }
    static ItemStack stack(String symbol, int metadata) {
        LogicDependencies.requireItems(symbol);
        return GenericRecipeConfigCodec.legacyStack(LogicDependencies.itemId(symbol), metadata, Map.of());
    }
    /** Old AxisAlignedBB.getBoundingBox did not reorder inverted endpoints. Expand before validating them. */
    static <T extends Entity> List<T> entities(ServerLevel level, Class<T> type, double x1, double y1, double z1,
                                              double x2, double y2, double z2, double ex, double ey, double ez) {
        double minX = x1 - ex, minY = y1 - ey, minZ = z1 - ez, maxX = x2 + ex, maxY = y2 + ey, maxZ = z2 + ez;
        if (minX > maxX || minY > maxY || minZ > maxZ) return List.of();
        return level.getEntitiesOfClass(type, new AABB(minX, minY, minZ, maxX, maxY, maxZ));
    }
    static List<Player> players(LogicRuntime tile, double radius) {
        var p = tile.getBlockPos();
        return entities(tile.server(), Player.class, p.getX(), p.getY(), p.getZ(), p.getX() + 1, p.getY() - 2, p.getZ() + 1, radius, radius, radius);
    }
    static void message(Player player, String key) {
        // Original commands address nearest actual player, not a fake command sender.
        player.displayClientMessage(Component.translatable(key), false);
    }
    static Player closest(LogicRuntime tile, double range) {
        var p = tile.getBlockPos(); return tile.server().getNearestPlayer(p.getX(), p.getY(), p.getZ(), range, false);
    }
    static boolean heavyHazmat(Player player) {
        String[][] sets = {{"hazmat_paa_helmet", "hazmat_paa_plate", "hazmat_paa_legs", "hazmat_paa_boots"},
            {"liquidator_helmet", "liquidator_plate", "liquidator_legs", "liquidator_boots"},
            {"euphemium_helmet", "euphemium_plate", "euphemium_legs", "euphemium_boots"},
            {"rpa_helmet", "rpa_plate", "rpa_legs", "rpa_boots"}, {"fau_helmet", "fau_plate", "fau_legs", "fau_boots"},
            {"dns_helmet", "dns_plate", "dns_legs", "dns_boots"}};
        var slots = new net.minecraft.world.entity.EquipmentSlot[]{net.minecraft.world.entity.EquipmentSlot.HEAD, net.minecraft.world.entity.EquipmentSlot.CHEST, net.minecraft.world.entity.EquipmentSlot.LEGS, net.minecraft.world.entity.EquipmentSlot.FEET};
        for (var set : sets) {
            boolean matches = true;
            for (int i = 0; i < slots.length; i++) if (!BuiltInRegistries.ITEM.getKey(player.getItemBySlot(slots[i]).getItem()).toString().equals("hbm:" + set[i])) { matches = false; break; }
            if (matches) return true;
        }
        return false;
    }
    private LogicWorld() {}
}
