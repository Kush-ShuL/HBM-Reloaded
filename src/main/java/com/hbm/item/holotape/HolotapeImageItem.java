package com.hbm.item.holotape;

import com.hbm.hazard.HbmHazards;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

/** Source ItemHoloTape/ItemHolotapeImage:18 actual single-stack metadata variants, client-only narrative reader. */
public final class HolotapeImageItem extends Item {
    public static Consumer<InteractionHand> OPEN=hand->{};
    public HolotapeImageItem(){super(new Properties().stacksTo(1).component(HbmHazards.LEGACY_METADATA.get(),0));}
    public static int metadata(ItemStack stack){return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue());}
    public static HolotapeImageType type(ItemStack stack){return HolotapeImageType.from(metadata(stack));}
    public ItemStack stack(HolotapeImageType type){var stack=new ItemStack(this);stack.set(HbmHazards.LEGACY_METADATA.get(),type.ordinal());return stack;}
    public List<ItemStack> variants(){return Arrays.stream(HolotapeImageType.values()).map(this::stack).toList();}
    @Override public Component getName(ItemStack stack){return Component.translatable("item.holotape_image.name");}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){if(level.isClientSide)OPEN.accept(hand);return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand),level.isClientSide);}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){var type=type(stack);lines.add(Component.translatable("tooltip.hbm.holotape.band").append(Component.translatable("holotape.hbm.band."+type.colorName).withStyle(type.colorCode)));lines.add(Component.translatable("tooltip.hbm.holotape.label").append(type.label));}
}
