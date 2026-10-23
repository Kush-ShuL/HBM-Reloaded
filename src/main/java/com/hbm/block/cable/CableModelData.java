package com.hbm.block.cable;

import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelProperty;

/** Immutable snapshots consumed by the client model, never a live block entity. */
public final class CableModelData {
    public static final ModelProperty<BlockState> CONNECTION_STATE = new ModelProperty<>();
    public static final ModelProperty<BlockState> PAINT = new ModelProperty<>();
    public static final ModelProperty<int[]> CONNECTED_TEXTURES = new ModelProperty<>();
    private CableModelData() {}
}
