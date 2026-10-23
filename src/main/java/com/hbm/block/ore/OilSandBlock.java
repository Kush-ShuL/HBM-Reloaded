package com.hbm.block.ore;

import com.hbm.block.falling.FallingSpec;
import com.hbm.block.falling.LegacyFallingBlock;

/** The original oil sand is a falling sand-material block, with no special ore drops. */
public final class OilSandBlock extends LegacyFallingBlock {
    public OilSandBlock(OreSpec spec) {
        super(new FallingSpec(spec.id(), spec.originalClass(), spec.material(), spec.sound(),
                "ore_oil_sand_alt", spec.hardness(), spec.explosionResistance(), spec.requiresTool(), false));
    }
}
