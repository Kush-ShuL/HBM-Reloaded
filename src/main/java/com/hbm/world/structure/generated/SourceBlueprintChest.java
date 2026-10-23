package com.hbm.world.structure.generated;

import com.hbm.config.WorldgenControls;
import com.hbm.world.structure.generated.compat.Block;
import com.hbm.world.structure.generated.compat.Blocks;
import com.hbm.world.structure.generated.compat.World;
import com.hbm.world.structure.loot.LegacyStructureLoot;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

/** Complete HbmWorldGen.genBlueprintChest body, planned through the existing native per-chunk pieces. */
public final class SourceBlueprintChest {
    private static final Block OBSIDIAN = Block.source("minecraft:obsidian");
    public static void generate(World world, Random random, int i, int j, Set<String> selected) {
        if (!WorldgenControls.feature("natural_events")) return;
        var control = WorldgenControls.rule("natural_events", "blueprint_chest");
        if (!control.enabled()) return;
        int boundsX = control.integer("gridX", 5000), boundsZ = control.integer("gridZ", 5000);
        if (boundsX <= 0 || boundsZ <= 0) return;
        if (Math.abs(i) < 100 && Math.abs(j) < 100) return;
        // This draw precedes the grid test in the source, including off-grid chunks.
        if (random.nextInt(20) < 10) return;
        int cX = Math.abs(i) % boundsX, cZ = Math.abs(j) % boundsZ;
        if (cX >= 0 && cX < 16 && cZ >= 0 && cZ < 16) {
            int x = i + 8, z = j + 8;
            int y = world.getHeightValue(x, z) - random.nextInt(2);
            var dependencies = GeneratedDependencies.forNames(List.of("old:BlueprintChest"));
            dependencies.requireRegistryAndHooks();
            if (!LegacyStructureLoot.ready(world.lookup(), "POOL_BLUEPRINTS"))
                throw new IllegalStateException("Incomplete original blueprint-chest loot pool POOL_BLUEPRINTS");
            world.setBlock(x, y, z, Blocks.chest);
            for (int a = x - 1; a <= x + 1; a++) {
                for (int b = y - 1; b <= y + 1; b++) {
                    for (int c = z - 1; c <= z + 1; c++) {
                        if (a != x || b != y || c != z) world.setBlock(a, b, c, OBSIDIAN);
                    }
                }
            }
            if (world.getTileEntity(x, y, z) instanceof ChestBlockEntity chest) {
                GeneratedLoot.fill(world, random, GeneratedLoot.pool("POOL_BLUEPRINTS"), chest, 50);
            }
            selected.add("old:BlueprintChest");
        }
    }

    private SourceBlueprintChest() { }
}
