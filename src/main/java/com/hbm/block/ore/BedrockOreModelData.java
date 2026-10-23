package com.hbm.block.ore;

import net.neoforged.neoforge.client.model.data.ModelProperty;

/** Common-side snapshot keys; no client renderer classes are referenced by the block entity. */
public final class BedrockOreModelData {
    public static final ModelProperty<Integer> SHAPE = new ModelProperty<>();
    private BedrockOreModelData() { }
}
