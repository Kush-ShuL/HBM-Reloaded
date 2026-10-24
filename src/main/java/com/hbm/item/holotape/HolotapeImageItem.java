package com.hbm.item.holotape;

import java.util.*;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

/** Source ItemHoloTape/ItemHolotapeImage:18 immutable single-stack images, original client-only narrative reader. */
public final class HolotapeImageItem extends Item {
    public static Consumer<InteractionHand> OPEN=hand->{};
    private final HolotapeImageType image;
    public HolotapeImageItem(HolotapeImageType image){super(new Properties().stacksTo(1));this.image=Objects.requireNonNull(image);}
    public HolotapeImageType image(){return image;}
    public static HolotapeImageType type(ItemStack stack){if(stack.getItem() instanceof HolotapeImageItem item)return item.image;throw new IllegalArgumentException("Not a source holotape image");}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){if(level.isClientSide)OPEN.accept(hand);return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand),level.isClientSide);}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){var type=type(stack);lines.add(Component.translatable("tooltip.hbm.holotape.band").append(Component.translatable("holotape.hbm.band."+type.colorName).withStyle(type.colorCode)));lines.add(Component.translatable("tooltip.hbm.holotape.label").append(type.label));}
}
