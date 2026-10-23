package com.hbm.block.pipe;

import com.hbm.fluid.HbmFluids;
import com.hbm.item.fluid.FluidItemContent;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;

/** Original ItemFluidDuct replaces snow or the adjacent replaceable block. */
public final class TypedPipeItem extends Item {
    public TypedPipeItem() { super(new Properties()); }
    public List<ItemStack> creativeStacks() {
        var result = new ArrayList<ItemStack>(); var order = HbmFluids.getInNiceOrder();
        for (int i = 1; i < order.length; i++) if (!order[i].hasNoID()) result.add(FluidItemContent.stack(this, order[i]));
        return result;
    }
    @Override public Component getName(ItemStack stack) { return Component.translatable(getDescriptionId()).append(" ").append(FluidItemContent.type(stack).displayName()); }
    @Override public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel(); var position = context.getClickedPos();
        if (!level.getBlockState(position).is(Blocks.SNOW)) {
            position = position.relative(context.getClickedFace());
            if (!level.getBlockState(position).canBeReplaced()) return InteractionResult.FAIL;
        }
        var player = context.getPlayer();
        if (player != null && !player.mayUseItemAt(position, context.getClickedFace(), context.getItemInHand())) return InteractionResult.FAIL;
        var type = FluidItemContent.type(context.getItemInHand());
        context.getItemInHand().shrink(1);
        level.setBlock(position, PipeContent.BLOCK.get().defaultBlockState(), 3);
        if (level.getBlockEntity(position) instanceof NativePipeBlockEntity pipe) pipe.setType(type);
        level.playSound(player, position, PipeSounds.TYPE.getPlaceSound(), net.minecraft.sounds.SoundSource.BLOCKS, (PipeSounds.TYPE.getVolume() + 1F) / 2F, PipeSounds.TYPE.getPitch() * 0.8F);
        return InteractionResult.SUCCESS;
    }
}
