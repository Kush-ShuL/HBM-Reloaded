package com.hbm.item.guidebook;

import com.hbm.hazard.HbmHazards;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

/** Source ItemGuideBook: one-stack subtyped book, source title tooltip and client-only reader. */
public final class GuideBookItem extends Item {
    public static Consumer<ItemStack> OPEN=stack->{};
    public GuideBookItem(){super(new Properties().stacksTo(1).component(HbmHazards.LEGACY_METADATA.get(),0));}
    public static int metadata(ItemStack stack){return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue());}
    public static GuideBooks.BookType type(ItemStack stack){return GuideBooks.BookType.getType(metadata(stack));}
    public ItemStack stack(GuideBooks.BookType type){var stack=new ItemStack(this);stack.set(HbmHazards.LEGACY_METADATA.get(),type.ordinal());return stack;}
    /** Original getSubItems excludes TEST0 and HADRON2, independently of requested creative tab. */
    public List<ItemStack> visibleVariants(){return List.of(stack(GuideBooks.BookType.RBMK),stack(GuideBooks.BookType.STARTER));}
    @Override public Component getName(ItemStack stack){return Component.translatable("item.book_guide.name");}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){lines.add(Component.literal(String.join(" ",Component.translatable(type(stack).title).getString().split("\\$"))));}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){var stack=player.getItemInHand(hand);if(level.isClientSide)OPEN.accept(stack.copy());return InteractionResultHolder.sidedSuccess(stack,level.isClientSide);}
}
