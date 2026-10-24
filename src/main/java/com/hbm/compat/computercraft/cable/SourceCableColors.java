package com.hbm.compat.computercraft.cable;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** 电缆颜色代码与色值保持独立，仅用实际原生染料标签识别物品。 */
public enum SourceCableColors {
    BLACK(0x444444, "black"), RED(0xB3312C, "red"), GREEN(0x339911, "green"), BROWN(0x51301A, "brown"),
    BLUE(0x6666FF, "blue"), PURPLE(0x7B2FBE, "purple"), CYAN(0x66FFFF, "cyan"), LIGHTGRAY(0xABABAB, "light_gray"),
    GRAY(0x666666, "gray"), PINK(0xD88198, "pink"), LIME(0x66FF66, "lime"), YELLOW(0xFFFF66, "yellow"),
    LIGHTBLUE(0xAAAAFF, "light_blue"), MAGENTA(0xC354CD, "magenta"), ORANGE(0xEB8844, "orange"), WHITE(0xF0F0F0, "white"), NONE(0, null);

    public static final TagKey<Item> DYES = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dyes"));
    public final int rgb;
    // 无色状态没有叶子标签；它仍保留独立的电缆重置代码。
    private final TagKey<Item> dyeTag;

    SourceCableColors(int rgb, String color) {
        this.rgb = rgb;
        this.dyeTag = color == null ? null : TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dyes/" + color));
    }

    public static SourceCableColors fromInt(int rgb) {
        for (var value : values()) if (value.rgb == rgb) return value;
        return NONE;
    }

    public static SourceCableColors fromDye(ItemStack stack) {
        for (var value : values()) if (value.dyeTag != null && stack.is(value.dyeTag)) return value;
        return NONE;
    }
}
