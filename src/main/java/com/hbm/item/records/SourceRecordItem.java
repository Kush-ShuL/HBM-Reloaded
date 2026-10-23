package com.hbm.item.records;

import com.hbm.world.soyuz.GlassRecordItem;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;

/** Source ItemModRecord; native song/player/BE stores and plays the real disc, including the original creative consumption. */
public final class SourceRecordItem extends GlassRecordItem {
    public final String recordName;
    public SourceRecordItem(String name) { super(name); recordName = name; }
    @Override public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        var level = context.getLevel(); var pos = context.getClickedPos(); var state = level.getBlockState(pos);
        if (!state.is(Blocks.JUKEBOX) || state.getValue(JukeboxBlock.HAS_RECORD)) return InteractionResult.PASS;
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof JukeboxBlockEntity jukebox)) return InteractionResult.PASS;
        // Source always --stackSize, even creative. The actual native setter notifies HAS_RECORD and starts its song player.
        jukebox.setTheItem(stack.split(1));
        return InteractionResult.CONSUME;
    }
}
