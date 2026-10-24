package com.hbm.item.dye;

import com.hbm.item.FlatSourceItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** 一种固定颜色的化学染料；无关物品组件不会改变颜色身份。 */
public class ChemicalDyeItem extends Item implements FlatSourceItem {
    // 仅用于本物品实际显示文本，不参与物品注册或颜色匹配。
    private final String translationFamily;
    private final ChemicalDyeType type;

    public ChemicalDyeItem(ChemicalDyeType type) { this("chemical_dye", type, new Properties()); }

    protected ChemicalDyeItem(String translationFamily, ChemicalDyeType type, Properties properties) {
        super(properties);
        this.translationFamily = translationFamily;
        this.type = type;
    }

    public ChemicalDyeType type() { return type; }

    @Override public String getDescriptionId() { return "item.hbm." + translationFamily + "." + type.translationSuffix(); }
    @Override public Component getName(ItemStack stack) { return Component.translatable("item." + translationFamily + "." + type.translationSuffix() + ".name"); }

    public static int color(ItemStack stack, int pass) {
        return 0xff000000 | (pass == 1 ? ((ChemicalDyeItem) stack.getItem()).type.color : 0xffffff);
    }
}
