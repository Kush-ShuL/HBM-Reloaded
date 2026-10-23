package com.hbm.world.structure.generated;

import com.hbm.config.WorldgenControls;
import com.hbm.world.soyuz.*;
import com.hbm.world.structure.generated.compat.Block;
import com.hbm.world.structure.generated.compat.World;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.world.item.ItemStack;

/** Complete original HbmWorldGen341..360 beach capsule, not an additional orbital-module generator. */
public final class SourceBeachCapsule {
    public static void generate(World world, Random random, int i, int j, int frequency, Set<String> selected) {
        if (!WorldgenControls.feature("soyuz_capsules")) return;
        var control = WorldgenControls.rule("soyuz_capsules", "landing_capsule");
        frequency = control.chance(frequency);
        if (!control.enabled() || frequency <= 0 || !world.getBiomeGenForCoords(i, j).biome().equals("minecraft:beach")
                || random.nextInt(frequency) != 0) return;
        int x = i + random.nextInt(16), z = j + random.nextInt(16);
        int y = world.getHeightValue(x, z) - 4;
        if (!world.getBlock(x, y + 1, z).canPlaceTorchOnTop(world, x, y + 1, z)) return;
        var dependencies = GeneratedDependencies.forNames(List.of("old:SoyuzCapsule"));
        dependencies.requireRegistryAndHooks();
        var state = SoyuzCapsulesContent.CAPSULE.get().defaultBlockState().setValue(SoyuzCapsuleBlock.META, 3);
        world.setBlock(x, y, z, Block.observed(state), 3, 2);
        if (world.getTileEntity(x, y, z) instanceof SoyuzCapsuleBlockEntity capsule)
            capsule.setItem(random.nextInt(capsule.getContainerSize()), new ItemStack(SoyuzCapsulesContent.GLASS_RECORD.get()));
        selected.add("old:SoyuzCapsule");
    }
    private SourceBeachCapsule() { }
}
