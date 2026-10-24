package com.hbm.item.guidebook;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/** A fixed source book identity; the reader keeps all original pages and formatting. */
public final class GuideBookItem extends Item {
    public static Consumer<ItemStack> OPEN = stack -> { };
    private final GuideBooks.BookType bookType;

    public GuideBookItem(GuideBooks.BookType bookType) {
        super(new Properties().stacksTo(1));
        this.bookType = bookType;
    }
    public static GuideBooks.BookType type(ItemStack stack) {
        return ((GuideBookItem) stack.getItem()).bookType;
    }
    @Override public Component getName(ItemStack stack) {
        return Component.translatable("item.book_guide.name");
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        lines.add(Component.literal(String.join(" ", Component.translatable(bookType.title).getString().split("\\$"))));
    }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if (level.isClientSide) OPEN.accept(stack.copy());
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
