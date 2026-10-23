package com.hbm.item.meteorbook.tool;

import com.hbm.bomb.nuclear.NuclearSpecialContent;
import java.util.Random;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

/** Original ItemBalefireMatch; intentionally does not inherit ordinary flint-and-steel ignition behavior. */
public final class BalefireMatchItem extends Item {
    private static final Random ITEM_RAND = new Random();
    public BalefireMatchItem() { super(new Properties().durability(256)); }
    @Override public Component getName(ItemStack stack) { return Component.translatable("item.balefire_and_steel.name"); }
    @Override public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel(); var player = context.getPlayer(); var face = context.getClickedFace();
        var target = context.getClickedPos().relative(face); var stack = context.getItemInHand();
        if (player == null || !player.mayUseItemAt(target, face, stack)) return InteractionResult.FAIL;
        if (level.isEmptyBlock(target)) {
            level.playSound(player, target.getX() + .5D, target.getY() + .5D, target.getZ() + .5D,
                SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1F, ITEM_RAND.nextFloat() * .4F + .8F);
            level.setBlock(target, NuclearSpecialContent.BALEFIRE.get().defaultBlockState(), 3);
        }
        // Source charges one use even for an occupied adjacent cell. Permission failure costs nothing.
        stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(context.getHand()));
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
