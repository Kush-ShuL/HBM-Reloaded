package com.hbm.block.toaster;

import java.util.Map;
import java.util.Objects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.BlockItem;

/** All three original ItemBlockBase variants deliberately share the one source name. */
public final class ToasterItem extends BlockItem {
    public enum Kind {
        IRON(0), STEEL(1), WOOD(2);
        private final int type;
        Kind(int type) { this.type = type; }
        public int type() { return type; }
    }
    private final Kind kind;
    public ToasterItem(ToasterBlock block, Kind kind) { super(block, new Properties()); this.kind = Objects.requireNonNull(kind); }
    public Kind kind() { return kind; }
    @Override public void registerBlocks(Map<Block, Item> mapping, Item item) { if (kind == Kind.IRON) super.registerBlocks(mapping, item); }
    @Override protected BlockState getPlacementState(BlockPlaceContext context) {
        var state = super.getPlacementState(context);
        return state == null ? null : state.setValue(ToasterBlock.META, kind.type() * 4 + state.getValue(ToasterBlock.META) % 4);
    }
}
